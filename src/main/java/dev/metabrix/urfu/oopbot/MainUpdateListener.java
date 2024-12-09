package dev.metabrix.urfu.oopbot;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.storage.model.dialog.DialogState;
import dev.metabrix.urfu.oopbot.telegram.MessageUpdateContext;
import dev.metabrix.urfu.oopbot.telegram.UpdateListener;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.command.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной обработчик событий Telegram API.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class MainUpdateListener implements UpdateListener {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private static final @NotNull Pattern COMMAND_REGEX = Pattern.compile("^/[^/]+(?:@[A-Za-z0-9_]+)?(?:\\s.+)?$");

    private final @NotNull BotApplication application;

    public MainUpdateListener(@NotNull BotApplication application) {
        this.application = application;
    }

    @Override
    public void handleMessage(@NotNull MessageInteraction interaction) throws TelegramApiException {
        // ignore forwarded messages
        if (interaction.getMessage().getForwardSenderName() != null) return;

        MessageUpdateContext updateContext = new MessageUpdateContext(interaction);

        Message message = interaction.getMessage();
        String text = message.getText();
        if (text == null || !COMMAND_REGEX.matcher(text).matches()) {
            DialogState dialogState = updateContext.getDialogState();
            if (dialogState == null) {
                if (message.getChat().isUserChat()) {
                    this.respondUnknownCommand(interaction);
                }
            } else {
                dialogState.handleMessage(interaction);
            }
            return;
        }

        User user = updateContext.createOrUpdateUser();
        Chat chat = updateContext.createChatIfNotExists();

        // remove any existing dialog state - we're processing commands now
        this.application.getStorage().dialogStates().delete(user.id(), chat.id());

        CommandContext ctx = new CommandContextImpl(interaction);
        CommandInput input = ctx.getCommandInput();

        String commandLabel = input.readToken();
        String[] tagSplitParts = commandLabel.split("@");
        if (tagSplitParts.length > 1) {
            if (tagSplitParts[tagSplitParts.length - 1].equalsIgnoreCase(this.application.getBot().getBotUsername())) {
                commandLabel = commandLabel.substring(0, commandLabel.length() - this.application.getBot().getBotUsername().length() - 1);
            } else {
                // command name has a bot tag, and it's not us
                return;
            }
        }

        BotCommand command = BotCommand.byName(commandLabel);
        if (command == null) {
            this.respondUnknownCommand(interaction);
            return;
        }


        CommandHandler handler = command.getHandler();
        CompletableFuture<CommandExecutionResult> future;
        try {
            future = handler.executeFuture(ctx);
        } catch (Exception ex) {
            future = CompletableFuture.failedFuture(ex);
        }
        future
            .exceptionally(throwable -> {
                LOGGER.error("Failed to process command: {}", ctx.getCommandInput().getRawInput(), throwable);
                return CommandExecutionResult.INTERNAL_ERROR;
            })
            .thenAcceptAsync(result -> {
                try {
                    switch (result) {
                        case SUCCESS -> {}
                        case INTERNAL_ERROR -> interaction.execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text(Emoji.X + " Во время выполнения команды произошла внутренняя ошибка, попробуйте ещё раз.")
                            .build());
                        case INVALID_SYNTAX -> interaction.execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text(Emoji.X + " Неверный синтаксис команды :(")
                            .build());
                        case UNKNOWN_COMMAND -> this.respondUnknownCommand(interaction);
                    }
                } catch (TelegramApiException e) {
                    LOGGER.error("Failed to process command execution result", e);
                }
            });
    }

    private void respondUnknownCommand(@NotNull MessageInteraction interaction) throws TelegramApiException {
        interaction.execute(SendMessage.builder()
            .chatId(interaction.getTelegramChat().getId())
            .text(Emoji.X + " Неизвестная команда :(")
            .build());
    }
}
