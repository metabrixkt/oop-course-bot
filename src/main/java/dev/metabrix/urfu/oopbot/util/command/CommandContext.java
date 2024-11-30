package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.storage.DataStorage;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.User;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

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
     * Возвращает хранилище данных.
     *
     * @return хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull DataStorage getStorage() {
        return this.getApplication().getStorage();
    }

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
        org.telegram.telegrambots.meta.api.objects.Chat telegramChat = this.getTelegramChat();
        return this.getStorage().chats().getByTelegramId(telegramChat.getId());
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
        Chat chat = this.getChatIfExists();
        if (chat == null) throw new NoSuchElementException("Chat " + this.getTelegramChat() + " not found in data storage");
        return chat;
    }

    /**
     * Возвращает отправителя команды в виде объекта {@link User}, если он есть в хранилище данных.
     *
     * @return пользователь или {@code null}, если его нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @Nullable User getSenderIfExists() {
        org.telegram.telegrambots.meta.api.objects.User telegramUser = this.getTelegramSender();
        return this.getStorage().users().getByTelegramId(telegramUser.getId());
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
        User user = this.getSenderIfExists();
        if (user == null) throw new NoSuchElementException("User " + this.getTelegramSender() + " not found in data storage");
        return user;
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
