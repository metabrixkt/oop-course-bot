package dev.metabrix.urfu.oopbot.util.command;

import dev.metabrix.urfu.oopbot.BotApplication;
import dev.metabrix.urfu.oopbot.BotConfiguration;
import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import dev.metabrix.urfu.oopbot.interaction.impl.MessageInteractionImpl;
import java.util.HashMap;
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
    public void testInteraction() {
        // Arrange
        MessageInteraction expectedInteraction = buildMockMessageInteraction();
        CommandContext commandContext = new CommandContextImpl(expectedInteraction);

        // Act
        MessageInteraction result = commandContext.getInteraction();

        // Assert
        assertEquals(result, expectedInteraction);
    }

    @ParameterizedTest
    @MethodSource("sourceTestCommandInput")
    public void testCommandInput(@NotNull String messageText, @NotNull String expectedInput) {
        // Arrange
        Update update = new Update();
        Message message = new Message();
        message.setText(messageText);
        update.setMessage(message);
        CommandContext commandContext = new CommandContextImpl(new MessageInteractionImpl(buildMockApplication(), update, Update::getMessage));

        // Act
        String result = commandContext.getCommandInput().getRawInput();

        // Assert
        assertEquals(result, expectedInput);
    }

    private static @NotNull Stream<@NotNull Arguments> sourceTestCommandInput() {
        return Stream.of(
            arguments("spaghetti monster", "spaghetti monster"),
            arguments("/spaghetti monster", "spaghetti monster"),
            arguments("//spaghetti monster", "/spaghetti monster")
        );
    }

    private static @NotNull MessageInteraction buildMockMessageInteraction() {
        return new MessageInteractionImpl(buildMockApplication(), buildMessageMockUpdate(), Update::getMessage);
    }

    private static @NotNull BotApplication buildMockApplication() {
        return new BotApplication(new BotConfiguration(
            new BotConfiguration.BotInfo("username", "token"),
            new BotConfiguration.Console(false),
            new BotConfiguration.DataStorage(BotConfiguration.DataStorage.Type.MYSQL, new HashMap<>())
        ));
    }

    private static @NotNull Update buildMessageMockUpdate() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/spaghetti monster");
        update.setMessage(message);
        return update;
    }
}
