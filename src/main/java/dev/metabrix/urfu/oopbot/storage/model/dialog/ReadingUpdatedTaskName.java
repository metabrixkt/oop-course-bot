package dev.metabrix.urfu.oopbot.storage.model.dialog;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.storage.model.Chat;
import dev.metabrix.urfu.oopbot.storage.model.Task;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.util.Emoji;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public record ReadingUpdatedTaskName(
    int taskId
) implements DialogState {
    public static @NotNull DialogState fromJson(@NotNull JSONObject json) {
        return new ReadingUpdatedTaskName(json.getInt("task_id"));
    }

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

        User user = interaction.getUser();
        Chat chat = interaction.getChat();

        interaction.getStorage().dialogStates().delete(user.id(), chat.id());
        interaction.getStorage().tasks().updateName(this.taskId(), taskName, user.id());

        interaction.execute(SendMessage.builder()
            .chatId(interaction.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.WHITE_CHECK_MARK + " *Задача обновлена\\!*")
            .build());

        Update update = interaction.getUpdate();
        Message message = update.getMessage();
        message.setFrom(interaction.getTelegramUser());
        message.setChat(interaction.getTelegramChat());
        message.setText("/tasks show " + this.taskId());
        update.setMessage(message);
        interaction.getApplication().getBot().onUpdateReceived(update);
    }

    @Override
    public @NotNull DialogStateType type() {
        return DialogStateType.READING_UPDATED_TASK_NAME;
    }

    @Override
    public @NotNull JSONObject toJson() {
        return new JSONObject().put("task_id", this.taskId());
    }
}
