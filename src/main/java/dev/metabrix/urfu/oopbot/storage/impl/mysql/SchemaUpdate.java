package dev.metabrix.urfu.oopbot.storage.impl.mysql;

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

enum SchemaUpdate {
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
                "id INT NOT NULL AUTO_INCREMENT, " +
                "telegram_id BIGINT NOT NULL, " +
                "telegram_username VARCHAR(32) NOT NULL, " +
                "joined_at TIMESTAMP NOT NULL, " +
                "updated_at TIMESTAMP DEFAULT NULL, " +
                "PRIMARY KEY (id), " +
                "UNIQUE (telegram_id), " +
                "INDEX (telegram_username)" +
                ")"
        )) {
            s.executeUpdate();
        }

        logger.info("Creating table {}", tables.chats());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.chats() + " (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "telegram_id BIGINT NOT NULL, " +
                "installed_by_id INT NOT NULL, " +
                "installed_at TIMESTAMP NOT NULL, " +
                "updated_by_id INT DEFAULT NULL, " +
                "updated_at TIMESTAMP DEFAULT NULL, " +
                "PRIMARY KEY (id), " +
                "UNIQUE (telegram_id), " +
                "CONSTRAINT fk_chats_installed_by_id__id FOREIGN KEY (installed_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_chats_updated_by_id__id FOREIGN KEY (updated_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }

        logger.info("Creating table {}", tables.tasks());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.tasks() + " (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "chat_id INT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT DEFAULT NULL, " +
                "created_by_id INT NOT NULL, " +
                "created_at TIMESTAMP NOT NULL, " +
                "updated_by_id INT DEFAULT NULL, " +
                "updated_at TIMESTAMP DEFAULT NULL, " +
                "PRIMARY KEY (id), " +
                "FULLTEXT (name), " +
                "INDEX (created_by_id), " +
                "INDEX (created_at), " +
                "INDEX (updated_at), " +
                "CONSTRAINT fk_tasks_chat_id__id FOREIGN KEY (chat_id) REFERENCES " + tables.chats() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_tasks_created_by_id__id FOREIGN KEY (created_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_tasks_updated_by_id__id FOREIGN KEY (updated_by_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
        )) {
            s.executeUpdate();
        }
    }),
    INTRODUCE_DIALOG_STATES((connection, tables, logger) -> {
        logger.info("Creating table {}", tables.dialogStates());
        try (PreparedStatement s = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + tables.dialogStates() + " (" +
                "user_id INT NOT NULL, " +
                "chat_id INT NOT NULL, " +
                "type VARCHAR(64) NOT NULL, " +
                "data JSON NOT NULL, " +
                "PRIMARY KEY (user_id, chat_id), " +
                "INDEX (type), " +
                "CONSTRAINT fk_dialog_states_user_id__id FOREIGN KEY (user_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_dialog_states_chat_id__id FOREIGN KEY (chat_id) REFERENCES " + tables.chats() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
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
                "id INT NOT NULL AUTO_INCREMENT, " +
                "task_id INT NOT NULL, " +
                "author_id INT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "posted_at TIMESTAMP NOT NULL, " +
                "updated_at TIMESTAMP DEFAULT NULL, " +
                "PRIMARY KEY (id), " +
                "INDEX (task_id), " +
                "INDEX (author_id), " +
                "INDEX (posted_at), " +
                "CONSTRAINT fk_tasks_comments_task_id__id FOREIGN KEY (task_id) REFERENCES " + tables.tasks() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT, " +
                "CONSTRAINT fk_tasks_comments_author_id__id FOREIGN KEY (author_id) REFERENCES " + tables.users() + " (id) ON DELETE RESTRICT ON UPDATE RESTRICT" +
                ")"
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

    SchemaUpdate(@NotNull Updater updater) {
        this.updater = updater;
    }

    public void update(@NotNull Connection connection, @NotNull Tables tables) throws Exception {
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
            @NotNull Tables tables,
            @NotNull Logger logger
        ) throws SQLException;
    }
}
