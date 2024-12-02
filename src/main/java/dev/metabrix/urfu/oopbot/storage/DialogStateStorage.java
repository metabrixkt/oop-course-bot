package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.storage.model.dialog.DialogState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранилище состояний диалогов.
 * 
 * @since 1.1.0
 * @author metabrix
 */
public interface DialogStateStorage {
    /**
     * Возвращает состояние диалога для пользователя в чате.
     *
     * @param userId ID пользователя в хранилище
     * @param chatId ID чата в хранилище
     * @return состояние диалога или {@code null}, если состояние диалога не установлено
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable DialogState get(int userId, int chatId);

    /**
     * Устанавливает состояние диалога для пользователя в чате.
     *
     * @param userId ID пользователя в хранилище
     * @param chatId ID чата в хранилище
     * @param dialogState состояние диалога
     * @since 1.1.0
     * @author metabrix
     */
    void set(
        int userId,
        int chatId,
        @NotNull DialogState dialogState
    );

    /**
     * Удаляет состояние диалога для пользователя в чате.
     *
     * @param userId ID пользователя в хранилище
     * @param chatId ID чата в хранилище
     * @since 1.1.0
     * @author metabrix
     */
    void delete(int userId, int chatId);
}
