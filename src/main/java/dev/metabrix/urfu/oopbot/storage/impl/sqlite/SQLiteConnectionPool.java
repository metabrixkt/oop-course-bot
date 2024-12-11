package dev.metabrix.urfu.oopbot.storage.impl.sqlite;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.storage.impl.sql.AbstractSQLConnectionPool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public class SQLiteConnectionPool extends AbstractSQLConnectionPool {
    private final @NotNull String connectionUrl;

    SQLiteConnectionPool(@NotNull BotConfiguration.DataStorage.SQLiteConfiguration configuration) throws SQLException {
        super(configuration.poolSize());
        this.connectionUrl = "jdbc:sqlite:%s".formatted(configuration.databaseFilePath().toAbsolutePath().toString());

        this.initialize();
    }

    @Override
    protected @NotNull Connection newConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionUrl);
    }
}
