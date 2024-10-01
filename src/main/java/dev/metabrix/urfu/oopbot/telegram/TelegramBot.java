package dev.metabrix.urfu.oopbot.telegram;

import dev.metabrix.urfu.oopbot.util.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс-слушатель события Telegram.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class TelegramBot extends TelegramLongPollingBot {
    private static final @NotNull Logger LOGGER = LogUtils.getLogger();

    private final @NotNull String username;
    private final @NotNull UpdateListener updateListener;

    /**
     * Создаёт бота.
     *
     * @param username юзернейм бота
     * @param token API-токен бота
     * @param updateListener обработчик событий API
     * @since 1.0.0
     * @author metabrix
     */
    public TelegramBot(@NotNull String username, @NotNull String token, @NotNull UpdateListener updateListener) {
        super(token);

        this.username = username;
        this.updateListener = updateListener;
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        try {
            if (update.hasMessage()) this.updateListener.handleMessage(update);
            else if (update.hasInlineQuery()) this.updateListener.handleInlineQuery(update);
            else if (update.hasChosenInlineQuery()) this.updateListener.handleChosenInlineQuery(update);
            else if (update.hasCallbackQuery()) this.updateListener.handleCallbackQuery(update);
            else if (update.hasEditedMessage()) this.updateListener.handleEditedMessage(update);
            else if (update.hasChannelPost()) this.updateListener.handleChannelPost(update);
            else if (update.hasEditedChannelPost()) this.updateListener.handleEditedChannelPost(update);
            else if (update.hasShippingQuery()) this.updateListener.handleShippingQuery(update);
            else if (update.hasPreCheckoutQuery()) this.updateListener.handlePreCheckoutQuery(update);
            else if (update.hasPoll()) this.updateListener.handlePoll(update);
            else if (update.hasPollAnswer()) this.updateListener.handlePollAnswer(update);
            else if (update.hasMyChatMember()) this.updateListener.handleMyChatMember(update);
            else if (update.hasChatMember()) this.updateListener.handleChatMember(update);
            else if (update.hasChatJoinRequest()) this.updateListener.handleChatJoinRequest(update);
            // на момент версии org.telegram:telegrambots:6.9.7.1 нет публичного доступа к методам этих апдейтов
//            else if (update.hasMessageReaction()) this.updateListener.handleMessageReaction(update);
//            else if (update.hasMessageReactionCount()) this.updateListener.handleMessageReactionCount(update);
//            else if (update.hasChatBoost()) this.updateListener.handleChatBoost(update);
//            else if (update.hasRemovedChatBoost()) this.updateListener.handleRemovedChatBoost(update);
            else LOGGER.warn("Received unknown update: {}", update);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to handle update ID {}", update.getUpdateId());
        }
    }
}
