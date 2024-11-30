package dev.metabrix.urfu.oopbot.storage;

import dev.metabrix.urfu.oopbot.storage.model.Task;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Хранилище задач.
 * 
 * @since 1.1.0
 * @author metabrix
 */
public interface TaskStorage {
    /**
     * Создаёт задачу.
     *
     * @param chatId внутренний ID чата задачи
     * @param name название задачи
     * @param description описание задачи
     * @param createdById внутренний ID пользователя, который создал задачу
     * @return созданная задача
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull Task create(
        int chatId,
        @NotNull String name,
        @Nullable String description,
        int createdById
    );

    /**
     * Возвращает задачу по ID.
     *
     * @param id ID задачи в хранилище
     * @return задача по ID или {@code null}, если такой задачи нет
     * @since 1.1.0
     * @author metabrix
     */
    @Nullable Task getById(int id);

    /**
     * Возвращает список задач по указанным фильтрам.
     *
     * @param limit максимальное количество задач в списке
     * @param offset смещение в списке
     * @param sort сортировка
     * @param ascending если {@code true}, то сортировка будет по возрастанию, иначе по убыванию
     * @param chatId внутренний ID чата или {@code null}, если по чату фильтровать не нужно
     * @param createdById внутренний ID пользователя, создавшего задачу, или {@code null}, если по пользователю фильтровать не нужно
     * @return список задач по указанным фильтрам
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull List<@NotNull Task> searchTasks(
        int limit, int offset,
        @NotNull Sort sort, boolean ascending,
        @Nullable Integer chatId, @Nullable Integer createdById
    );

    /**
     * Возвращает список задач в чате.
     *
     * @param limit максимальное количество задач в списке
     * @param offset смещение в списке
     * @param sort сортировка
     * @param ascending если {@code true}, то сортировка будет по возрастанию, иначе по убыванию
     * @param chatId внутренний ID чата
     * @return список задач в чате
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull List<@NotNull Task> searchTasksByChatId(
        int limit, int offset,
        @NotNull Sort sort, boolean ascending,
        int chatId
    ) {
        return searchTasks(limit, offset, sort, ascending, chatId, null);
    }

    /**
     * Возвращает количество задач по указанным фильтрам.
     *
     * @param chatId внутренний ID чата или {@code null}, если по чату фильтровать не нужно
     * @param createdById внутренний ID пользователя, создавшего задачу, или {@code null}, если по пользователю фильтровать не нужно
     * @return количество задач по указанным фильтрам
     * @since 1.1.0
     * @author metabrix
     */
    int countTasks(@Nullable Integer chatId, @Nullable Integer createdById);

    /**
     * Возвращает количество задач в чате.
     *
     * @param chatId внутренний ID чата
     * @return количество задач в чате
     * @since 1.1.0
     * @author metabrix
     */
    default int countTasksByChatId(int chatId) {
        return countTasks(chatId, null);
    }

    /**
     * Изменяет название задачи.
     *
     * @param id внутренний ID задачи
     * @param newName новое название задачи
     * @since 1.1.0
     * @author metabrix
     */
    void updateName(int id, @NotNull String newName);

    /**
     * Изменяет описание задачи.
     *
     * @param id внутренний ID задачи
     * @param newDescription новое описание задачи
     * @since 1.1.0
     * @author metabrix
     */
    void updateDescription(int id, @Nullable String newDescription);

    /**
     * Удаляет задачу по ID.
     *
     * @param id ID задачи
     * @since 1.1.0
     * @author metabrix
     */
    void delete(int id);

    /**
     * Свойство задачи для сортировки.
     *
     * @since 1.1.0
     * @author metabrix
     */
    enum Sort {
        CREATED_AT,
        UPDATED_AT(CREATED_AT),
        ;

        private final @Nullable Sort fallback;

        Sort() {
            this(null);
        }

        Sort(@Nullable Sort fallback) {
            this.fallback = fallback;
        }

        /**
         * Возвращает свойство задачи, которое должны использоваться для сортировки, если это свойство окажется {@code null}.
         *
         * @return свойство задачи для сортировки или {@code null}, если у свойства для сортировки нет «запасного»
         * @since 1.1.0
         * @author metabrix
         */
        public @Nullable Sort fallback() {
            return this.fallback;
        }
    }
}
