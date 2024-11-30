package dev.metabrix.urfu.oopbot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import dev.metabrix.urfu.oopbot.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("");

    private static final @NotNull String CONFIG_FILENAME = "application.conf";
    private static final @NotNull String CONFIG_PROPERTY_PREFIX = "oopbot.";

    public static void main(String[] args) {
        long startTime = Util.monotonicMillis();

        BotConfiguration config = loadConfigOrSaveDefault();

        try {
            LOGGER.info("Starting the @{} bot", config.botInfo().username());
            BotApplication application = new BotApplication(config);
            application.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    application.stop();
                } catch (Exception ex) {
                    LOGGER.error("Failed to stop the bot", ex);
                }
            }));

            long elapsedMillis = Util.monotonicMillis() - startTime;
            LOGGER.info("Done ({}s)!", String.format(Locale.ROOT, "%.3f", elapsedMillis / 1000.0));
        } catch (Throwable t) {
            LOGGER.error("Failed to start the bot", t);
        }
    }

    /**
     * Загружает конфиг из файла, если он существует, в противном случае создаёт дефолтный конфиг-файл и завершает работу приложения.
     *
     * @return конфигурация приложения
     * @since 1.0.0
     * @author metabrix
     */
    private static @NotNull BotConfiguration loadConfigOrSaveDefault() {
        Path path = Path.of(CONFIG_FILENAME);
        if (!Files.isRegularFile(path)) {
            LOGGER.warn("Unable to find bot configuration file {}", path);

            try {
                createDefaultConfigFile(path);
            } catch (IOException e) {
                LOGGER.error("Failed to create default config file in {}", path, e);
            }

            System.exit(1);
            return null;
        }

        return loadConfig(path);
    }

    /**
     * Копирует дефолтный конфиг-файл из ресурсов приложения на указанный путь.
     *
     * @param path путь для сохранения конфиг-файла
     * @throws IOException если произойдёт ошибка ввода/вывода
     * @since 1.0.0
     * @author metabrix
     */
    private static void createDefaultConfigFile(@NotNull Path path) throws IOException {
        LOGGER.info("Creating default config file: {}", path);

        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME)) {
            if (is == null) {
                LOGGER.error("Unable to find default bot configuration file {}, was the JAR file corrupted?", CONFIG_FILENAME);
                System.exit(1);
                return;
            }
            Files.copy(is, path);
        }
    }

    /**
     * Загружает конфиг приложения из указанного файла, используя {@link System#getProperties() системные значения},
     * указанные в командной строке, с приоритетом выше, чем данные в конфиг-файле. Полезно,
     * когда нужно запустить приложение без возможности использовать конфиг-файл.
     *
     * @param path путь
     * @return конфигурация приложения из файла
     * @since 1.0.0
     * @author metabrix
     */
    private static @NotNull BotConfiguration loadConfig(@NotNull Path path) {
        try {
            LOGGER.info("Loading configuration from {}", path);

            Config config = ConfigFactory.parseFile(path.toFile());

            for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                Object keyObject = entry.getKey();
                Object valueObject = entry.getValue();
                String key = keyObject.toString();
                if (!key.startsWith(CONFIG_PROPERTY_PREFIX)) continue;

                config = config.withValue(
                    key.substring(CONFIG_PROPERTY_PREFIX.length()),
                    ConfigValueFactory.fromAnyRef(valueObject.toString(), "Java property " + key)
                );
            }

            return BotConfiguration.fromFile(path);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to load configuration from {}: {}", path, e.getLocalizedMessage());
            System.exit(1);
            return null;
        }
    }
}
