package dev.metabrix.urfu.oopbot.util.command;

import java.util.NoSuchElementException;

/**
 * Исключение, которое выбрасывается при некорректной позиции курсора.
 *
 * @author metabrix
 * @since 1.0.0
 */
public class CursorOutOfBoundsException extends NoSuchElementException {
    public CursorOutOfBoundsException(final int cursor, final int length) {
        super(String.format("Cursor exceeds the input length (%d > %d)", cursor, length - 1));
    }
}
