package dev.metabrix.urfu.oopbot.interaction;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.storage.DataStorage;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Взаимодействие с ботом.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface Interaction {
    /**
     * Возвращает приложение бота.
     *
     * @return главный класс приложения
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull BotApplication getApplication();

    /**
     * Возвращает хранилище данных.
     *
     * @return хранилище
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull DataStorage getStorage() {
        return this.getApplication().getStorage();
    }

    /**
     * Возвращает событие Telegram API.
     *
     * @return событие
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull org.telegram.telegrambots.meta.api.objects.Update getUpdate();

    /**
     * Асинхронно отправляет запрос в Telegram API и возвращает ответ в виде {@link CompletableFuture}.
     *
     * @param request запрос
     * @param <T> тип ответа
     * @return будущий ответ
     * @since 1.1.0
     * @author metabrix
     */
    default <T extends Serializable> @NotNull CompletableFuture<T> executeAsync(@NotNull BotApiMethod<T> request) {
        try {
            return this.getApplication().getBot().executeAsync(request);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Синхронно отправляет запрос в Telegram API и возвращает ответ.
     *
     * @param request запрос
     * @param <T> тип ответа
     * @return ответ
     * @throws TelegramApiException в случае ошибки Telegram API
     * @since 1.1.0
     * @author metabrix
     */
    default <T extends Serializable> T execute(@NotNull BotApiMethod<T> request) throws TelegramApiException {
        return this.getApplication().getBot().execute(request);
    }

}
