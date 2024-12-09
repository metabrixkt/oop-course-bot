package dev.metabrix.urfu.oopbot.interaction;

import dev.metabrix.urfu.oopbot.storage.model.User;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Взаимодействие пользователя с ботом.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface UserInteraction extends Interaction {
    /**
     * Возвращает Telegram-пользователя, вызвавшего взаимодействие.
     *
     * @return Telegram-пользователь
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull org.telegram.telegrambots.meta.api.objects.User getTelegramUser();

    /**
     * Возвращает пользователя, вызвавшего взаимодействие, в виде объекта {@link User}, если он есть
     * в хранилище данных.
     *
     * @return пользователь или {@code null}, если его нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @Nullable User getUserIfExists() {
        org.telegram.telegrambots.meta.api.objects.User telegramUser = this.getTelegramUser();
        return this.getStorage().users().getByTelegramId(telegramUser.getId());
    }

    /**
     * Возвращает пользователя, вызвавшего взаимодействие, в виде объекта {@link User}.
     * <p>
     * Этот метод ожидает, что пользователь был добавлен в хранилище данных до передачи
     * управления в обработчик взаимодействия.
     *
     * @return пользователь
     * @throws NoSuchElementException если пользователя нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull User getUser() {
        User user = this.getUserIfExists();
        if (user == null) throw new NoSuchElementException("User " + this.getTelegramUser() + " not found in data storage");
        return user;
    }
}
