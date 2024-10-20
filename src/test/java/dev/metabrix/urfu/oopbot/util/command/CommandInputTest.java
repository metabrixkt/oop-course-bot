package dev.metabrix.urfu.oopbot.util.command;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CommandInputTest {
    @ParameterizedTest
    @ValueSource(strings = {"spaghetti monster! ", "oh hello", "cool"})
    public void testRawInput(@NotNull String rawInput) {
        // Arrange
        CommandInput commandInput = CommandInput.of(rawInput);

        // Act
        String result = commandInput.getRawInput();

        // Assert
        assertEquals(result, rawInput);
    }

    @ParameterizedTest
    @ValueSource(strings = {"spaghetti monster! ", "oh hello", "cool"})
    public void testRawInputLength(@NotNull String rawInput) {
        // Arrange
        CommandInput commandInput = CommandInput.of(rawInput);

        // Act
        int result = commandInput.getRawLength();

        // Assert
        assertEquals(result, rawInput.length());
    }

    @Test
    public void testCursorMovement() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");

        // Act
        commandInput.moveCursor(10);

        // Assert
        assertEquals(commandInput.getCursor(), 10);
        assertEquals(commandInput.getRemainingInput(), "monster");
    }

    @Test
    public void testCursorRestrictions() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");

        // Act
        CursorOutOfBoundsException exception = assertThrows(
            CursorOutOfBoundsException.class,
            () -> commandInput.moveCursor(10)
        );

        // Assert
        assertNotNull(exception);
    }

    @Test
    public void testPeekString() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        String result = commandInput.peekString(7);

        // Assert
        assertEquals(result, "monster");
        assertEquals(commandInput.getCursor(), 10);
    }

    @Test
    public void testPeekStringOutOfBounds() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        CursorOutOfBoundsException exception = assertThrows(
            CursorOutOfBoundsException.class,
            () -> commandInput.peekString(8)
        );

        // Assert
        assertNotNull(exception);
    }

    @Test
    public void testReadString() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        String result = commandInput.readString(7);

        // Assert
        assertEquals(result, "monster");
        assertEquals(commandInput.getCursor(), 17);
    }

    @Test
    public void testReadStringOutOfBounds() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        CursorOutOfBoundsException exception = assertThrows(
            CursorOutOfBoundsException.class,
            () -> commandInput.readString(8)
        );

        // Assert
        assertNotNull(exception);
    }

    @Test
    public void testPeekChar() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        char result = commandInput.peekChar();

        // Assert
        assertEquals(result, 'm');
        assertEquals(commandInput.getCursor(), 10);
    }

    @Test
    public void testPeekCharOutOfBounds() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");
        commandInput.moveCursor(9);

        // Act
        CursorOutOfBoundsException exception = assertThrows(
            CursorOutOfBoundsException.class,
            commandInput::peekChar
        );

        // Assert
        assertNotNull(exception);
    }

    @Test
    public void testReadChar() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster");
        commandInput.moveCursor(10);

        // Act
        char result = commandInput.readChar();

        // Assert
        assertEquals(result, 'm');
        assertEquals(commandInput.getCursor(), 11);
    }

    @Test
    public void testReadCharOutOfBounds() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");
        commandInput.moveCursor(9);

        // Act
        CursorOutOfBoundsException exception = assertThrows(
            CursorOutOfBoundsException.class,
            commandInput::readChar
        );

        // Assert
        assertNotNull(exception);
    }

    @Test
    public void testPeekTokenOnEmptyInputIsEmpty() {
        // Arrange
        CommandInput commandInput = CommandInput.empty();

        // Act
        String result = commandInput.peekToken();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPeekTokenOnOneTokenReturnsToken() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");

        // Act
        String result = commandInput.peekToken();

        // Assert
        assertEquals(result, "spaghetti");
        assertEquals(commandInput.getCursor(), 0);
    }

    @Test
    public void testPeekTokenOnMultipleTokensReturnsFirstToken() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster attacks");

        // Act
        String result = commandInput.peekToken();

        // Assert
        assertEquals(result, "spaghetti");
        assertEquals(commandInput.getCursor(), 0);
    }

    @ParameterizedTest
    @MethodSource("sourceTestPeekTokenIgnoresLeadingSpaces")
    public void testPeekTokenIgnoresLeadingSpaces(@NotNull String rawInput, @NotNull String expectedResult) {
        // Arrange
        CommandInput commandInput = CommandInput.of(rawInput);

        // Act
        String result = commandInput.peekToken();

        // Assert
        assertEquals(result, expectedResult);
    }

    private static @NotNull Stream<@NotNull Arguments> sourceTestPeekTokenIgnoresLeadingSpaces() {
        return Stream.of(
            arguments(" spaghetti monster ", "spaghetti"),
            arguments("  spaghetti monster", "spaghetti"),
            arguments(" ", ""),
            arguments("  ", "")
        );
    }

    @Test
    public void testReadsEmptyTokenOnEmptyInput() {
        // Arrange
        CommandInput commandInput = CommandInput.empty();

        // Act
        String result = commandInput.readToken();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testReadTokenOnOneTokenReturnsToken() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");

        // Act
        String result = commandInput.readToken();

        // Assert
        assertEquals(result, "spaghetti");
        assertTrue(commandInput.getRemainingInput().isEmpty());
        assertEquals(commandInput.getCursor(), 9);
        assertEquals(commandInput.getRemainingLength(), 0);
        assertEquals(commandInput.getReadInput(), "spaghetti");
    }

    @Test
    public void testReadTokenOnMultipleTokensReturnsFirstTokenAndKeepsSpace() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti monster attacks");

        // Act
        String result = commandInput.readToken();

        // Assert
        assertEquals(result, "spaghetti");
        assertEquals(commandInput.getRemainingInput(), " monster attacks");
        assertEquals(commandInput.getCursor(), 9);
        assertEquals(commandInput.getRemainingLength(), 16);
        assertEquals(commandInput.getReadInput(), "spaghetti");
    }

    @Test
    public void testReadTokensWithLeadingSpaceReturnsFirstTokenAndKeepsSpace() {
        // Arrange
        CommandInput commandInput = CommandInput.of("     spaghetti monster attacks");

        // Act
        String result = commandInput.readToken();

        // Assert
        assertEquals(result, "spaghetti");
        assertEquals(commandInput.getRemainingInput(), " monster attacks");
        assertEquals(commandInput.getCursor(), 14);
        assertEquals(commandInput.getRemainingLength(), 16);
        assertEquals(commandInput.getReadInput(), "     spaghetti");
    }

    @Test
    public void testSkipWhitespaceNonBlankInput() {
        // Arrange
        CommandInput commandInput = CommandInput.of("  spaghetti");

        // Act
        commandInput.skipWhitespace();

        // Assert
        assertEquals(commandInput.getRemainingInput(), "spaghetti");
    }

    @Test
    public void testSkipWhitespaceBlankInput() {
        // Arrange
        CommandInput commandInput = CommandInput.of("  ");

        // Act
        commandInput.skipWhitespace();

        // Assert
        assertTrue(commandInput.getRemainingInput().isEmpty());
    }

    @Test
    public void testSkipWhitespaceDoesNotSkipNonWhitespace() {
        // Arrange
        CommandInput commandInput = CommandInput.of("spaghetti");

        // Act
        commandInput.skipWhitespace();

        // Assert
        assertEquals(commandInput.getRemainingInput(), "spaghetti");
    }

    @Test
    public void testSkipWhitespaceLimit() {
        // Arrange
        CommandInput commandInput = CommandInput.of("   spaghetti");

        // Act
        commandInput.skipWhitespace(1);

        // Assert
        assertEquals(commandInput.getRemainingInput(), "  spaghetti");
    }
}
