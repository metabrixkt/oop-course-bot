package dev.metabrix.urfu.oopbot.util.command;

import org.jetbrains.annotations.NotNull;

public class CommandInputImpl implements CommandInput {
    private final String rawInput;
    private int cursor;

    CommandInputImpl(final @NotNull String rawInput) {
        this(rawInput, 0);
    }

    CommandInputImpl(final @NotNull String rawInput, final int cursor) {
        this.rawInput = rawInput;
        this.cursor = cursor;
    }

    @Override
    public @NotNull String getRawInput() {
        return this.rawInput;
    }

    @Override
    public @NotNull CommandInput appendString(@NotNull String string) {
        return new CommandInputImpl(this.rawInput + string, this.cursor);
    }

    @Override
    public int getCursor() {
        return this.cursor;
    }

    @Override
    public void moveCursor(final int chars) {
        if (this.getCursor() + chars > this.getRawLength()) {
            throw new CursorOutOfBoundsException(this.getCursor() + chars, this.getRawLength());
        }
        this.cursor += chars;
    }

    @Override
    public @NotNull CommandInput setCursor(final int cursor) {
        if (cursor < 0 || cursor > this.getRawLength()) {
            throw new CursorOutOfBoundsException(cursor, this.getRawLength());
        }
        this.cursor = cursor;
        return this;
    }

    @Override
    public @NotNull CommandInput copy() {
        return new CommandInputImpl(this.rawInput, this.cursor);
    }
}
