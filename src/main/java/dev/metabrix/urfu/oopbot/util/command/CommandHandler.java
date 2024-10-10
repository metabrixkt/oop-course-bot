package dev.metabrix.urfu.oopbot.util.command;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Обработчик команды.
 *
 * @since 1.0.0
 * @author metabrix
 */
@FunctionalInterface
public interface CommandHandler {
    /**
     * Выполняет команду.
     *
     * @param context контекст выполнения команды
     * @return результат
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull CompletableFuture<@NotNull CommandExecutionResult> executeFuture(@NotNull CommandContext context) {
        CompletableFuture<@NotNull CommandExecutionResult> future = new CompletableFuture<>();
        try {
            future.complete(this.execute(context));
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }

    /**
     * Выполняет команду, блокируя текущий поток до конца её выполнения.
     *
     * @param context контекст выполнения команды
     * @return результат
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull CommandExecutionResult execute(@NotNull CommandContext context);
}
