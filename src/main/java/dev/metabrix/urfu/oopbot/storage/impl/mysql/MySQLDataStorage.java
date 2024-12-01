package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.storage.ChatStorage;
import dev.metabrix.urfu.oopbot.storage.DataStorage;
import dev.metabrix.urfu.oopbot.storage.TaskStorage;
import dev.metabrix.urfu.oopbot.storage.UserStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

public class MySQLDataStorage implements DataStorage {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull Tables tables;
    private final @NotNull SQLConnectionPool pool;

    private final @NotNull UserStorage users;
    private final @NotNull ChatStorage chats;
    private final @NotNull TaskStorage tasks;

    public MySQLDataStorage(@NotNull BotConfiguration.DataStorage.MySQLConfiguration configuration) throws Exception {
        this.tables = new Tables(configuration.tablePrefix());
        this.pool = new MySQLConnectionPool(configuration);
        this.updateSchema();

        this.users = new MySQLUserStorage(this.pool, this.tables);
        this.chats = new MySQLChatStorage(this.pool, this.tables);
        this.tasks = new MySQLTaskStorage(this.pool, this.tables);
    }

    private void updateSchema() throws Exception {
        int currentVersion = -1;
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.version() + " ORDER BY `time` DESC LIMIT 1"
        )) {
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    currentVersion = rs.getInt("version");
                    checkState(
                        currentVersion >= 0,
                        "Unable to initialize MySQL storage, invalid storage schema version found: " + currentVersion
                    );
                }
            } catch (SQLSyntaxErrorException ignored) {
            }
        }

        SchemaUpdate[] updates = SchemaUpdate.values();
        final int latestVersion = updates.length - 1;
        if (currentVersion > latestVersion) {
            throw new UnsupportedOperationException(
                "Unable to initialize MySQL storage, detected unknown newer schema version: " + currentVersion
            );
        } else if (currentVersion == latestVersion) {
            LOGGER.info("Using latest schema version {} ({}), no updates needed", currentVersion, updates[currentVersion].toString());
            return;
        } else {
            if (currentVersion == -1) {
                LOGGER.info("No storage schema detected, creating table structure");
            } else {
                LOGGER.info("Current storage schema version is {}, updating to {}", currentVersion, latestVersion);
            }
        }

        while (currentVersion != latestVersion) {
            int nextVersion = currentVersion + 1;
            LOGGER.info("Updating schema version to {} ({})...", nextVersion, updates[nextVersion].toString());
            try {
                updates[nextVersion].update(this.pool.getConnection(), this.tables);
            } catch (Exception ex) {
                throw new Exception(
                    "Unable to initialize MySQL storage, failed to update the storage schema from " + currentVersion + " to " + nextVersion,
                    ex
                );
            }
            currentVersion = nextVersion;
        }
    }

    @Override
    public @NotNull BotConfiguration.DataStorage.Type<MySQLDataStorage, BotConfiguration.DataStorage.MySQLConfiguration> getType() {
        return BotConfiguration.DataStorage.Type.MYSQL;
    }

    @Override
    public @NotNull UserStorage users() {
        checkNotClosed();
        return this.users;
    }

    @Override
    public @NotNull ChatStorage chats() {
        checkNotClosed();
        return this.chats;
    }

    @Override
    public @NotNull TaskStorage tasks() {
        checkNotClosed();
        return this.tasks;
    }

    @Override
    public boolean isClosed() {
        return this.pool.isClosed();
    }

    @Override
    public void close() throws Exception {
        this.pool.close();
    }
    
    void checkNotClosed() {
        checkState(!this.isClosed(), "Storage is closed");
    }
}
