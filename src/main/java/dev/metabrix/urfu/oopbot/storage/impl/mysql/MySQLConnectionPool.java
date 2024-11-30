package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

public class MySQLConnectionPool implements SQLConnectionPool {
    private final @NotNull String connectionUrl;
    private final @NotNull String username;
    private final @NotNull String password;

    private final @NotNull Connection[] pool;
    private int poolCursor = 0; // -1 if pool is closed

    public MySQLConnectionPool(@NotNull BotConfiguration.DataStorage.MySQLConfiguration configuration) throws SQLException {
        this.connectionUrl = "jdbc:mysql://%s:%d/%s".formatted(configuration.host(), configuration.port(), configuration.database());
        this.username = configuration.username();
        this.password = configuration.password();
        this.pool = new Connection[configuration.poolSize()];
        for (int i = 0; i < this.pool.length; i++) {
            this.pool[i] = this.newConnection();
        }
    }

    @Override
    public int poolSize() {
        return this.pool.length;
    }

    private @NotNull Connection newConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionUrl, this.username, this.password);
    }

    @Override
    public @NotNull Connection getConnection() throws SQLException {
        synchronized (this.pool) {
            checkState(!this.isClosed(), "Connection pool is closed");

            SQLException lastException = null;
            assert this.pool.length != 0 : "Pool size must be positive";
            for (int i = 0; i < this.pool.length; i++) {
                Connection connection = this.pool[(this.poolCursor + i) % this.pool.length];
                if (!connection.isValid(3)) {
                    try {
                        connection = this.newConnection();
                        this.poolCursor = (this.poolCursor + i) % this.pool.length;
                        this.pool[(this.poolCursor + i) % this.pool.length] = connection;
                        return connection;
                    } catch (SQLException ex) {
                        lastException = ex;
                    }
                } else {
                    this.poolCursor = (this.poolCursor + i) % this.pool.length;
                    return connection;
                }
            }
            throw lastException;
        }
    }

    @Override
    public boolean isClosed() {
        return this.poolCursor == -1;
    }

    @Override
    public void close() throws Exception {
        synchronized (this.pool) {
            for (Connection connection : this.pool) {
                connection.close();
            }
            this.poolCursor = -1;
        }
    }
}
