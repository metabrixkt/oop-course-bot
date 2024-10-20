package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.BotApplication;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandContextImpl implements CommandContext {
    private final @NotNull BotApplication application;
    private final @NotNull Update rawUpdate;
    private final @NotNull CommandInput input;

    public CommandContextImpl(@NotNull BotApplication application, @NotNull Update rawUpdate) {
        this.application = application;
        this.rawUpdate = rawUpdate;

        String rawInput = this.getMessage().getText();
        if (rawInput.startsWith("/")) rawInput = rawInput.substring(1);
        this.input = CommandInput.of(rawInput);
    }

    @Override
    public @NotNull BotApplication getApplication() {
        return this.application;
    }

    @Override
    public @NotNull Update getRawUpdate() {
        return this.rawUpdate;
    }

    @Override
    public @NotNull CommandInput getCommandInput() {
        return this.input;
    }

    @Override
    public String toString() {
        return "CommandContextImpl{application=%s, rawUpdate=%s, input=%s}".formatted(this.getApplication(), this.getRawUpdate(), this.getCommandInput());
    }
}
