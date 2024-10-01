package dev.metabrix.urfu.oopbot.console;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * @param command введённая команда
     * @since 1.0.0
     * @author metabrix
     */
    public void handleCommand(@NotNull BotApplication application, @NotNull String command) {
        List<String> list = new ArrayList<>(Arrays.asList(command.split(" ")));
        if (list.isEmpty()) return;

        switch (list.removeFirst()) {
            case "stop" -> application.stop();
            default -> LOGGER.info("Unknown command: {}", command);
        }
    }
}
