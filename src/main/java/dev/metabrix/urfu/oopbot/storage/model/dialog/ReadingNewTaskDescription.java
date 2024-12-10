package dev.metabrix.urfu.oopbot.storage.model.dialog;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.Task;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.util.Emoji;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static dev.metabrix.urfu.oopbot.util.Checks.checkArgument;

public record ReadingNewTaskDescription(
    @NotNull String taskName
) implements DialogState {
    public static final @NotNull Set<Character> NO_DESCRIPTION_CHARS = Set.of(
        '-', '–', '—'
    );

    public static @NotNull DialogState fromJson(@NotNull JSONObject json) {
        String taskName = json.optString("task_name");
        checkArgument(
            taskName != null && !taskName.isBlank(),
            "task_name cannot be null or blank"
        );
        checkArgument(
            taskName.trim().length() <= Task.NAME_MAX_LENGTH,
            "task_name cannot be longer than " + Task.NAME_MAX_LENGTH + " characters"
        );
        return new ReadingNewTaskDescription(taskName);
    }

    @Override
    public void handleMessage(@NotNull MessageInteraction interaction) throws TelegramApiException {
        String taskDescription = interaction.getMessage().getText();
        if (taskDescription.isBlank() || taskDescription.length() == 1 && NO_DESCRIPTION_CHARS.contains(taskDescription.charAt(0))) {
            taskDescription = null;
        }
        if (taskDescription != null && taskDescription.length() > Task.DESCRIPTION_MAX_LENGTH) {
            interaction.execute(SendMessage.builder()
                .chatId(interaction.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Описание задачи не может быть длиннее " + Task.DESCRIPTION_MAX_LENGTH + " символов*")
                .build());
            return;
        }

        User user = interaction.getUser();
        Chat chat = interaction.getChat();

        interaction.getStorage().dialogStates().delete(user.id(), chat.id());
        Task task = interaction.getStorage().tasks().create(chat.id(), this.taskName, taskDescription, user.id());

        interaction.execute(SendMessage.builder()
            .chatId(interaction.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.PARTY_POPPER + " *Задача создана\\!*")
            .build());

        Update update = interaction.getUpdate();
        Message message = update.getMessage();
        message.setFrom(interaction.getTelegramUser());
        message.setChat(interaction.getTelegramChat());
        message.setText("/tasks show " + task.id());
        update.setMessage(message);
        interaction.getApplication().getBot().onUpdateReceived(update);
    }

    @Override
    public @NotNull DialogStateType type() {
        return DialogStateType.READING_NEW_TASK_DESCRIPTION;
    }

    @Override
    public @NotNull JSONObject toJson() {
        return new JSONObject().put("task_name", this.taskName());
    }
}
