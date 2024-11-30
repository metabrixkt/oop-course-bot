package dev.metabrix.urfu.oopbot.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import dev.metabrix.urfu.oopbot.BotCommand;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.command.CommandContext;
import dev.metabrix.urfu.oopbot.util.command.CommandExecutionResult;
import dev.metabrix.urfu.oopbot.util.command.CommandHandler;

public class StartCommand implements CommandHandler {
    @Override
    public @NotNull CommandExecutionResult execute(@NotNull CommandContext ctx) {
        try {
            ctx.getApplication().getBot().execute(SendMessage.builder()
                .chatId(ctx.getChat().getId())
                .parseMode("html")
                .text(
                    """
                    %s
                    
                    С помощью этого бота можно удобно отслеживать задачи, дедлайны, а также настроить уведомления.
                    
                    Список команд: %s
                    
                    Бот находится в разработке %s
                    """.formatted(
                        ctx.getChat().isUserChat() ? Emoji.NOTEBOOK + " <b>Трекер задач</b>" : "",
                        Arrays.stream(BotCommand.values())
                            .map(botCommand -> "/" + botCommand.getName())
                            .collect(Collectors.joining(", ")),
                        Emoji.EYES
                    )
                )
                .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return CommandExecutionResult.SUCCESS;
    }
}
