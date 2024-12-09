package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import org.jetbrains.annotations.NotNull;

public class CommandContextImpl implements CommandContext {
    private final @NotNull MessageInteraction interaction;
    private final @NotNull CommandInput input;

    public CommandContextImpl(@NotNull MessageInteraction interaction) {
        this.interaction = interaction;

        String rawInput = this.getMessage().getText();
        if (rawInput.startsWith("/")) rawInput = rawInput.substring(1);
        this.input = CommandInput.of(rawInput);
    }

    @Override
    public @NotNull MessageInteraction getInteraction() {
        return this.interaction;
    }

    @Override
    public @NotNull CommandInput getCommandInput() {
        return this.input;
    }

    @Override
    public String toString() {
        return "CommandContextImpl{interaction=%s, input=%s}".formatted(this.getInteraction(), this.getCommandInput());
    }
}
