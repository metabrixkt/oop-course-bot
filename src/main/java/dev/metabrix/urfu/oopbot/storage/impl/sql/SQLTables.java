package dev.metabrix.urfu.oopbot.storage.impl.sql;

import org.jetbrains.annotations.NotNull;

public record SQLTables(
    @NotNull String version,
    @NotNull String users,
    @NotNull String chats,
    @NotNull String tasks,
    @NotNull String tasksComments,
    @NotNull String dialogStates
) {
    public SQLTables(@NotNull String tablePrefix) {
        this(
            tablePrefix + "version",
            tablePrefix + "users",
            tablePrefix + "chats",
            tablePrefix + "tasks",
            tablePrefix + "tasks_comments",
            tablePrefix + "dialog_states"
        );
    }
}
