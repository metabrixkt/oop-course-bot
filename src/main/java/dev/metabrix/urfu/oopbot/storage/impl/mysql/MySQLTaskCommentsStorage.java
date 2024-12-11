package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.storage.TaskCommentsStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLTables;
import dev.metabrix.urfu.oopbot.storage.model.TaskComment;
import dev.metabrix.urfu.oopbot.util.exception.StorageException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MySQLTaskCommentsStorage implements TaskCommentsStorage {
    private final @NotNull SQLConnectionPool pool;
    private final @NotNull SQLTables tables;

    MySQLTaskCommentsStorage(@NotNull SQLConnectionPool pool, @NotNull SQLTables tables) {
        this.pool = pool;
        this.tables = tables;
    }

    @Override
    public @NotNull TaskComment create(int taskId, int authorId, @NotNull String content) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "INSERT INTO " + this.tables.tasksComments() + " (task_id, author_id, content, posted_at) VALUES (?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            Instant postedAt = Instant.now();
            s.setInt(1, taskId);
            s.setInt(2, authorId);
            s.setString(3, content);
            s.setTimestamp(4, Timestamp.from(postedAt));
            s.executeUpdate();

            try (ResultSet rs = s.getGeneratedKeys()) {
                rs.next();
                return new TaskComment(
                    rs.getInt(1),
                    taskId,
                    authorId,
                    content,
                    postedAt,
                    null
                );
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public @Nullable TaskComment getById(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.tasksComments() + " WHERE id = ? LIMIT 1"
        )) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new TaskComment(
                        rs.getInt("id"),
                        rs.getInt("task_id"),
                        rs.getInt("author_id"),
                        rs.getString("content"),
                        rs.getTimestamp("posted_at").toInstant(),
                        updatedAt == null ? null : updatedAt.toInstant()
                    );
                }
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }

        return null;
    }

    @Override
    public @NotNull List<@NotNull TaskComment> getByTaskId(
        int taskId,
        int limit, int offset,
        boolean newerFirst
    ) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.tasksComments() +
                " WHERE task_id = ?" +
                " ORDER BY posted_at " + (newerFirst ? "DESC" : "ASC") +
                " LIMIT ? OFFSET ?"
        )) {
            s.setInt(1, taskId);
            s.setInt(2, limit);
            s.setInt(3, offset);
            try (ResultSet rs = s.executeQuery()) {
                List<TaskComment> comments = new ArrayList<>();
                while (rs.next()) {
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    comments.add(new TaskComment(
                        rs.getInt("id"),
                        rs.getInt("task_id"),
                        rs.getInt("author_id"),
                        rs.getString("content"),
                        rs.getTimestamp("posted_at").toInstant(),
                        updatedAt == null ? null : updatedAt.toInstant()
                    ));
                }
                return comments;
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public int countByTaskId(int taskId) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT COUNT(*) FROM " + this.tables.tasksComments() + " WHERE task_id = ?"
        )) {
            s.setInt(1, taskId);
            try (ResultSet rs = s.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void updateContent(int id, @NotNull String content) {
        TaskComment.validateContent(content);
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "UPDATE " + this.tables.tasksComments() + " SET content = ?, updated_at = ? WHERE id = ?"
        )) {
            s.setString(1, content);
            s.setTimestamp(2, Timestamp.from(Instant.now()));
            s.setInt(3, id);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public boolean delete(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "DELETE FROM " + this.tables.tasksComments() + " WHERE id = ?"
        )) {
            s.setInt(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }
}
