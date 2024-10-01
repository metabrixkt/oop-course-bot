package dev.metabrix.urfu.oopbot.telegram;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработчик событий бота.
 *
 * @since 1.0.0
 * @author metabrix
 */
public interface UpdateListener {
    default void handleMessage(@NotNull Update update) throws TelegramApiException {}

    default void handleInlineQuery(@NotNull Update update) throws TelegramApiException {}

    default void handleChosenInlineQuery(@NotNull Update update) throws TelegramApiException {}

    default void handleCallbackQuery(@NotNull Update update) throws TelegramApiException {}

    default void handleEditedMessage(@NotNull Update update) throws TelegramApiException {}

    default void handleChannelPost(@NotNull Update update) throws TelegramApiException {}

    default void handleEditedChannelPost(@NotNull Update update) throws TelegramApiException {}

    default void handleShippingQuery(@NotNull Update update) throws TelegramApiException {}

    default void handlePreCheckoutQuery(@NotNull Update update) throws TelegramApiException {}

    default void handlePoll(@NotNull Update update) throws TelegramApiException {}

    default void handlePollAnswer(@NotNull Update update) throws TelegramApiException {}

    default void handleMyChatMember(@NotNull Update update) throws TelegramApiException {}

    default void handleChatMember(@NotNull Update update) throws TelegramApiException {}

    default void handleChatJoinRequest(@NotNull Update update) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleMessageReaction(@NotNull Update update) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleMessageReactionCount(@NotNull Update update) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleChatBoost(@NotNull Update update) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleRemovedChatBoost(@NotNull Update update) throws TelegramApiException {}
}
