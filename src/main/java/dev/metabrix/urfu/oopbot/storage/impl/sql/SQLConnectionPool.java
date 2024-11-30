package dev.metabrix.urfu.oopbot.storage.impl.sql;

import java.sql.Connection;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

/**
 * Пул подключений к SQL-хранилищу данных.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface SQLConnectionPool extends AutoCloseable {
    /**
     * Возвращает количество подключений в пуле.
     *
     * @return количество подключений в пуле
     * @since 1.1.0
     * @author metabrix
     */
    int poolSize();

    /**
     * Возвращает подключение из пула.
     *
     * @return подключение
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull Connection getConnection() throws SQLException;

    /**
     * Проверяет, закрыт ли пул.
     *
     * @return {@code true}, если пул закрыт, иначе {@code false}
     * @since 1.1.0
     * @author metabrix
     */
    boolean isClosed();

    /**
     * Закрывает все подключения в пуле.
     *
     * @since 1.1.0
     * @author metabrix
     */
    @Override
    void close() throws Exception;
}
