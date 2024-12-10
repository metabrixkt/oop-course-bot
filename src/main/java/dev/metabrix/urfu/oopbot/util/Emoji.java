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
    EYES("\ud83d\udc40"),
    PAGE_FACING_UP("\ud83d\udcc4"),
    WRITING_HAND("\u270d"),
    PARTY_POPPER("\ud83c\udf89"),
    KEYCAP_SUFFIX("\ufe0f\u20e3"),
    EXCLAMATION_MARK("\u2757"),
    TRASH_BIN("\ud83d\uddd1"),
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
