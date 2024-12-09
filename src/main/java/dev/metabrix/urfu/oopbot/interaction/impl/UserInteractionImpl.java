package dev.metabrix.urfu.oopbot.interaction.impl;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.interaction.UserInteraction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserInteractionImpl implements UserInteraction {
    private final @NotNull BotApplication application;
    private final @NotNull Update update;
    private final @NotNull Function<@NotNull Update, @NotNull User> userFunction;

    public UserInteractionImpl(
        @NotNull BotApplication application,
        @NotNull Update update,
        @NotNull Function<@NotNull Update, @NotNull User> userFunction
    ) {
        this.application = application;
        this.update = update;
        this.userFunction = userFunction;
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
    public @NotNull User getTelegramUser() {
        return this.userFunction.apply(this.getUpdate());
    }
}
