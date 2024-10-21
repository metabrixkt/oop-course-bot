package dev.metabrix.urfu.oopbot.util;

import org.jetbrains.annotations.NotNull;

/**
 * Эмодзи.
 *
 * @since 1.0.0
 * @author metabrix
 */
public enum Emoji {
    X("\u274c"),
    WHITE_CHECK_MARK("\u2705"),
    NOTEBOOK("\ud83d\udcd4"),
    EYES("\ud83d\udc40")
    ;

    private final @NotNull String unicode;

    Emoji(@NotNull String unicode) {
        this.unicode = unicode;
    }

    public @NotNull String getUnicode() {
        return this.unicode;
    }

    @Override
    public String toString() {
        return this.unicode;
    }
}
