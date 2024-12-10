package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.storage.ChatStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import dev.metabrix.urfu.oopbot.util.exception.StorageException;
import java.sql.*;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MySQLChatStorage implements ChatStorage {
    private final @NotNull SQLConnectionPool pool;
    private final @NotNull Tables tables;

    MySQLChatStorage(@NotNull SQLConnectionPool pool, @NotNull Tables tables) {
        this.pool = pool;
        this.tables = tables;
    }

    @Override
    public @NotNull Chat create(long telegramId, int installedById) throws DuplicateObjectException {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "INSERT INTO " + this.tables.chats() + " (telegram_id, installed_by_id, installed_at) VALUES (?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            Instant installedAt = Instant.now();
            s.setLong(1, telegramId);
            s.setInt(2, installedById);
            s.setTimestamp(3, Timestamp.from(installedAt));
            s.executeUpdate();

            try (ResultSet rs = s.getGeneratedKeys()) {
                rs.next();
                return new Chat(
                    rs.getInt(1),
                    telegramId,
                    installedById,
                    installedAt,
                    null,
                    null
                );
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateObjectException(Chat.class, "telegramId=" + telegramId, ex);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public @Nullable Chat getById(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.chats() + " WHERE id = ? LIMIT 1"
        )) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    int updatedById = rs.getInt("updated_by_id");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new Chat(
                        rs.getInt("id"),
                        rs.getLong("telegram_id"),
                        rs.getInt("installed_by_id"),
                        rs.getTimestamp("installed_at").toInstant(),
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
    public @Nullable Chat getByTelegramId(long telegramId) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.chats() + " WHERE telegram_id = ? LIMIT 1"
        )) {
            s.setLong(1, telegramId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    int updatedById = rs.getInt("updated_by_id");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    return new Chat(
                        rs.getInt("id"),
                        rs.getLong("telegram_id"),
                        rs.getInt("installed_by_id"),
                        rs.getTimestamp("installed_at").toInstant(),
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
    public boolean delete(int id) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "DELETE FROM " + this.tables.chats() + " WHERE id = ?"
        )) {
            s.setInt(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }
}
