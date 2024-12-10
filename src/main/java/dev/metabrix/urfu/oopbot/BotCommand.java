package dev.metabrix.urfu.oopbot;

import dev.metabrix.urfu.oopbot.commands.StartCommand;
import dev.metabrix.urfu.oopbot.commands.TasksCommand;
import dev.metabrix.urfu.oopbot.util.command.CommandHandler;
import java.util.HashMap;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Команды бота. Здесь определяются все команды бота.
 *
 * @see #BotCommand(String , CommandHandler handler, String... aliases)
 * @since 1.0.0
 * @author metabrix
 */
public enum BotCommand {
    START("start", new StartCommand(), "help"),
    TASKS("tasks", new TasksCommand()),
    ;

    private static final @NotNull HashMap<@NotNull String, @NotNull BotCommand> COMMANDS = new HashMap<>();

    static {
        for (BotCommand command : BotCommand.values()) {
            COMMANDS.put(command.getName(), command);
            for (String alias : command.getAliases()) {
                COMMANDS.put(alias, command);
            }
        }
    }

    private final @NotNull String name;
    private final @NotNull Set<@NotNull String> aliases;
    private final @NotNull CommandHandler handler;

    BotCommand(@NotNull String name, @NotNull CommandHandler handler, @NotNull String @NotNull ... aliases) {
        this.name = name;
        this.aliases = Set.of(aliases);
        this.handler = handler;
    }

    /**
     * Возвращает имя команды.
     *
     * @return имя команды
     * @since 1.0.0
     * @author metabrix
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Возвращает алиасы команды (альтернативные имена).
     *
     * @return алиасы
     * @since 1.0.0
     * @author metabrix
     * @apiNote Алиасы команды не должны отображаться в команде {@code /help} и других справках.
     */
    public @NotNull Set<@NotNull String> getAliases() {
        return this.aliases;
    }

    /**
     * Возвращает обработчик команды.
     *
     * @return обработчик команды
     * @since 1.0.0
     * @author metabrix
     */
    public @NotNull CommandHandler getHandler() {
        return this.handler;
    }

    /**
     * Возвращает команду с указанным именем или алиасом.
     *
     * @param name имя или алиас команды
     * @return команда
     * @since 1.0.0
     * @author metabrix
     */
    public static @Nullable BotCommand byName(@NotNull String name) {
        return COMMANDS.get(name);
    }
}
