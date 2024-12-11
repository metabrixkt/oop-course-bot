package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.storage.TaskCommentsStorage;
import dev.metabrix.urfu.oopbot.storage.TaskStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLTables;
import dev.metabrix.urfu.oopbot.storage.model.Task;
import dev.metabrix.urfu.oopbot.util.exception.StorageException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

public class MySQLTaskStorage implements TaskStorage {
    private final @NotNull SQLConnectionPool pool;
    private final @NotNull SQLTables tables;

    private final @NotNull TaskCommentsStorage comments;

    MySQLTaskStorage(@NotNull SQLConnectionPool pool, @NotNull SQLTables tables) {
        this.pool = pool;
        this.tables = tables;

        this.comments = new MySQLTaskCommentsStorage(pool, tables);
    }

    @Override
    public @NotNull TaskCommentsStorage comments() {
        checkState(!this.pool.isClosed(), "Storage is closed");
        return this.comments;
    }

    @Override
    public @NotNull Task create(int chatId, @NotNull String name, @Nullable String description, int createdById) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "INSERT INTO " + this.tables.tasks() + " (chat_id, name, description, created_by_id, created_at) VALUES (?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            Instant createdAt = Instant.now();
            s.setInt(1, chatId);
            s.setString(2, name);
            s.setString(3, description);
            s.setInt(4, createdById);
            s.setTimestamp(5, Timestamp.from(createdAt));
            s.executeUpdate();

            try (ResultSet rs = s.getGeneratedKeys()) {
                rs.next();
                return new Task(
                    rs.getInt(1),
                    chatId,
                    name,
                    description,
                    createdById,
                    createdAt,
                    null,
                    null
                );
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public @Nullable Task getById(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.tasks() + " WHERE id = ? LIMIT 1"
        )) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    int updatedById = rs.getInt("updated_by_id");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new Task(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("created_by_id"),
                        rs.getTimestamp("created_at").toInstant(),
                        updatedById == 0 ? null : updatedById,
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
    public @NotNull List<@NotNull Task> searchTasks(
        int limit, int offset,
        @NotNull Sort sort, boolean ascending,
        @Nullable Integer chatId, @Nullable Integer createdById
    ) {
        List<String> filters = new ArrayList<>();
        if (chatId != null) filters.add("chat_id = ?");
        if (createdById != null) filters.add("created_by_id = ?");
        String where = filters.isEmpty() ? "" : " WHERE " + String.join(" AND ", filters);

        StringBuilder orderBy = new StringBuilder(" ORDER BY ");
        if (sort.fallback() == null) {
            orderBy.append(sort.name().toLowerCase(Locale.ROOT));
        } else {
            orderBy.append("COALESCE(");
            do {
                orderBy.append(sort.name().toLowerCase(Locale.ROOT)).append(", ");
                sort = sort.fallback();
            } while (sort != null);
            orderBy.delete(orderBy.length() - 2, orderBy.length());
            orderBy.append(")");
        }
        orderBy.append(" ").append(ascending ? "ASC" : "DESC");

        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.tasks() + where + orderBy + " LIMIT ? OFFSET ?"
        )) {
            int parameterIndex = 1;
            if (chatId != null) s.setInt(parameterIndex++, chatId);
            if (createdById != null) s.setInt(parameterIndex++, createdById);

            s.setInt(parameterIndex++, limit);
            s.setInt(parameterIndex++, offset);

            try (ResultSet rs = s.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    int updatedById = rs.getInt("updated_by_id");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("created_by_id"),
                        rs.getTimestamp("created_at").toInstant(),
                        updatedById == 0 ? null : updatedById,
                        updatedAt == null ? null : updatedAt.toInstant()
                    ));
                }
                return tasks;
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public int countTasks(@Nullable Integer chatId, @Nullable Integer createdById) {
        List<String> filters = new ArrayList<>();
        if (chatId != null) filters.add("chat_id = ?");
        if (createdById != null) filters.add("created_by_id = ?");
        String where = filters.isEmpty() ? "" : " WHERE " + String.join(" AND ", filters);

        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT COUNT(*) FROM " + this.tables.tasks() + where
        )) {
            int parameterIndex = 1;
            if (chatId != null) s.setInt(parameterIndex++, chatId);
            if (createdById != null) s.setInt(parameterIndex++, createdById);
            try (ResultSet rs = s.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void updateName(int id, @NotNull String newName, int updatedById) {
        Task.validateName(newName);
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "UPDATE " + this.tables.tasks() + " SET name = ?, updated_by_id = ?, updated_at = ? WHERE id = ?"
        )) {
            s.setString(1, newName);
            s.setInt(2, updatedById);
            s.setTimestamp(3, Timestamp.from(Instant.now()));
            s.setInt(4, id);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void updateDescription(int id, @Nullable String newDescription, int updatedById) {
        Task.validateDescription(newDescription);
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "UPDATE " + this.tables.tasks() + " SET description = ?, updated_by_id = ?, updated_at = ? WHERE id = ?"
        )) {
            s.setString(1, newDescription);
            s.setInt(2, updatedById);
            s.setTimestamp(3, Timestamp.from(Instant.now()));
            s.setInt(4, id);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public boolean delete(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "DELETE FROM " + this.tables.tasks() + " WHERE id = ?"
        )) {
            s.setInt(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }
}
