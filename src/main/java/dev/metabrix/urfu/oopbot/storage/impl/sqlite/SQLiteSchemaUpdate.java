package dev.metabrix.urfu.oopbot.storage.impl.sqlite;

import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLTables;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

enum SQLiteSchemaUpdate {
    INITIAL((connection, tables, logger) -> {
        logger.info("Creating table {}", tables.version());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.version() + " (" +
                "version INT NOT NULL, " +
                "time TIMESTAMP NOT NULL, " +
                "PRIMARY KEY (version)" +
                ")"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "INSERT INTO " + tables.version() + " (version, time) VALUES (?, ?)"
        )) {
            s.setInt(1, 0);
            s.setTimestamp(2, Timestamp.from(Instant.now()));

            int updated = s.executeUpdate();
            checkState(
                updated == 1,
                "Failed to add the current version row into the newly created " + tables.version() + " table"
            );
        }

        logger.info("Creating table {}", tables.users());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.users() + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "telegram_id INTEGER NOT NULL UNIQUE, " +
                "telegram_username TEXT NOT NULL, " +
                "joined_at DATETIME NOT NULL, " +
                "updated_at DATETIME DEFAULT NULL" +
                ")"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_users_telegram_username ON " + tables.users() + " (telegram_username)"
        )) {
            s.executeUpdate();
        }

        logger.info("Creating table {}", tables.chats());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.chats() + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "telegram_id INTEGER NOT NULL UNIQUE, " +
                "installed_by_id INTEGER NOT NULL, " +
                "installed_at DATETIME NOT NULL, " +
                "updated_by_id INTEGER DEFAULT NULL, " +
                "updated_at DATETIME DEFAULT NULL, " +
                "CONSTRAINT fk_chats_installed_by_id__id FOREIGN KEY (installed_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_chats_updated_by_id__id FOREIGN KEY (updated_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }

        logger.info("Creating table {}", tables.tasks());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.tasks() + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chat_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT DEFAULT NULL, " +
                "created_by_id INTEGER NOT NULL, " +
                "created_at DATETIME NOT NULL, " +
                "updated_by_id INTEGER DEFAULT NULL, " +
                "updated_at DATETIME DEFAULT NULL, " +
                "FOREIGN KEY (chat_id) REFERENCES " + tables.chats() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "FOREIGN KEY (created_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "FOREIGN KEY (updated_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_created_by_id ON " + tables.tasks() + " (created_by_id)"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON " + tables.tasks() + " (created_at)"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_updated_at ON " + tables.tasks() + " (updated_at)"
        )) {
            s.executeUpdate();
        }
    }),
    INTRODUCE_DIALOG_STATES((connection, tables, logger) -> {
        logger.info("Creating table {}", tables.dialogStates());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.dialogStates() + " (" +
                "user_id INTEGER NOT NULL, " +
                "chat_id INTEGER NOT NULL, " +
                "type VARCHAR(64) NOT NULL, " +
                "data TEXT NOT NULL, " +
                "PRIMARY KEY (user_id, chat_id), " +
                "FOREIGN KEY (user_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "FOREIGN KEY (chat_id) REFERENCES " + tables.chats() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_dialog_states_type ON " + tables.dialogStates() + " (type)"
        )) {
            s.executeUpdate();
        }

        try (PreparedStatement s = connection.prepareStatement(
            "INSERT INTO " + tables.version() + " (version, time) VALUES (?, ?)"
        )) {
            s.setInt(1, 1);
            s.setTimestamp(2, Timestamp.from(Instant.now()));
            s.executeUpdate();
        }
    }),
    ADD_TASK_COMMENTS((connection, tables, logger) -> {
        logger.info("Creating table {}", tables.tasksComments());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.tasksComments() + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER NOT NULL, " +
                "author_id INTEGER NOT NULL, " +
                "content TEXT NOT NULL, " +
                "posted_at DATETIME NOT NULL, " +
                "updated_at DATETIME DEFAULT NULL, " +
                "FOREIGN KEY (task_id) REFERENCES " + tables.tasks() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "FOREIGN KEY (author_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_comments_task_id ON " + tables.tasksComments() + " (task_id)"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_comments_author_id ON " + tables.tasksComments() + " (author_id)"
        )) {
            s.executeUpdate();
        }
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE INDEX IF NOT EXISTS idx_tasks_comments_posted_at ON " + tables.tasksComments() + " (posted_at)"
        )) {
            s.executeUpdate();
        }

        try (PreparedStatement s = connection.prepareStatement(
            "INSERT INTO " + tables.version() + " (version, time) VALUES (?, ?)"
        )) {
            s.setInt(1, 2);
            s.setTimestamp(2, Timestamp.from(Instant.now()));
            s.executeUpdate();
        }
    }),
    ;

    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull Updater updater;

    SQLiteSchemaUpdate(@NotNull Updater updater) {
        this.updater = updater;
    }

    public void update(@NotNull Connection connection, @NotNull SQLTables tables) throws Exception {
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            this.updater.update(connection, tables, LOGGER);
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public @NotNull String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @FunctionalInterface
    private interface Updater {
        void update(
            @NotNull Connection connection,
            @NotNull SQLTables tables,
            @NotNull Logger logger
        ) throws SQLException;
    }
}
