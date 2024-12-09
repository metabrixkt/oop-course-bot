package dev.metabrix.urfu.oopbot.telegram;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.ChatStorage;
import dev.metabrix.urfu.oopbot.storage.UserStorage;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.storage.model.dialog.DialogState;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import java.time.Instant;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Контекст обработки события нового сообщения в Telegram.
 *
 * @since 1.1.0
 * @author metabrix
 */
public final class MessageUpdateContext {
    private final @NotNull MessageInteraction interaction;

    private @Nullable User cachedUser;
    private @Nullable Chat cachedChat;
    private @Nullable DialogState cachedDialogState;

    /**
     * Создаёт новый контекст.
     *
     * @param interaction взаимодействие с ботом
     * @since 1.1.0
     * @author metabrix
     */
    public MessageUpdateContext(@NotNull MessageInteraction interaction) {
        this.interaction = interaction;
    }

    /**
     * Получает пользователя, обновляет его данные, если он существует, и возвращает его.
     *
     * @return пользователь или {@code null}, если пользователя не существует
     * @since 1.1.0
     * @author metabrix
     */
    public @Nullable User getAndUpdateUserIfExists() {
        if (this.cachedUser != null) return this.cachedUser;

        User user = this.interaction.getUserIfExists();
        if (user == null) return null;

        org.telegram.telegrambots.meta.api.objects.User telegramUser = this.interaction.getTelegramUser();
        String newTelegramUsername = telegramUser.getUserName();
        if (Objects.equals(user.telegramUsername(), newTelegramUsername)) {
            this.cachedUser = user;
            return user;
        }

        UserStorage users = this.interaction.getStorage().users();
        users.updateTelegramUsername(user.id(), newTelegramUsername);
        user = new User(
            user.id(),
            user.telegramId(), newTelegramUsername,
            user.joinedAt(), Instant.now()
        );
        this.cachedUser = user;
        return user;
    }

    /**
     * Создаёт нового пользователя, если его не существует, или обновляет данные
     * существующего, возвращая его.
     *
     * @return пользователь
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull User createOrUpdateUser() {
        if (this.cachedUser != null) return this.cachedUser;

        org.telegram.telegrambots.meta.api.objects.User telegramUser = this.interaction.getTelegramUser();
        UserStorage users = this.interaction.getStorage().users();

        User user = this.interaction.getUserIfExists();
        if (user != null) {
            String newTelegramUsername = telegramUser.getUserName();
            if (Objects.equals(user.telegramUsername(), newTelegramUsername)) {
                this.cachedUser = user;
                return user;
            }

            users.updateTelegramUsername(user.id(), newTelegramUsername);
            user = new User(
                user.id(),
                user.telegramId(), newTelegramUsername,
                user.joinedAt(), Instant.now()
            );
            this.cachedUser = user;
            return user;
        }

        try {
            user = users.create(telegramUser.getId(), telegramUser.getUserName());
            this.cachedUser = user;
            return user;
        } catch (DuplicateObjectException ex) {
            // got a race condition: the user was created on our way here
            user = users.getByTelegramId(telegramUser.getId());
            if (user != null) {
                String newTelegramUsername = telegramUser.getUserName();
                if (Objects.equals(user.telegramUsername(), newTelegramUsername)) {
                    this.cachedUser = user;
                    return user;
                }

                users.updateTelegramUsername(user.id(), newTelegramUsername);
                user = new User(
                    user.id(),
                    user.telegramId(), newTelegramUsername,
                    user.joinedAt(), Instant.now()
                );
                this.cachedUser = user;
                return user;
            } else {
                // woah, what? that's not a race condition (probably)
                throw new IllegalStateException(
                    "User creation failed with DuplicateObjectException, but the user for Telegram ID " + telegramUser.getId() + " doesn't exist",
                    ex
                );
            }
        }
    }

    /**
     * Возвращает чат, в котором было отправлено сообщение, если он существует.
     *
     * @return чат или {@code null}, если чата не существует
     * @since 1.1.0
     * @author metabrix
     */
    public @Nullable Chat getChatIfExists() {
        if (this.cachedChat != null) return this.cachedChat;

        Chat chat = this.interaction.getChatIfExists();
        if (chat == null) return null;

        this.cachedChat = chat;
        return chat;
    }

    /**
     * Создаёт чат, если его не существует, возвращая его.
     *
     * @return чат
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull Chat createChatIfNotExists() {
        if (this.cachedChat != null) return this.cachedChat;

        Chat chat = this.getChatIfExists();
        if (chat != null) {
            this.cachedChat = chat;
            return chat;
        }

        org.telegram.telegrambots.meta.api.objects.Chat telegramChat = this.interaction.getTelegramChat();
        ChatStorage chats = this.interaction.getStorage().chats();
        try {
            chat = chats.create(telegramChat.getId(), this.createOrUpdateUser().id());
            this.cachedChat = chat;
            return chat;
        } catch (DuplicateObjectException ex) {
            // got a race condition: the chat was created on our way here
            chat = chats.getByTelegramId(telegramChat.getId());
            if (chat != null) {
                this.cachedChat = chat;
                return chat;
            } else {
                // woah, what? that's not a race condition (probably)
                throw new IllegalStateException(
                    "Chat creation failed with DuplicateObjectException, but the chat for Telegram ID " + telegramChat.getId() + " doesn't exist",
                    ex
                );
            }
        }
    }

    /**
     * Возвращает состояние диалога.
     *
     * @return состояние диалога или {@code null}, если его нет
     * @since 1.1.0
     * @author metabrix
     */
    public @Nullable DialogState getDialogState() {
        if (this.cachedDialogState != null) return this.cachedDialogState;

        User user = this.getAndUpdateUserIfExists();
        if (user == null) return null;

        Chat chat = this.getChatIfExists();
        if (chat == null) return null;

        DialogState dialogState = this.interaction.getStorage().dialogStates().get(user.id(), chat.id());
        this.cachedDialogState = dialogState;
        return dialogState;
    }
}
