package dev.metabrix.urfu.oopbot.interaction;

import dev.metabrix.urfu.oopbot.storage.model.Chat;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Взаимодействие с ботом в чате.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface ChatInteraction extends Interaction {
    /**
     * Возвращает Telegram-чат, в котором было вызвано взаимодействие.
     *
     * @return Telegram-чат
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull org.telegram.telegrambots.meta.api.objects.Chat getTelegramChat();

    /**
     * Возвращает чат, в котором было вызвано взаимодействие, в виде объекта {@link Chat}, если он
     * есть в хранилище данных, иначе {@code null}.
     *
     * @return чат или {@code null}, если его нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @Nullable Chat getChatIfExists() {
        org.telegram.telegrambots.meta.api.objects.Chat telegramChat = this.getTelegramChat();
        return this.getStorage().chats().getByTelegramId(telegramChat.getId());
    }

    /**
     * Возвращает чат, в котором было вызвано взаимодействие, в виде объекта {@link Chat}.
     * <p>
     * Этот метод ожидает, что чат был добавлен в хранилище данных до передачи управления
     * в обработчик взаимодействия.
     *
     * @return чат
     * @throws NoSuchElementException если чата нет в хранилище данных
     * @since 1.1.0
     * @author metabrix
     */
    default @NotNull Chat getChat() {
        Chat chat = this.getChatIfExists();
        if (chat == null) throw new NoSuchElementException("Chat " + this.getTelegramChat() + " not found in data storage");
        return chat;
    }
}
