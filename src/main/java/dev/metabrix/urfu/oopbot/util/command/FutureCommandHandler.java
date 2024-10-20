package dev.metabrix.urfu.oopbot.util.command;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Асинхронный обработчик команды.
 *
 * @since 1.0.0
 * @author metabrix
 */
@FunctionalInterface
public interface FutureCommandHandler extends CommandHandler {
    @Override
    @NotNull CompletableFuture<@NotNull CommandExecutionResult> executeFuture(@NotNull CommandContext ctx);

    @Override
    default @NotNull CommandExecutionResult execute(@NotNull CommandContext ctx) {
        return this.executeFuture(ctx).join();
    }
}
