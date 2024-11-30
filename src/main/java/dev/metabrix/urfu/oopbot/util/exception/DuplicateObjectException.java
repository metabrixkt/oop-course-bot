package dev.metabrix.urfu.oopbot.util.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Исключение добавления дубликата объекта.
 *
 * @since 1.1.0
 * @author metabrix
 */
public class DuplicateObjectException extends Exception {
    private final @NotNull Class<?> objectType;

    /**
     * Создаёт {@link DuplicateObjectException}.
     *
     * @param objectType класс дублирующегося объекта
     * @param detail дополнительная информация, например, совпавший ключ
     * @since 1.1.0
     * @author metabrix
     */
    public DuplicateObjectException(@NotNull Class<?> objectType, @NotNull String detail) {
        super(formatMessage(objectType, detail));

        this.objectType = objectType;
    }

    /**
     * Создаёт {@link DuplicateObjectException}.
     *
     * @param objectType класс дублирующегося объекта
     * @param detail дополнительная информация, например, совпавший ключ
     * @param cause исходное исключение
     * @since 1.1.0
     * @author metabrix
     */
    public DuplicateObjectException(@NotNull Class<?> objectType, @NotNull String detail, @NotNull Throwable cause) {
        super(formatMessage(objectType, detail), cause);

        this.objectType = objectType;
    }

    /**
     * Возвращает тип дублирующегося объекта.
     *
     * @return класс
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull Class<?> getObjectType() {
        return this.objectType;
    }

    private static @NotNull String formatMessage(@NotNull Class<?> objectType, @NotNull String detail) {
        return "Duplicate %s: %s".formatted(objectType.getSimpleName(), detail);
    }
}
