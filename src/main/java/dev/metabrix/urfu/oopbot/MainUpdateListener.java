package dev.metabrix.urfu.oopbot;

import dev.metabrix.urfu.oopbot.telegram.MessageUpdateContext;
import dev.metabrix.urfu.oopbot.telegram.UpdateListener;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.command.*;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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

        MessageUpdateContext updateContext = new MessageUpdateContext(this.application, update);

        Message message = update.getMessage();
        String text = message.getText();
        if (text == null || !COMMAND_REGEX.matcher(text).matches()) {
            if (message.getChat().isUserChat()) this.respondUnknownCommand(message);
            return;
        }

        updateContext.createOrUpdateUser();
        updateContext.createChatIfNotExists();

        CommandContext ctx = new CommandContextImpl(this.application, update);
        CommandInput input = ctx.getCommandInput();

        String commandLabel = input.readToken();
        BotCommand command = BotCommand.byName(commandLabel);
        if (command == null) {
            this.respondUnknownCommand(message);
            return;
        }


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

    private void respondUnknownCommand(@NotNull Message message) throws TelegramApiException {
        this.application.getBot().execute(SendMessage.builder()
            .chatId(message.getChatId())
            .text(Emoji.X + " Неизвестная команда :(")
            .build());
    }
}
