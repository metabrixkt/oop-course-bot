package dev.metabrix.urfu.oopbot.storage.impl.sql;

import java.sql.Connection;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

public abstract class AbstractSQLConnectionPool implements SQLConnectionPool {
    private final @NotNull Connection[] pool;
    private boolean poolInitialized = false;
    private int poolCursor = 0; // -1 if pool is closed

    public AbstractSQLConnectionPool(int poolSize) {
        this.pool = new Connection[poolSize];
    }

    protected void initialize() throws SQLException {
        this.poolInitialized = true;

        for (int i = 0; i < this.pool.length; i++) {
            this.pool[i] = this.newConnection();
        }
    }

    @Override
    public int poolSize() {
        return this.pool.length;
    }

    protected abstract @NotNull Connection newConnection() throws SQLException;

    @Override
    public @NotNull Connection getConnection() throws SQLException {
        synchronized (this.pool) {
            checkState(this.poolInitialized, "Connection pool is not yet initialized");
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
        if (!this.poolInitialized) return;

        synchronized (this.pool) {
            for (Connection connection : this.pool) {
                connection.close();
            }
            this.poolCursor = -1;
        }
    }
}
