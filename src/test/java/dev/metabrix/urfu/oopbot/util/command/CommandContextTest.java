package dev.metabrix.urfu.oopbot.util.command;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CommandContextTest {
    @Test
    public void testRawUpdate() {
        // Arrange
        Update expectedUpdate = new Update();
        Message message = new Message();
        message.setText("spaghetti monster");
        expectedUpdate.setMessage(message);
        CommandContext commandContext = new CommandContextImpl(expectedUpdate);

        // Act
        Update result = commandContext.getRawUpdate();

        // Assert
        assertEquals(result, expectedUpdate);
    }

    @ParameterizedTest
    @MethodSource("sourceTestCommandInput")
    public void testCommandInput(@NotNull Update update, @NotNull String expectedInput) {
        // Arrange
        CommandContext commandContext = new CommandContextImpl(update);

        // Act
        String result = commandContext.getCommandInput().getRawInput();

        // Assert
        assertEquals(result, expectedInput);
    }

    private static @NotNull Stream<@NotNull Arguments> sourceTestCommandInput() {
        Update update1 = new Update();
        Message message1 = new Message();
        message1.setText("spaghetti monster");
        update1.setMessage(message1);

        Update update2 = new Update();
        Message message2 = new Message();
        message2.setText("/spaghetti monster");
        update2.setMessage(message2);

        Update update3 = new Update();
        Message message3 = new Message();
        message3.setText("//spaghetti monster");
        update3.setMessage(message3);

        return Stream.of(
            arguments(update1, "spaghetti monster"),
            arguments(update2, "spaghetti monster"),
            arguments(update3, "/spaghetti monster")
        );
    }
}
