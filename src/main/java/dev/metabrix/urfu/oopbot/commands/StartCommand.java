package dev.metabrix.urfu.oopbot.commands;

import dev.metabrix.urfu.oopbot.BotCommand;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.command.CommandContext;
import dev.metabrix.urfu.oopbot.util.command.CommandExecutionResult;
import dev.metabrix.urfu.oopbot.util.command.CommandHandler;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand implements CommandHandler {
    @Override
    public @NotNull CommandExecutionResult execute(@NotNull CommandContext ctx) throws TelegramApiException {
        ctx.getInteraction().execute(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(
                """
                %s
                
                С помощью этого бота можно удобно отслеживать задачи, дедлайны, а также настроить уведомления\\.
                
                Список команд: %s
                
                Бот находится в разработке %s
                """.formatted(
                    ctx.getTelegramChat().isUserChat() ? Emoji.NOTEBOOK + " *Трекер задач*" : "",
                    Arrays.stream(BotCommand.values())
                        .map(botCommand -> "/" + botCommand.getName())
                        .collect(Collectors.joining(", ")),
                    Emoji.EYES
                )
            )
            .build());
        return CommandExecutionResult.SUCCESS;
    }
}
