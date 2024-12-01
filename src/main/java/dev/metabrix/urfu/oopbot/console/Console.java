package dev.metabrix.urfu.oopbot.console;

import dev.metabrix.urfu.oopbot.BotApplication;
import java.nio.file.Path;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

/**
 * Консоль бота.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class Console extends SimpleTerminalConsole {
    private final @NotNull BotApplication application;
    private final @NotNull ConsoleHandler handler;

    /**
     * Создаёт экземпляр консоли для приложения с указанным обработчиком команд.
     *
     * @param application приложение
     * @param handler обработчик вводимых команд
     * @since 1.0.0
     * @author metabrix
     */
    public Console(@NotNull BotApplication application, @NotNull ConsoleHandler handler) {
        this.application = application;
        this.handler = handler;
    }

    @Override
    protected LineReader buildReader(@NotNull LineReaderBuilder builder) {
        return super.buildReader(builder
            .appName("Bot")
            .variable(LineReader.HISTORY_FILE, Path.of(".console_history"))
        );
    }

    @Override
    protected boolean isRunning() {
        return this.application.isRunning();
    }

    @Override
    protected void runCommand(@NotNull String command) {
        this.handler.handleCommand(this.application, command);
    }

    @Override
    protected void shutdown() {
        try {
            this.application.stop();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
