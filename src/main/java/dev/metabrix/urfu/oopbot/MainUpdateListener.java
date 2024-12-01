package dev.metabrix.urfu.oopbot;

import dev.metabrix.urfu.oopbot.storage.ChatStorage;
import dev.metabrix.urfu.oopbot.storage.UserStorage;
import dev.metabrix.urfu.oopbot.telegram.UpdateListener;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.command.*;
import dev.metabrix.urfu.oopbot.util.exception.DuplicateObjectException;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной обработчик событий Telegram API.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class MainUpdateListener implements UpdateListener {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private static final @NotNull Pattern COMMAND_REGEX = Pattern.compile("^/[^/]+(?:\\s.+)?$");

    private final @NotNull BotApplication application;

    public MainUpdateListener(@NotNull BotApplication application) {
        this.application = application;
    }

    @Override
    public void handleMessage(@NotNull Update update) throws TelegramApiException {
        // ignore forwarded messages
        if (update.getMessage().getForwardSenderName() != null) return;

        Message message = update.getMessage();
        String text = message.getText();
        if (text == null || !COMMAND_REGEX.matcher(text).matches()) {
            if (message.getChat().isUserChat()) this.respondUnknownCommand(message);
            return;
        }

        CommandContext ctx = new CommandContextImpl(this.application, update);
        CommandInput input = ctx.getCommandInput();

        String commandLabel = input.readToken();
        BotCommand command = BotCommand.byName(commandLabel);
        if (command == null) {
            this.respondUnknownCommand(message);
            return;
        }

        dev.metabrix.urfu.oopbot.storage.model.User user = this.createOrUpdateUser(message.getFrom());
        this.createChatIfNotExists(user, message.getChat());

        CommandHandler handler = command.getHandler();
        handler.executeFuture(ctx)
            .exceptionally(throwable -> {
                LOGGER.error("Failed to process command: {}", ctx.getCommandInput().getRawInput(), throwable);
                return CommandExecutionResult.INTERNAL_ERROR;
            })
            .thenAcceptAsync(result -> {
                try {
                    switch (result) {
                        case SUCCESS -> {}
                        case INTERNAL_ERROR -> this.application.getBot().execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text(Emoji.X + " Во время выполнения команды произошла внутренняя ошибка, попробуйте ещё раз.")
                            .build());
                        case INVALID_SYNTAX -> this.application.getBot().execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text(Emoji.X + " Неверный синтаксис команды :(")
                            .build());
                        case UNKNOWN_COMMAND -> this.respondUnknownCommand(message);
                    }
                } catch (TelegramApiException e) {
                    LOGGER.error("Failed to process command execution result", e);
                }
            });
    }

    private @NotNull dev.metabrix.urfu.oopbot.storage.model.User createOrUpdateUser(@NotNull User telegramUser) {
        UserStorage users = this.application.getStorage().users();

        dev.metabrix.urfu.oopbot.storage.model.User user = users.getByTelegramId(telegramUser.getId());
        if (user != null) {
            String newUsername = telegramUser.getUserName();
            if (Objects.equals(user.telegramUsername(), newUsername)) return user;

            users.updateTelegramUsername(user.id(), newUsername);
            return new dev.metabrix.urfu.oopbot.storage.model.User(
                user.id(),
                user.telegramId(),
                newUsername,
                user.joinedAt(),
                Instant.now()
            );
        }

        try {
            return users.create(telegramUser.getId(), telegramUser.getUserName());
        } catch (DuplicateObjectException e) {
            // got a race condition: the user was created on our way here
            user = users.getByTelegramId(telegramUser.getId());
            if (user != null) {
                String newUsername = telegramUser.getUserName();
                if (Objects.equals(user.telegramUsername(), newUsername)) return user;

                users.updateTelegramUsername(user.id(), newUsername);
                return new dev.metabrix.urfu.oopbot.storage.model.User(
                    user.id(),
                    user.telegramId(),
                    newUsername,
                    user.joinedAt(),
                    Instant.now()
                );
            } else {
                // woah, what? that's not a race condition (probably)
                throw new IllegalStateException(
                    "User creation failed with DuplicateObjectException, but the user for Telegram ID " + telegramUser.getId() + " doesn't exist",
                    e
                );
            }
        }
    }

    private void createChatIfNotExists(@NotNull dev.metabrix.urfu.oopbot.storage.model.User user, @NotNull Chat telegramChat) {
        ChatStorage chats = this.application.getStorage().chats();
        if (chats.getByTelegramId(telegramChat.getId()) != null) return;

        try {
            chats.create(telegramChat.getId(), user.id());
        } catch (DuplicateObjectException ignored) {
        }
    }

    private void respondUnknownCommand(@NotNull Message message) throws TelegramApiException {
        this.application.getBot().execute(SendMessage.builder()
            .chatId(message.getChatId())
            .text(Emoji.X + " Неизвестная команда :(")
            .build());
    }
}
