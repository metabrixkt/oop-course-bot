package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import dev.metabrix.urfu.oopbot.storage.DialogStateStorage;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLConnectionPool;
import dev.metabrix.urfu.oopbot.storage.impl.sql.SQLTables;
import dev.metabrix.urfu.oopbot.storage.model.dialog.DialogState;
import dev.metabrix.urfu.oopbot.storage.model.dialog.DialogStateType;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.exception.StorageException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public class MySQLDialogStateStorage implements DialogStateStorage {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull SQLConnectionPool pool;
    private final @NotNull SQLTables tables;

    MySQLDialogStateStorage(@NotNull SQLConnectionPool pool, @NotNull SQLTables tables) {
        this.pool = pool;
        this.tables = tables;
    }

    @Override
    public @Nullable DialogState get(int userId, int chatId) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "SELECT * FROM " + this.tables.dialogStates() + " WHERE user_id = ? AND chat_id = ?"
        )) {
            s.setInt(1, userId);
            s.setInt(2, chatId);

            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    DialogStateType type = DialogStateType.byId(rs.getString("type"));
                    if (type == null) {
                        LOGGER.warn(
                            "Encountered unknown dialog state type: {} for user ID {} and chat ID {}",
                            rs.getString("type"), userId, chatId
                        );
                        return null;
                    }

                    JSONObject json = new JSONObject(rs.getString("data"));
                    return type.deserializeFromJson(json);
                } else {
                    return null;
                }
            }
        } catch (SQLException | JSONException ex) {
            // JSONException is here, because the data is stored in a JSON column and malformed data cannot be there,
            // so JSONException here would be an unexpected scenario
            throw new StorageException(ex);
        }
    }

    @Override
    public void set(int userId, int chatId, @NotNull DialogState dialogState) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "INSERT INTO " + this.tables.dialogStates() + " (user_id, chat_id, type, data) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE type = ?, data = ?"
        )) {
            String typeId = dialogState.type().getId();
            String data = dialogState.toJson().toString();
            s.setInt(1, userId);
            s.setInt(2, chatId);
            s.setString(3, typeId);
            s.setString(4, data);
            s.setString(5, typeId);
            s.setString(6, data);
            s.executeUpdate();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }

    @Override
    public boolean delete(int userId, int chatId) {
        try (PreparedStatement s = this.pool.getConnection().prepareStatement(
            "DELETE FROM " + this.tables.dialogStates() + " WHERE user_id = ? AND chat_id = ?"
        )) {
            s.setInt(1, userId);
            s.setInt(2, chatId);
            return s.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        }
    }
}
