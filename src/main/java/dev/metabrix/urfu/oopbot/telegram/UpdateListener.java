package dev.metabrix.urfu.oopbot.telegram;

import dev.metabrix.urfu.oopbot.interaction.CallbackQueryInteraction;
import dev.metabrix.urfu.oopbot.interaction.Interaction;
import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработчик событий бота.
 *
 * @since 1.0.0
 * @author metabrix
 */
public interface UpdateListener {
    default void handleMessage(@NotNull MessageInteraction interaction) throws TelegramApiException {}

    default void handleInlineQuery(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handleChosenInlineQuery(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handleCallbackQuery(@NotNull CallbackQueryInteraction interaction) throws TelegramApiException {}

    default void handleEditedMessage(@NotNull MessageInteraction interaction) throws TelegramApiException {}

    default void handleChannelPost(@NotNull MessageInteraction interaction) throws TelegramApiException {}

    default void handleEditedChannelPost(@NotNull MessageInteraction interaction) throws TelegramApiException {}

    default void handleShippingQuery(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handlePreCheckoutQuery(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handlePoll(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handlePollAnswer(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handleMyChatMember(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handleChatMember(@NotNull Interaction interaction) throws TelegramApiException {}

    default void handleChatJoinRequest(@NotNull Interaction interaction) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleMessageReaction(@NotNull Interaction interaction) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleMessageReactionCount(@NotNull Interaction interaction) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleChatBoost(@NotNull Interaction interaction) throws TelegramApiException {}

    @ApiStatus.Experimental
    default void handleRemovedChatBoost(@NotNull Interaction interaction) throws TelegramApiException {}
}
