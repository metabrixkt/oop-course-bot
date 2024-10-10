package dev.metabrix.urfu.oopbot.util.command;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandContextImpl implements CommandContext {
    private final @NotNull Update rawUpdate;
    private final @NotNull CommandInput input;

    public CommandContextImpl(@NotNull Update rawUpdate) {
        this.rawUpdate = rawUpdate;

        String rawInput = this.getMessage().getText();
        if (rawInput.startsWith("/")) rawInput = rawInput.substring(1);
        this.input = CommandInput.of(rawInput);
    }

    @Override
    public @NotNull Update getRawUpdate() {
        return this.rawUpdate;
    }

    @Override
    public @NotNull CommandInput getCommandInput() {
        return this.input;
    }
}
