package dev.metabrix.urfu.oopbot.commands;

import dev.metabrix.urfu.oopbot.storage.TaskStorage;
import dev.metabrix.urfu.oopbot.storage.model.Task;
import dev.metabrix.urfu.oopbot.storage.model.User;
import dev.metabrix.urfu.oopbot.storage.model.dialog.ReadingNewTaskName;
import dev.metabrix.urfu.oopbot.storage.model.dialog.ReadingUpdatedTaskDescription;
import dev.metabrix.urfu.oopbot.storage.model.dialog.ReadingUpdatedTaskName;
import dev.metabrix.urfu.oopbot.util.Emoji;
import dev.metabrix.urfu.oopbot.util.Util;
import dev.metabrix.urfu.oopbot.util.command.CommandContext;
import dev.metabrix.urfu.oopbot.util.command.CommandExecutionResult;
import dev.metabrix.urfu.oopbot.util.command.FutureCommandHandler;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class TasksCommand implements FutureCommandHandler {
    private static final int PAGE_SIZE = 5;
    private static final @NotNull SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm:ss");

    @Override
    public @NotNull CompletableFuture<@NotNull CommandExecutionResult> executeFuture(@NotNull CommandContext ctx) {
        String subCommand = ctx.getCommandInput().readToken();
        return switch (subCommand) {
            case "new" -> this.handleNew(ctx);
            case "list" -> {
                int pageIndex = 0;
                try {
                    pageIndex = ctx.getCommandInput().readInt() - 1;
                    if (pageIndex < 0) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored) {
                }
                yield this.handleList(ctx, pageIndex);
            }
            case "show" -> this.handleShow(ctx);
            case "edit-name" -> this.handleEditName(ctx);
            case "edit-description" -> this.handleEditDescription(ctx);
            case "delete-request" -> this.handleDeleteRequest(ctx);
            case "delete-confirm" -> this.handleDeleteConfirm(ctx);
            default -> this.handleHelp(ctx);
        };
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleHelp(@NotNull CommandContext ctx) {
        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.NOTEBOOK + " Используйте кнопки для создания и просмотра задач")
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                    InlineKeyboardButton.builder()
                        .text(Emoji.PAGE_FACING_UP + " Создать задачу")
                        .callbackData("command:tasks new")
                        .build(),
                    InlineKeyboardButton.builder()
                        .text(Emoji.NOTEBOOK + " Список задач")
                        .callbackData("command:tasks list")
                        .build()
                )).build())
            .build()
        ).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleNew(@NotNull CommandContext ctx) {
        ctx.getStorage().dialogStates().set(
            ctx.getSender().id(),
            ctx.getChat().id(),
            ReadingNewTaskName.INSTANCE
        );
        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.WRITING_HAND + " Напишите название новой задачи")
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleList(@NotNull CommandContext ctx, int pageIndex) {
        int totalTasks = ctx.getStorage().tasks().countTasksByChatId(ctx.getChat().id());
        int totalPages = totalTasks / PAGE_SIZE + (totalTasks % PAGE_SIZE == 0 ? 0 : 1);

        if (totalPages == 0) {
            return ctx.getInteraction().executeAsync(SendMessage.builder()
                .chatId(ctx.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text("""
                    %s *Создайте первую задачу\\!*
                    
                    Используйте кнопку ниже или `/tasks new`, чтобы создать задачу\\.
                    """.formatted(Emoji.PAGE_FACING_UP))
                .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(List.of(
                    InlineKeyboardButton.builder()
                        .text(Emoji.PAGE_FACING_UP + " Создать первую задачу")
                        .callbackData("command:tasks new")
                        .build()
                )).build())
                .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
        }

        if (pageIndex >= totalPages) return this.handleList(ctx, 0);

        StringBuilder message = new StringBuilder();

        message.append("%s *Задачи:* страница %d из %d\n".formatted(Emoji.NOTEBOOK, pageIndex + 1, totalPages));

        HashMap<Integer, User> userCache = new HashMap<>();
        List<Task> tasks = ctx.getStorage().tasks().searchTasksByChatId(
            PAGE_SIZE, pageIndex * PAGE_SIZE,
            TaskStorage.Sort.UPDATED_AT, false,
            ctx.getChat().id()
        );
        for (Task task : tasks) {
            message.append("\n");

            User createdBy = userCache.computeIfAbsent(task.createdById(), ctx.getStorage().users()::getById);
            message.append("*%s* _\\(создана %s %s\\)_\n".formatted(
                Util.sanitizeString(task.name()),
                createdBy == null ? "<null>" : createdBy.markdownMention(),
                formatInstant(task.createdAt())
            ));
            if (task.updatedAt() != null) {
                User updatedBy = task.updatedById() == null ? null : userCache.computeIfAbsent(task.updatedById(), ctx.getStorage().users()::getById);
                message.append("_Последнее изменение %s от %s_\n".formatted(
                    formatInstant(task.updatedAt()),
                    updatedBy == null ? "<null>" : updatedBy.markdownMention()
                ));
            }

            if (task.description() == null) continue;

            for (String line : task.description().split("\n")) {
                message.append(">").append(Util.sanitizeString(line)).append("\n");
            }
        }

        List<InlineKeyboardButton> taskButtons = new ArrayList<>(tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            taskButtons.add(InlineKeyboardButton.builder()
                .text((i + 1) + Emoji.KEYCAP_SUFFIX.toString())
                .callbackData("command:tasks show %d".formatted(task.id()))
                .build());
        }

        int page = pageIndex + 1;
        List<InlineKeyboardButton> pageButtons = new ArrayList<>();
        if (page > 1) {
            pageButtons.add(InlineKeyboardButton.builder()
                .text("← Страница " + (page - 1))
                .callbackData("command:tasks list %d".formatted(page - 1))
                .build());
        }
        if (page < totalPages) {
            pageButtons.add(InlineKeyboardButton.builder()
                .text("Страница " + (page + 1) + " →")
                .callbackData("command:tasks list %d".formatted(page + 1))
                .build());
        }

        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(message.toString())
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(taskButtons)
                .keyboardRow(pageButtons)
                .build())
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleShow(@NotNull CommandContext ctx) {
        int taskId;
        try {
            taskId = ctx.getCommandInput().readInt();
        } catch (NumberFormatException ex) {
            return this.handleHelp(ctx);
        }

        Task task = ctx.getStorage().tasks().getById(taskId);
        if (task == null) {
            return ctx.getInteraction().executeAsync(SendMessage.builder()
                .chatId(ctx.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Задача не найдена*")
                .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
        }

        StringBuilder message = new StringBuilder();

        User createdBy = ctx.getStorage().users().getById(task.createdById());
        message.append("*%s* _\\(создана %s %s\\)_\n".formatted(
            Util.sanitizeString(task.name()),
            createdBy == null ? "<null>" : createdBy.markdownMention(),
            formatInstant(task.createdAt())
        ));
        if (task.updatedAt() != null) {
            User updatedBy = task.updatedById() == null ? null : ctx.getStorage().users().getById(task.updatedById());
            message.append("_Последнее изменение %s от %s_\n".formatted(
                formatInstant(task.updatedAt()),
                updatedBy == null ? "<null>" : updatedBy.markdownMention()
            ));
        }

        if (task.description() != null) {
            message.append("\n").append("*Описание:*\n");
            for (String line : task.description().split("\n")) {
                message.append(">").append(Util.sanitizeString(line)).append("\n");
            }
        }

        SendMessage.SendMessageBuilder builder = SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(message.toString());

        if (createdBy != null) {
            builder.replyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                    InlineKeyboardButton.builder()
                        .text("Изменить название")
                        .callbackData("command:tasks edit-name %d".formatted(taskId))
                        .build(),
                    InlineKeyboardButton.builder()
                        .text("Изменить описание")
                        .callbackData("command:tasks edit-description %d".formatted(taskId))
                        .build()
                ))
                .keyboardRow(List.of(
                    InlineKeyboardButton.builder()
                        .text("Удалить задачу")
                        .callbackData("command:tasks delete-request %d".formatted(taskId))
                        .build()
                ))
                .build());
        }

        return ctx.getInteraction()
            .executeAsync(builder.build())
            .thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleEditName(@NotNull CommandContext ctx) {
        int taskId;
        try {
            taskId = ctx.getCommandInput().readInt();
        } catch (NumberFormatException ex) {
            return this.handleHelp(ctx);
        }

        Task task = ctx.getStorage().tasks().getById(taskId);
        if (task == null) {
            return ctx.getInteraction().executeAsync(SendMessage.builder()
                .chatId(ctx.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Задача не найдена*")
                .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
        }

        ctx.getStorage().dialogStates().set(task.createdById(), task.chatId(), new ReadingUpdatedTaskName(task.id()));

        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.WRITING_HAND + " Напишите новое название задачи")
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleEditDescription(@NotNull CommandContext ctx) {
        int taskId;
        try {
            taskId = ctx.getCommandInput().readInt();
        } catch (NumberFormatException ex) {
            return this.handleHelp(ctx);
        }

        Task task = ctx.getStorage().tasks().getById(taskId);
        if (task == null) {
            return ctx.getInteraction().executeAsync(SendMessage.builder()
                .chatId(ctx.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Задача не найдена*")
                .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
        }

        ctx.getStorage().dialogStates().set(task.createdById(), task.chatId(), new ReadingUpdatedTaskDescription(task.id()));

        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.WRITING_HAND + " Напишите новое описание задачи")
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleDeleteRequest(@NotNull CommandContext ctx) {
        int taskId;
        try {
            taskId = ctx.getCommandInput().readInt();
        } catch (NumberFormatException ex) {
            return this.handleHelp(ctx);
        }

        Task task = ctx.getStorage().tasks().getById(taskId);
        if (task == null) {
            return ctx.getInteraction().executeAsync(SendMessage.builder()
                .chatId(ctx.getTelegramChat().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(Emoji.X + " *Задача не найдена*")
                .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
        }

        return ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(Emoji.EXCLAMATION_MARK + " Подтвердите удаление задачи «" + Util.sanitizeString(task.name()) + "»")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(List.of(
                InlineKeyboardButton.builder()
                    .text(Emoji.TRASH_BIN + " Удалить задачу")
                    .callbackData("command:tasks delete-confirm %d".formatted(taskId))
                    .build(),
                InlineKeyboardButton.builder()
                    .text(Emoji.X + " Отмена")
                    .callbackData("delete-message:%d".formatted(ctx.getTelegramChat().getId()))
                    .build()
            )).build())
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);
    }

    private @NotNull CompletableFuture<@NotNull CommandExecutionResult> handleDeleteConfirm(@NotNull CommandContext ctx) {
        int taskId;
        try {
            taskId = ctx.getCommandInput().readInt();
        } catch (NumberFormatException ex) {
            return this.handleHelp(ctx);
        }

        boolean success = ctx.getStorage().tasks().delete(taskId);

        CompletableFuture<CommandExecutionResult> future = ctx.getInteraction().executeAsync(SendMessage.builder()
            .chatId(ctx.getTelegramChat().getId())
            .parseMode(ParseMode.MARKDOWNV2)
            .text(success ? Emoji.WHITE_CHECK_MARK + " Задача удалена" : Emoji.X + " *Задача не найдена*")
            .build()).thenApply(ignored -> CommandExecutionResult.SUCCESS);

        future.thenAccept(ignored -> this.handleList(ctx, 0));

        return future;
    }

    private @NotNull String formatInstant(@NotNull Instant instant) {
        return Util.sanitizeString(DATE_FORMAT.format(Date.from(instant)));
    }
}
