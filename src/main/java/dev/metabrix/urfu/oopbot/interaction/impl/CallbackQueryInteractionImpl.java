package dev.metabrix.urfu.oopbot.interaction.impl;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.interaction.CallbackQueryInteraction;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallbackQueryInteractionImpl implements CallbackQueryInteraction {
    private final @NotNull BotApplication application;
    private final @NotNull Update update;

    public CallbackQueryInteractionImpl(@NotNull BotApplication application, @NotNull Update update) {
        this.application = application;
        this.update = update;
    }

    @Override
    public @NotNull BotApplication getApplication() {
        return this.application;
    }

    @Override
    public @NotNull Update getUpdate() {
        return this.update;
    }

    @Override
    public @NotNull CallbackQuery getQuery() {
        return this.update.getCallbackQuery();
    }
}
