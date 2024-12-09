package dev.metabrix.urfu.oopbot;

import dev.metabrix.urfu.oopbot.console.Console;
import dev.metabrix.urfu.oopbot.console.ConsoleHandler;
import dev.metabrix.urfu.oopbot.storage.DataStorage;
import dev.metabrix.urfu.oopbot.telegram.TelegramBot;
import dev.metabrix.urfu.oopbot.util.LogUtils;
import dev.metabrix.urfu.oopbot.util.Util;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static dev.metabrix.urfu.oopbot.util.Checks.checkState;

/**
 * Приложение бота.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class BotApplication {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull BotConfiguration configuration;

    private final @NotNull Console console;
    private final @NotNull TelegramBot bot;

    private final @NotNull Object stateLock = new Object();
    private @Nullable BotSession currentSession;

    private @Nullable DataStorage storage;

    /**
     * Создаёт приложение.
     *
     * @param configuration конфигурация
     * @since 1.0.0
     * @author metabrix
     */
    public BotApplication(@NotNull BotConfiguration configuration) {
        this.configuration = configuration;

        this.console = new Console(this, new ConsoleHandler());
        this.bot = new TelegramBot(
            this,
            configuration.botInfo().username(),
            configuration.botInfo().token(),
            new MainUpdateListener(this)
        );
    }

    /**
     * Возвращает бота (клиента Telegram API).
     *
     * @return бот
     * @since 1.0.0
     * @author metabrix
     */
    public @NotNull TelegramBot getBot() {
        return this.bot;
    }

    /**
     * Возвращает хранилище данных, если бот запущен, в противном случае выбрасывает {@link IllegalStateException}.
     *
     * @return хранилище данных
     * @throws IllegalStateException если бот не запущен
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull DataStorage getStorage() {
        DataStorage storage = this.storage;
        checkState(storage != null, "Bot is not running");
        return storage;
    }

    /**
     * Запускает приложение.
     *
     * @throws TelegramApiException когда Telegram API возвращает ошибку при запуске
     * @since 1.0.0
     * @author metabrix
     */
    public void start() throws Exception {
        synchronized (this.stateLock) {
            checkState(this.currentSession == null, "Bot is already running");

            long startMillis = Util.monotonicMillis();

            this.storage = this.configuration.dataStorage().createStorage();

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            this.currentSession = botsApi.registerBot(this.bot);
            checkState(this.currentSession != null, "Bot startup was successful, but the created session is null");

            long elapsedMillis = Util.monotonicMillis() - startMillis;
            LOGGER.info("Connected ({}s)!", String.format(Locale.ROOT, "%.3f", elapsedMillis / 1000.0));
        }

        this.console.start();
    }

    /**
     * Останавливает приложение.
     *
     * @since 1.0.0
     * @author metabrix
     */
    public void stop() throws Exception {
        synchronized (this.stateLock) {
            checkState(this.currentSession != null, "Bot is not running");

            LOGGER.info("Stopping the bot session...");

            long startMillis = Util.monotonicMillis();

            this.currentSession.stop();
            this.currentSession = null;

            if (this.storage != null) {
                this.storage.close();
                this.storage = null;
            }

            long elapsedMillis = Util.monotonicMillis() - startMillis;
            LOGGER.info("Bot session shutdown complete ({}s)!", String.format(Locale.ROOT, "%.3f", elapsedMillis / 1000.0));
        }
    }

    /**
     * Проверяет, работает ли сейчас бот.
     *
     * @return <code>true</code>, если сессия бота активна, иначе <code>false</code>
     * @since 1.0.0
     * @author metabrix
     */
    public boolean isRunning() {
        return this.currentSession != null;
    }
}
