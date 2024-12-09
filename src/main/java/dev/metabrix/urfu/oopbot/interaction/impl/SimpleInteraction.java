package dev.metabrix.urfu.oopbot.interaction.impl;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.interaction.Interaction;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SimpleInteraction implements Interaction {
    private final @NotNull BotApplication application;
    private final @NotNull Update update;

    public SimpleInteraction(@NotNull BotApplication application, @NotNull Update update) {
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
}
