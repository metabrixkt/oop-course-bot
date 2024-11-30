package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранилище пользователей.
 * 
 * @since 1.1.0
 * @author metabrix
 */
public interface UserStorage {
    /**
     * Создаёт пользователя.
     *
     * @param telegramId ID пользователя в Telegram
     * @param telegramUsername имя пользователя в Telegram
     * @return созданный пользователь
     * @throws DuplicateObjectException если пользователь с таким ID в Telegram уже
     *         существует в хранилище
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull User create(
        long telegramId,
        @NotNull String telegramUsername
    ) throws DuplicateObjectException;

    /**
     * Возвращает пользователя по ID.
     *
     * @param id ID пользователя в хранилище
     * @return пользователь по ID или {@code null}, если такого пользователя нет
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable User getById(int id);

    /**
     * Возвращает пользователя по ID в Telegram.
     *
     * @param telegramId ID пользователя в Telegram
     * @return пользователь по ID в Telegram или {@code null}, если такого пользователя нет
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable User getByTelegramId(long telegramId);

    /**
     * Обновляет имя пользователя в Telegram по ID.
     *
     * @param id ID пользователя
     * @param telegramUsername имя пользователя в Telegram
     * @since 1.1.0
     * @author metabrix
     */
    void updateTelegramUsername(int id, @Nullable String telegramUsername);

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя
     * @since 1.1.0
     * @author metabrix
     */
    void delete(int id);
}
