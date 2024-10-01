package dev.metabrix.urfu.oopbot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

import static dev.metabrix.urfu.oopbot.util.Checks.checkArgument;

/**
 * Конфигурация бота.
 *
 * @param botInfo информация о Telegram-боте
 * @param console интерактивная консоль
 * @since 1.0.0
 * @author metabrix
 */
public record BotConfiguration(
    @NotNull BotInfo botInfo,
    @NotNull Console console
) {
    /**
     * Загружает конфигурацию из <code>.conf</code>-файла.
     *
     * @param path путь к файлу конфигурации
     * @return конфигурация из файла
     * @since 1.0.0
     * @author metabrix
     */
    public static @NotNull BotConfiguration fromFile(@NotNull Path path) {
        return BotConfiguration.fromConfig(ConfigFactory.parseFile(path.toFile()));
    }

    /**
     * Создаёт конфигурацию из объекта {@link Config}.
     *
     * @param config объект {@link Config}
     * @return конфигурация из указанного {@link Config}
     * @since 1.0.0
     * @author metabrix
     */
    public static @NotNull BotConfiguration fromConfig(@NotNull Config config) {
        return new BotConfiguration(
            BotInfo.fromConfig(config.getConfig("bot-info"), "bot-info."),
            Console.fromConfig(config.getConfig("console"))
        );
    }

    /**
     * Секция <code>bot-info</code> в конфигурации.
     *
     * @param username юзернейм бота
     * @param token API-токен бота
     * @since 1.0.0
     * @author metabrix
     */
    public record BotInfo(
        @NotNull String username,
        @NotNull String token
    ) {
        /**
         * Создаёт {@link BotInfo} из объекта {@link Config}.
         *
         * @param config объект {@link Config}
         * @return {@link BotInfo} из указанного {@link Config}
         * @since 1.0.0
         * @author metabrix
         */
        public static @NotNull BotInfo fromConfig(@NotNull Config config, @NotNull String pathPrefix) {
            String username = config.hasPath("username") ? config.getString("username") : null;
            checkArgument(username != null && !username.isBlank(), pathPrefix + "username cannot be null or blank");

            String token = config.hasPath("token") ? config.getString("token") : null;
            checkArgument(token != null && !token.isBlank(), pathPrefix + "token cannot be null or blank");

            return new BotInfo(username, token);
        }
    }

    /**
     * Секция <code>console</code> в конфигурации.
     *
     * @param enabled включена ли интерактивная консоль
     * @since 1.0.0
     * @author metabrix
     */
    public record Console(
        boolean enabled
    ) {
        /**
         * Создаёт {@link Console} из объекта {@link Config}.
         *
         * @param config объект {@link Config}
         * @return {@link Console} из указанного {@link Config}
         * @since 1.0.0
         * @author metabrix
         */
        public static @NotNull Console fromConfig(@NotNull Config config) {
            return new Console(config.getBoolean("enabled"));
        }
    }
}
