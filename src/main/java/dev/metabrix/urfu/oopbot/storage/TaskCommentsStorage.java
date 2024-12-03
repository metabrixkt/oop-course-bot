package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.storage.model.TaskComment;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранилище комментариев задач.
 * 
 * @since 1.1.1
 * @author metabrix
 */
public interface TaskCommentsStorage {
    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId внутренний ID задачи
     * @param authorId внутренний ID автора комментария
     * @param content текст комментария
     * @return созданный комментарий
     * @since 1.1.1
     * @author metabrix
     */
    @NotNull TaskComment create(
        int taskId,
        int authorId,
        @NotNull String content
    );

    /**
     * Возвращает комментарий по ID.
     *
     * @param id внутренний ID комментария
     * @return комментарий по ID или {@code null}, если такого комментария нет
     * @since 1.1.1
     * @author metabrix
     */
    @Nullable TaskComment getById(int id);

    /**
     * Возвращает список комментариев к задаче по её ID.
     *
     * @param taskId внутренний ID задачи
     * @param limit максимальное количество комментариев в списке
     * @param offset смещение в списке
     * @param newerFirst если {@code true}, комментарии будут сортироваться от новых к старым,
     *        иначе в обратном порядке
     * @return список комментариев к задаче по её ID
     * @since 1.1.1
     * @author metabrix
     */
    @NotNull List<@NotNull TaskComment> getByTaskId(int taskId, int limit, int offset, boolean newerFirst);

    /**
     * Возвращает количество комментариев к задаче по её ID.
     *
     * @param taskId внутренний ID задачи
     * @return количество комментариев к задаче по её ID
     * @since 1.1.1
     * @author metabrix
     */
    int countByTaskId(int taskId);

    /**
     * Изменяет текст комментария.
     *
     * @param id внутренний ID комментария
     * @param content текст комментария
     * @since 1.1.1
     * @author metabrix
     */
    void updateContent(int id, @NotNull String content);

    /**
     * Удаляет комментарий по ID.
     *
     * @param id ID комментария
     * @since 1.1.1
     * @author metabrix
     */
    void delete(int id);
}
