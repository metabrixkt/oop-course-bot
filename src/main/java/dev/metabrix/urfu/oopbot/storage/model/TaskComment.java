package dev.metabrix.urfu.oopbot.storage.model;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.glassfish.jersey.internal.guava.Preconditions.checkArgument;

/**
 * Комментарий к задаче в боте.
 *
 * @param id внутренний ID комментария
 * @param taskId внутренний ID задачи
 * @param authorId внутренний ID автора комментария
 * @param content текст комментария
 * @param postedAt дата-время публикации комментария
 * @param updatedAt дата-время последнего обновления комментария
 * @since 1.2.0
 * @author metabrix
 */
public record TaskComment(
    int id,
    int taskId,
    int authorId,
    @NotNull String content,
    @NotNull Instant postedAt,
    @Nullable Instant updatedAt
) {
    public static final int CONTENT_MAX_LENGTH = 1000;

    /**
     * Проверяет, что указанный текст комментария корректен.
     *
     * @param content текст комментария
     * @throws IllegalArgumentException если текст комментария некорректен
     * @since 1.2.0
     * @author metabrix
     */
    public static void validateContent(@NotNull String content) {
        checkArgument(
            !content.isBlank(),
            "content cannot be blank"
        );
        checkArgument(
            content.trim().length() <= CONTENT_MAX_LENGTH,
            "content cannot be longer than " + CONTENT_MAX_LENGTH + " characters"
        );
    }
}
