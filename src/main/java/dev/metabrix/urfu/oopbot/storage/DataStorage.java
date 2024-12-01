package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.BotConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Хранилище данных бота.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface DataStorage extends AutoCloseable {
    /**
     * Возвращает тип хранилища в файле конфигурации.
     *
     * @return тип хранилища в файле конфигурации
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull BotConfiguration.DataStorage.Type<?, ?> getType();

    /**
     * Возвращает хранилище пользователей.
     *
     * @return хранилище пользователей
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull UserStorage users();

    /**
     * Возвращает хранилище чатов.
     *
     * @return хранилище чатов
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull ChatStorage chats();

    /**
     * Возвращает хранилище задач.
     *
     * @return хранилище задач
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull TaskStorage tasks();

    /**
     * Возвращает хранилище состояний диалогов.
     *
     * @return хранилище состояний диалогов
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull DialogStateStorage dialogStates();

    /**
     * Проверяет, закрыто ли хранилище.
     *
     * @return {@code true}, если хранилище закрыто, иначе {@code false}
     * @since 1.1.0
     * @author metabrix
     */
    boolean isClosed();

    /**
     * Завершает активные запросы и закрывает соединения.
     *
     * @since 1.1.0
     * @author metabrix
     */
    @Override
    void close() throws Exception;
}
