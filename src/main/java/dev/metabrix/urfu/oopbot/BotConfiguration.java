package dev.metabrix.urfu.oopbot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import dev.metabrix.urfu.oopbot.storage.impl.mysql.MySQLDataStorage;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.metabrix.urfu.oopbot.util.Checks.checkArgument;

/**
 * Конфигурация бота.
 *
 * @param botInfo информация о Telegram-боте
 * @param console интерактивная консоль
 * @param dataStorage хранилище данных
 * @since 1.0.0
 * @author metabrix
 */
public record BotConfiguration(
    @NotNull BotInfo botInfo,
    @NotNull Console console,
    @NotNull DataStorage dataStorage
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
            Console.fromConfig(config.getConfig("console")),
            DataStorage.fromConfig(config.getConfig("data-storage"), "data-storage.")
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

    /**
     * Секция <code>data-storage</code> в конфигурации.
     *
     * @param type тип хранилища данных
     * @since 1.1.0
     * @author metabrix
     */
    public record DataStorage(
        @NotNull Type<?, ?> type,
        @NotNull Map<@NotNull Type<?, ?>, @NotNull Object> configurations
    ) {
        /**
         * Создаёт {@link DataStorage} из объекта {@link Config}.
         *
         * @param config объект {@link Config}
         * @return {@link DataStorage} из указанного {@link Config}
         * @since 1.1.0
         * @author metabrix
         */
        public static @NotNull DataStorage fromConfig(@NotNull Config config, @NotNull String pathPrefix) {
            Type<?, ?> selectedType = config.hasPath("type") ? Type.bySerializedName(config.getString("type")) : null;
            checkArgument(selectedType != null, pathPrefix + "type must be one of " + Type.values());

            Map<Type<?, ?>, Object> configurations = new HashMap<>();
            for (Type<?, ?> type : Type.values()) {
                try {
                    Config subConfig = config.hasPath(type.getSerializedName()) ? config.getConfig(type.getSerializedName()) : null;
                    checkArgument(
                        subConfig != null,
                        pathPrefix + type.getSerializedName() + " section must be present to initialize the '" + type.getSerializedName() + "' storage"
                    );
                    String subPathPrefix = pathPrefix + type.getSerializedName() + ".";
                    Object configuration = type.readConfiguration(subConfig, subPathPrefix);
                    configurations.put(type, configuration);
                } catch (IllegalArgumentException | ConfigException ex) {
                    if (type == selectedType) throw ex;
                }
            }

            return new DataStorage(selectedType, configurations);
        }

        /**
         * Создаёт хранилище соответствующего типа.
         *
         * @return хранилище
         * @since 1.1.0
         * @author metabrix
         */
        public @NotNull dev.metabrix.urfu.oopbot.storage.DataStorage createStorage() throws Exception {
            return this.type.createStorage(this);
        }

        /**
         * Тип хранилища данных.
         *
         * @since 1.1.0
         * @author metabrix
         */
        public static final class Type<T extends dev.metabrix.urfu.oopbot.storage.DataStorage, C> {
            private static final @NotNull Map<@NotNull String, @NotNull Type<?, ?>> BY_SERIALIZED_NAME = new HashMap<>();

            public static final @NotNull Type<@NotNull MySQLDataStorage, @NotNull MySQLConfiguration> MYSQL = new Type<>(
                "mysql",
                MySQLConfiguration::fromConfig,
                MySQLDataStorage::new
            );

            private final @NotNull String serializedName;
            private final @NotNull BiFunction<@NotNull Config, @NotNull String, @NotNull C> configurationReader;
            private final @NotNull StorageInitializer<@NotNull T, @NotNull C> storageInitializer;

            private Type(
                @NotNull String serializedName,
                @NotNull BiFunction<@NotNull Config, @NotNull String, @NotNull C> configurationReader,
                @NotNull StorageInitializer<@NotNull T, @NotNull C> storageInitializer
            ) {
                this.serializedName = serializedName;
                this.configurationReader = configurationReader;
                this.storageInitializer = storageInitializer;

                BY_SERIALIZED_NAME.put(this.serializedName, this);
            }

            /**
             * Возвращает название типа хранилища в файле конфигурации.
             *
             * @return имя хранилища в файле конфигурации
             * @since 1.1.0
             * @author metabrix
             */
            public @NotNull String getSerializedName() {
                return this.serializedName;
            }

            /**
             * Читает конфигурацию хранилища из объекта {@link Config}.
             *
             * @param config объект {@link Config}
             * @return конфигурация хранилища
             * @since 1.1.0
             * @author metabrix
             */
            public @NotNull C readConfiguration(@NotNull Config config, @NotNull String pathPrefix) {
                return this.configurationReader.apply(config, pathPrefix);
            }

            /**
             * Создаёт хранилище указанного типа.
             *
             * @param rootStorageConfiguration конфигурация хранилищ
             * @return хранилище
             * @since 1.1.0
             * @author metabrix
             */
            @SuppressWarnings("unchecked")
            public @NotNull T createStorage(@NotNull DataStorage rootStorageConfiguration) throws Exception {
                C configuration = (C) rootStorageConfiguration.configurations().get(this);
                checkArgument(
                    configuration != null,
                    "Unable to find configuration for the '" + this.serializedName + "' storage"
                );
                return this.storageInitializer.initialize(configuration);
            }

            @Override
            public @NotNull String toString() {
                return this.serializedName;
            }

            /**
             * Получает тип хранилища по его названию в файле конфигурации.
             *
             * @param serializedName имя хранилища в файле конфигурации
             * @return тип хранилища или {@code null}, если типа с таким названием не существует
             * @since 1.1.0
             * @author metabrix
             */
            public static @Nullable Type<?, ?> bySerializedName(@NotNull String serializedName) {
                return BY_SERIALIZED_NAME.get(serializedName);
            }

            /**
             * Возвращает список всех доступных типов хранилищ.
             *
             * @return список всех типов
             * @since 1.1.0
             * @author metabrix
             */
            public static @NotNull List<@NotNull Type<?, ?>> values() {
                return new ArrayList<>(BY_SERIALIZED_NAME.values());
            }

            /**
             * Инициализатор хранилища из общей конфигурации всех хранилищ.
             *
             * @param <T> тип хранилища
             * @since 1.1.0
             * @author metabrix
             */
            @FunctionalInterface
            public interface StorageInitializer<T extends dev.metabrix.urfu.oopbot.storage.DataStorage, C> {
                /**
                 * Создаёт хранилище для указанной конфигурации.
                 *
                 * @param configuration конфигурация хранилища
                 * @return хранилище
                 * @throws Exception в случае ошибки при инициализации
                 * @since 1.1.0
                 * @author metabrix
                 */
                T initialize(@NotNull C configuration) throws Exception;
            }
        }

        /**
         * Конфигурация MySQL.
         *
         * @param host имя хоста
         * @param port порт
         * @param database имя базы данных
         * @param username имя пользователя
         * @param password пароль
         * @param tablePrefix префикс названия таблиц бота
         * @param poolSize размер пула потоков
         * @since 1.1.0
         * @author metabrix
         */
        public record MySQLConfiguration(
            @NotNull String host,
            int port,
            @NotNull String database,
            @NotNull String username,
            @NotNull String password,
            @NotNull String tablePrefix,
            int poolSize
        ) {
            /**
             * Создаёт {@link MySQLConfiguration} из объекта {@link Config}.
             *
             * @param config объект {@link Config}
             * @return {@link MySQLConfiguration} из указанного {@link Config}
             * @since 1.1.0
             * @author metabrix
             */
            public static @NotNull MySQLConfiguration fromConfig(@NotNull Config config, @NotNull String pathPrefix) {
                String host = config.hasPath("host") ? config.getString("host") : null;
                checkArgument(host != null && !host.isBlank(), pathPrefix + "host cannot be null or blank");

                int port = config.hasPath("port") ? config.getInt("port") : 3306;
                checkArgument(1 <= port && port <= 65535, pathPrefix + "port must be between 1 and 65535");

                String database = config.hasPath("database") ? config.getString("database") : null;
                checkArgument(database != null && !database.isBlank(), pathPrefix + "database cannot be null or blank");

                String username = config.hasPath("username") ? config.getString("username") : null;
                checkArgument(username != null && !username.isBlank(), pathPrefix + "username cannot be null or blank");

                String password = config.hasPath("password") ? config.getString("password") : null;
                checkArgument(password != null && !password.isBlank(), pathPrefix + "password cannot be null or blank");

                String tablePrefix = config.hasPath("table-prefix") ? config.getString("table-prefix") : "tt_";

                int poolSize = config.hasPath("pool-size") ? config.getInt("pool-size") : 4;
                checkArgument(poolSize > 0, pathPrefix + "pool-size must be greater than 0");

                return new MySQLConfiguration(host, port, database, username, password, tablePrefix, poolSize);
            }
        }
    }
}
