package dev.metabrix.urfu.oopbot.util.command;

/**
 * Результат выполнения команды.
 *
 * @see CommandHandler#executeFuture(CommandContext)
 */
public enum CommandExecutionResult {
    SUCCESS,
    INVALID_SYNTAX,
    INTERNAL_ERROR,
    UNKNOWN_COMMAND,
    ;
}
