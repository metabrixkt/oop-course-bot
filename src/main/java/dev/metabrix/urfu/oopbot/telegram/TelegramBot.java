package dev.metabrix.urfu.oopbot.telegram;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.interaction.impl.CallbackQueryInteractionImpl;
import dev.metabrix.urfu.oopbot.interaction.impl.MessageInteractionImpl;
import dev.metabrix.urfu.oopbot.interaction.impl.SimpleInteraction;
import dev.metabrix.urfu.oopbot.interaction.impl.UserInteractionImpl;
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

    private final @NotNull BotApplication application;
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
    public TelegramBot(
        @NotNull BotApplication application,
        @NotNull String username, @NotNull String token,
        @NotNull UpdateListener updateListener
    ) {
        super(token);

        this.application = application;
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
            if (update.hasMessage()) {
                this.updateListener.handleMessage(new MessageInteractionImpl(this.application, update, Update::getMessage));
            } else if (update.hasInlineQuery()) {
                this.updateListener.handleInlineQuery(new UserInteractionImpl(this.application, update, u -> u.getInlineQuery().getFrom()));
            } else if (update.hasChosenInlineQuery()) {
                this.updateListener.handleChosenInlineQuery(new UserInteractionImpl(this.application, update, u -> u.getChosenInlineQuery().getFrom()));
            } else if (update.hasCallbackQuery()) {
                this.updateListener.handleCallbackQuery(new CallbackQueryInteractionImpl(this.application, update));
            } else if (update.hasEditedMessage()) {
                this.updateListener.handleEditedMessage(new MessageInteractionImpl(this.application, update, Update::getEditedMessage));
            } else if (update.hasChannelPost()) {
                this.updateListener.handleChannelPost(new MessageInteractionImpl(this.application, update, Update::getChannelPost));
            } else if (update.hasEditedChannelPost()) {
                this.updateListener.handleEditedChannelPost(new MessageInteractionImpl(this.application, update, Update::getEditedChannelPost));
            } else if (update.hasShippingQuery()) {
                this.updateListener.handleShippingQuery(new UserInteractionImpl(this.application, update, u -> u.getShippingQuery().getFrom()));
            } else if (update.hasPreCheckoutQuery()) {
                this.updateListener.handlePreCheckoutQuery(new UserInteractionImpl(this.application, update, u -> u.getPreCheckoutQuery().getFrom()));
            } else if (update.hasPoll()) {
                this.updateListener.handlePoll(new SimpleInteraction(this.application, update));
            } else if (update.hasPollAnswer()) {
                this.updateListener.handlePollAnswer(new UserInteractionImpl(this.application, update, u -> u.getPollAnswer().getUser()));
            } else if (update.hasMyChatMember()) {
                this.updateListener.handleMyChatMember(new UserInteractionImpl(this.application, update, u -> u.getMyChatMember().getFrom()));
            } else if (update.hasChatMember()) {
                this.updateListener.handleChatMember(new UserInteractionImpl(this.application, update, u -> u.getChatMember().getFrom()));
            } else if (update.hasChatJoinRequest()) {
                this.updateListener.handleChatJoinRequest(new UserInteractionImpl(this.application, update, u -> u.getChatJoinRequest().getUser()));
            }
            // на момент версии org.telegram:telegrambots:6.9.7.1 нет публичного доступа к методам этих апдейтов
//            else if (update.hasMessageReaction()) {
//                this.updateListener.handleMessageReaction(new UserInteractionImpl(this.application, update, u -> u.getMessageReaction().getUser()));
//            } else if (update.hasMessageReactionCount()) {
//                this.updateListener.handleMessageReactionCount(new SimpleInteraction(this.application, update));
//            } else if (update.hasChatBoost()) {
//                this.updateListener.handleChatBoost(new SimpleInteraction(this.application, update));
//            } else if (update.hasRemovedChatBoost()) {
//                this.updateListener.handleRemovedChatBoost(new SimpleInteraction(this.application, update));
//            }
            else LOGGER.warn("Received unknown update: {}", update);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to handle update ID {}", update.getUpdateId());
        }
    }
}
