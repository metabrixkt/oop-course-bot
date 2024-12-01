package dev.metabrix.urfu.oopbot.util.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Исключение работы с хранилищем.
 *
 * @since 1.1.0
 * @author metabrix
 */
public class StorageException extends RuntimeException {
    /**
     * Создаёт {@link StorageException}.
     *
     * @param cause фактическое исключение
     * @since 1.1.0
     * @author metabrix
     */
    public StorageException(@NotNull Exception cause) {
        super(cause);
    }
}
