package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.DataStorage;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.User;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Контекст исполнения команды.
 *
 * @since 1.0.0
 * @author metabrix
 */
public interface CommandContext {
    /**
     * Возвращает взаимодействие с ботом.
     *
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull MessageInteraction getInteraction();

    /**
     * Возвращает хранилище данных.
     *
     * @return хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull DataStorage getStorage() {
        return this.getInteraction().getStorage();
    }

    /**
     * Возвращает сообщение, которое вызвало исполнение команды.
     *
     * @return сообщение
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull Message getMessage() {
        return this.getInteraction().getMessage();
    }

    /**
     * Возвращает чат, в котором была вызвана команда, в виде объекта Telegram API.
     *
     * @return чат
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull org.telegram.telegrambots.meta.api.objects.Chat getTelegramChat() {
        return this.getMessage().getChat();
    }

    /**
     * Возвращает отправителя команды в виде объекта Telegram API.
     *
     * @return отправитель
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull org.telegram.telegrambots.meta.api.objects.User getTelegramSender() {
        return this.getMessage().getFrom();
    }

    /**
     * Возвращает чат, в котором была вызвана команда, в виде объекта {@link Chat}, если он есть в хранилище данных.
     *
     * @return чат или {@code null}, если его нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @Nullable Chat getChatIfExists() {
        return this.getInteraction().getChatIfExists();
    }

    /**
     * Возвращает чат, в котором была вызвана команда, в виде объекта {@link Chat}.
     * <p>
     * Этот метод ожидает, что чат был добавлен в хранилище данных до передачи управления в обработчик команды.
     *
     * @return чат
     * @throws NoSuchElementException если чата нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull Chat getChat() {
        return this.getInteraction().getChat();
    }

    /**
     * Возвращает отправителя команды в виде объекта {@link User}, если он есть в хранилище данных.
     *
     * @return пользователь или {@code null}, если его нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @Nullable User getSenderIfExists() {
        return this.getInteraction().getUserIfExists();
    }

    /**
     * Возвращает отправителя команды в виде объекта {@link User}.
     * <p>
     * Этот метод ожидает, что пользователь был добавлен в хранилище данных до передачи
     * управления в обработчик команды.
     *
     * @return пользователь
     * @throws NoSuchElementException если пользователя нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull User getSender() {
        return this.getInteraction().getUser();
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
