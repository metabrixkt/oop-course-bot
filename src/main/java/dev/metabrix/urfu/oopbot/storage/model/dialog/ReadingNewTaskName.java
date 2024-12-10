package dev.metabrix.urfu.oopbot.storage.model.dialog;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.model.Task;
import dev.metabrix.urfu.oopbot.util.Emoji;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ReadingNewTaskName implements DialogState {
    public static final @NotNull ReadingNewTaskName INSTANCE = new ReadingNewTaskName();

    @Override
    public void handleMessage(@NotNull MessageInteraction interaction) throws TelegramApiException {
        String taskName = interaction.getMessage().getText();
        if (taskName.isBlank()) {
            interaction.execute(SendMessage.builder()
                .chatId(interaction.getTelegramChat().getId())
                .text(Emoji.X + " *Название задачи не может быть пустым*")
                .build());
            return;
        }
        if (taskName.trim().length() > Task.NAME_MAX_LENGTH) {
            interaction.execute(SendMessage.builder()
                .chatId(interaction.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Название задачи не может быть длиннее " + Task.NAME_MAX_LENGTH + " символов*")
                .build());
            return;
        }

        interaction.getStorage().dialogStates().set(
            interaction.getUser().id(),
            interaction.getChat().id(),
            new ReadingNewTaskDescription(taskName)
        );
        interaction.execute(SendMessage.builder()
            .chatId(interaction.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.WRITING_HAND + " Хорошо, теперь напишите описание задачи\\. " +
                "Если описание не нужно, напишите `-` — тогда задача будет создана без него")
            .build());
    }

    @Override
    public @NotNull DialogStateType type() {
        return DialogStateType.READING_NEW_TASK_NAME;
    }

    @Override
    public @NotNull JSONObject toJson() {
        return new JSONObject();
    }
}
