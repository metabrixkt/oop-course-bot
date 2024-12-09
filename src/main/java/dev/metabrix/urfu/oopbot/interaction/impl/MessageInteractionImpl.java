package dev.metabrix.urfu.oopbot.interaction.impl;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageInteractionImpl implements MessageInteraction {
    private final @NotNull BotApplication application;
    private final @NotNull Update update;
    private final @NotNull Function<@NotNull Update, @NotNull Message> messageFunction;

    public MessageInteractionImpl(
        @NotNull BotApplication application,
        @NotNull Update update,
        @NotNull Function<@NotNull Update, @NotNull Message> messageFunction
    ) {
        this.application = application;
        this.update = update;
        this.messageFunction = messageFunction;
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
    public @NotNull Message getMessage() {
        return this.messageFunction.apply(this.getUpdate());
    }
}
