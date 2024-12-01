package dev.metabrix.urfu.oopbot.storage.impl.mysql;

import org.jetbrains.annotations.NotNull;

record Tables(
    @NotNull String version,
    @NotNull String users,
    @NotNull String chats,
    @NotNull String tasks,
    @NotNull String dialogStates
) {
    Tables(@NotNull String tablePrefix) {
        this(
            tablePrefix + "version",
            tablePrefix + "users",
            tablePrefix + "chats",
            tablePrefix + "tasks",
            tablePrefix + "dialog_states"
        );
    }
}
