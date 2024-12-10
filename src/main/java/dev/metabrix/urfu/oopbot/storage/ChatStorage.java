package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранилище чатов.
 * 
 * @since 1.1.0
 * @author metabrix
 */
public interface ChatStorage {
    /**
     * Создаёт чат.
     *
     * @param telegramId ID чата в Telegram
     * @param installedById ID пользователя в Telegram, начавшего работу с ботом в чате
     * @return созданный чат
     * @throws DuplicateObjectException если чат с таким ID в Telegram уже
     *         существует в хранилище
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull Chat create(long telegramId, int installedById) throws DuplicateObjectException;

    /**
     * Возвращает чат по ID.
     *
     * @param id ID чата в хранилище
     * @return чат по ID или {@code null}, если такого чата нет
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable Chat getById(int id);

    /**
     * Возвращает чат по ID в Telegram.
     *
     * @param telegramId ID чата в Telegram
     * @return чат по ID в Telegram или {@code null}, если такого чата нет
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable Chat getByTelegramId(long telegramId);

    /**
     * Удаляет чат по ID.
     *
     * @param id ID чата
     * @return {@code true}, если чат был удален, или {@code false}, если такого чата нет
     * @since 1.1.0
     * @author metabrix
     */
    boolean delete(int id);
}
