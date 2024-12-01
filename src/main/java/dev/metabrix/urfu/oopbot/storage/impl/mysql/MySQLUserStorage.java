package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.storage.UserStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import dev.metabrix.urfu.oopbot.util.exception.StorageException;
import java.sql.*;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MySQLUserStorage implements UserStorage {
    private final @NotNull SQLConnectionPool pool;
    private final @NotNull Tables tables;

    MySQLUserStorage(@NotNull SQLConnectionPool pool, @NotNull Tables tables) {
        this.pool = pool;
        this.tables = tables;
    }

    @Override
    public @NotNull User create(long telegramId, @NotNull String telegramUsername) throws DuplicateObjectException {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "INSERT INTO " + this.tables.users() + " (telegram_id, telegram_username, joined_at) VALUES (?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            Instant joinedAt = Instant.now();

            s.setLong(1, telegramId);
            s.setString(2, telegramUsername);
            s.setTimestamp(3, Timestamp.from(joinedAt));
            s.executeUpdate();

            try (ResultSet rs = s.getGeneratedKeys()) {
                rs.next();
                return new User(
                    rs.getInt(1),
                    telegramId,
                    telegramUsername,
                    joinedAt,
                    null
                );
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateObjectException(User.class, "telegramId=" + telegramId, ex);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public @Nullable User getById(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.users() + " WHERE id = ? LIMIT 1"
        )) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new User(
                        rs.getInt("id"),
                        rs.getLong("telegram_id"),
                        rs.getString("telegram_username"),
                        rs.getTimestamp("joined_at").toInstant(),
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
    public @Nullable User getByTelegramId(long telegramId) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.users() + " WHERE telegram_id = ? LIMIT 1"
        )) {
            s.setLong(1, telegramId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new User(
                        rs.getInt("id"),
                        rs.getLong("telegram_id"),
                        rs.getString("telegram_username"),
                        rs.getTimestamp("joined_at").toInstant(),
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
    public void updateTelegramUsername(int id, @Nullable String telegramUsername) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "UPDATE " + this.tables.users() + " SET telegram_username = ?, updated_at = ? WHERE id = ?"
        )) {
            Instant updatedAt = Instant.now();

            s.setString(1, telegramUsername);
            s.setTimestamp(2, Timestamp.from(updatedAt));
            s.setInt(3, id);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "DELETE FROM " + this.tables.users() + " WHERE id = ?"
        )) {
            s.setInt(1, id);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }
}