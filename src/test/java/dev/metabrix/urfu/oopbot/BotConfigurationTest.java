package dev.metabrix.urfu.oopbot;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BotConfigurationTest {
    @Test
    public void testBotInfo() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.BotInfo.fromConfig(ConfigFactory.parseString(
                """
                username = ""
                token = "potato"
                """
            ), "")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.BotInfo.fromConfig(ConfigFactory.parseString(
                """
                username = "potato"
                token = ""
                """
            ), "")
        );
        assertDoesNotThrow(() -> BotConfiguration.BotInfo.fromConfig(ConfigFactory.parseString(
            """
            username = "potato"
            token = "potato"
            """
        ), ""));
    }

    @Test
    public void testConsole() {
        assertThrows(
            ConfigException.WrongType.class,
            () -> BotConfiguration.Console.fromConfig(ConfigFactory.parseString(
                """
                enabled = "potato"
                """
            ))
        );
        assertDoesNotThrow(() -> BotConfiguration.Console.fromConfig(ConfigFactory.parseString(
            """
            enabled = true
            """
        )));
    }
}
