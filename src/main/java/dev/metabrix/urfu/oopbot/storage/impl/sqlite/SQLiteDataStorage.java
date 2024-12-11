package dev.metabrix.urfu.oopbot.storage.impl.sqlite;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.storage.*;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLTables;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.sqlite.SQLiteException;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

public class SQLiteDataStorage implements DataStorage {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull SQLTables tables;
    private final @NotNull SQLConnectionPool pool;

    private final @NotNull UserStorage users;
    private final @NotNull ChatStorage chats;
    private final @NotNull TaskStorage tasks;
    private final @NotNull DialogStateStorage dialogStates;

    public SQLiteDataStorage(@NotNull BotConfiguration.DataStorage.SQLiteConfiguration configuration) throws Exception {
        this.tables = new SQLTables(configuration.tablePrefix());
        this.pool = new SQLiteConnectionPool(configuration);
        this.updateSchema();

        this.users = new SQLiteUserStorage(this.pool, this.tables);
        this.chats = new SQLiteChatStorage(this.pool, this.tables);
        this.tasks = new SQLiteTaskStorage(this.pool, this.tables);
        this.dialogStates = new SQLiteDialogStateStorage(this.pool, this.tables);
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
                        "Unable to initialize SQLite storage, invalid storage schema version found: " + currentVersion
                    );
                }
            }
        } catch (SQLiteException ignored) {
        }

        SQLiteSchemaUpdate[] updates = SQLiteSchemaUpdate.values();
        final int latestVersion = updates.length - 1;
        if (currentVersion > latestVersion) {
            throw new UnsupportedOperationException(
                "Unable to initialize SQLite storage, detected unknown newer schema version: " + currentVersion
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
                String currentVersionString = currentVersion == -1 ? "<none>" : String.valueOf(currentVersion);
                throw new Exception(
                    "Unable to initialize SQLite storage, failed to update the storage schema from " + currentVersionString + " to " + nextVersion,
                    ex
                );
            }
            currentVersion = nextVersion;
        }
    }

    @Override
    public @NotNull BotConfiguration.DataStorage.Type<SQLiteDataStorage, BotConfiguration.DataStorage.SQLiteConfiguration> getType() {
        return BotConfiguration.DataStorage.Type.SQLITE;
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
    public @NotNull DialogStateStorage dialogStates() {
        checkNotClosed();
        return this.dialogStates;
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