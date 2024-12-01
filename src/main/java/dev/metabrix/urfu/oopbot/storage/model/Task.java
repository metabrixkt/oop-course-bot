package dev.metabrix.urfu.oopbot.storage.model;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.glassfish.jersey.internal.guava.Preconditions.checkArgument;

/**
 * Задача в боте.
 *
 * @param id внутренний ID задачи
 * @param chatId внутренний ID чата этой задачи
 * @param name название задачи
 * @param description описание задачи
 * @param createdById внутренний ID пользователя, который создал задачу
 * @param createdAt дата-время создания задачи
 * @param updatedById внутренний ID пользователя, который последний раз обновлял задачу
 * @param updatedAt дата-время последнего обновления задачи
 * @since 1.1.0
 * @author metabrix
 */
public record Task(
    int id,
    int chatId,
    @NotNull String name,
    @Nullable String description,
    int createdById,
    @NotNull Instant createdAt,
    @Nullable Integer updatedById,
    @Nullable Instant updatedAt
) {
    public static final int NAME_MAX_LENGTH = 200;
    public static final int DESCRIPTION_MAX_LENGTH = 4000;

    /**
     * Проверяет, что указанное название задачи корректно.
     *
     * @param name название задачи
     * @throws IllegalArgumentException если название задачи некорректно
     * @since 1.1.0
     * @author metabrix
     */
    public static void validateName(@NotNull String name) {
        checkArgument(
            !name.isBlank(),
            "name cannot be blank"
        );
        checkArgument(
            name.trim().length() <= NAME_MAX_LENGTH,
            "name cannot be longer than " + NAME_MAX_LENGTH + " characters"
        );
    }

    /**
     * Проверяет, что указанное описание задачи корректно.
     *
     * @param description описание задачи
     * @throws IllegalArgumentException если описание задачи некорректно
     * @since 1.1.0
     * @author metabrix
     */
    public static void validateDescription(@Nullable String description) {
        if (description == null) return;

        checkArgument(
            !description.isBlank(),
            "description cannot be blank"
        );
        checkArgument(
            description.trim().length() <= DESCRIPTION_MAX_LENGTH,
            "description cannot be longer than " + DESCRIPTION_MAX_LENGTH + " characters"
        );
    }
}
