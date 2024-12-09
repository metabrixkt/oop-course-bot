package dev.metabrix.urfu.oopbot.interaction;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Взаимодействие с ботом в сообщении.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface MessageInteraction extends UserInteraction, ChatInteraction {
    /**
     * Возвращает сообщение, которое вызвало это взаимодействие.
     *
     * @return сообщение
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull Message getMessage();

    @Override
    default @NotNull User getTelegramUser() {
        return this.getMessage().getFrom();
    }

    @Override
    default @NotNull Chat getTelegramChat() {
        return this.getMessage().getChat();
    }
}
