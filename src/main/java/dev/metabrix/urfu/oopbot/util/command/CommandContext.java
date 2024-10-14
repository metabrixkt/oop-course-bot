package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.BotApplication;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Контекст исполнения команды.
 *
 * @since 1.0.0
 * @author metabrix
 */
public interface CommandContext {
    /**
     * Возвращает приложение бота.
     *
     * @return приложение
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull BotApplication getApplication();

    /**
     * Возвращает объект события Telegram API.
     *
     * @return событие API
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull Update getRawUpdate();

    /**
     * Возвращает сообщение, которое вызвало исполнение команды.
     *
     * @return сообщение
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull Message getMessage() {
        return this.getRawUpdate().getMessage();
    }

    /**
     * Возвращает отправителя команды.
     *
     * @return отправитель
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull User getSender() {
        return this.getMessage().getFrom();
    }

    /**
     * Возвращает чат, в котором была вызвана команда.
     *
     * @return чат
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull Chat getChat() {
        return this.getMessage().getChat();
    }

    /**
     * Возвращает командную строку.
     *
     * @return командная строка
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull CommandInput getCommandInput();
}
