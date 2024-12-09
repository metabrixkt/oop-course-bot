package dev.metabrix.urfu.oopbot.interaction;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Взаимодействие с ботом при нажатии на кнопку у сообщения.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface CallbackQueryInteraction extends UserInteraction {
    /**
     * Возвращает нажатие на кнопку.
     *
     * @return запрос ответа на нажатие на кнопку
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull CallbackQuery getQuery();

    @Override
    default @NotNull User getTelegramUser() {
        return this.getQuery().getFrom();
    }
}
