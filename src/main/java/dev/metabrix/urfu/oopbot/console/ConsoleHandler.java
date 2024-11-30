package dev.metabrix.urfu.oopbot.console;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.command.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Обработчик команд {@link Console консоли}.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class ConsoleHandler {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    /**
     * Обрабатывает команду, введённую в консоли.
     *
     * @param application приложение бота
     * @param rawInput введённая команда
     * @since 1.0.0
     * @author metabrix
     */
    public void handleCommand(@NotNull BotApplication application, @NotNull String rawInput) {
        CommandInput input = CommandInput.of(rawInput);
        if (input.isEmpty()) return;

        try {
            switch (input.readToken()) {
                case "stop" -> application.stop();
                default -> LOGGER.info("Unknown command: {}", rawInput);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to execute console command: {}", input, ex);
        }
    }
}
