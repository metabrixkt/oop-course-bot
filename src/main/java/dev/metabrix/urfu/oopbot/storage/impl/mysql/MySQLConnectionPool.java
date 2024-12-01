package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.storage.impl.sql.AbstractSQLConnectionPool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public class MySQLConnectionPool extends AbstractSQLConnectionPool {
    private final @NotNull String connectionUrl;
    private final @NotNull String username;
    private final @NotNull String password;

    public MySQLConnectionPool(@NotNull BotConfiguration.DataStorage.MySQLConfiguration configuration) throws SQLException {
        super(configuration.poolSize());
        this.connectionUrl = "jdbc:mysql://%s:%d/%s".formatted(configuration.host(), configuration.port(), configuration.database());
        this.username = configuration.username();
        this.password = configuration.password();

        this.initialize();
    }

    @Override
    protected @NotNull Connection newConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionUrl, this.username, this.password);
    }
}
