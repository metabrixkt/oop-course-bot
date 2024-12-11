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

    @Test
    public void testDataStorage() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.fromConfig(ConfigFactory.parseString(
                """
                type = "potato"
                """
            ), "")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.fromConfig(ConfigFactory.parseString(
                """
                type = "mysql"
                """
            ), "")
        );

        assertDoesNotThrow(() -> BotConfiguration.DataStorage.fromConfig(ConfigFactory.parseString(
            """
            type = "mysql"
            
            mysql {
                host = "localhost"
                port = 3306
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
            }
            """
        ), ""));
    }

    @Test
    public void testDataStorageMySQL() {
        // test empty host
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = ""
                port = 3306
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test empty string as port
        assertThrows(
            ConfigException.WrongType.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "potato"
                port = ""
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test no port (defaults to 3306)
        assertDoesNotThrow(() -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
            """
            host = "localhost"
            database = "potato"
            username = "potato"
            password = "potato"
            table-prefix = "potato"
            pool-size = 4
            """
        ), ""));
        // test invalid ports
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 0
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 65536
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        assertThrows(
            ConfigException.WrongType.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = "potato"
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test empty database
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 3306
                database = ""
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test empty username
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 3306
                database = "potato"
                username = ""
                password = "potato"
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test empty password
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 3306
                database = "potato"
                username = "potato"
                password = ""
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test no table prefix (defaults to "tt_")
        assertDoesNotThrow(() -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
            """
            host = "localhost"
            port = 3306
            database = "potato"
            username = "potato"
            password = "potato"
            pool-size = 4
            """
        ), ""));
        // test no pool size (defaults to 4)
        assertDoesNotThrow(() -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
            """
            host = "localhost"
            port = 3306
            database = "potato"
            username = "potato"
            password = "potato"
            table-prefix = "potato"
            """
        ), ""));
        // test invalid pool sizes
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 3306
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = 0
                """
            ), "")
        );
        assertThrows(
            ConfigException.WrongType.class,
            () -> BotConfiguration.DataStorage.MySQLConfiguration.fromConfig(ConfigFactory.parseString(
                """
                host = "localhost"
                port = 3306
                database = "potato"
                username = "potato"
                password = "potato"
                table-prefix = "potato"
                pool-size = "potato"
                """
            ), "")
        );
    }

    @Test
    public void testDataStorageSQLite() {
        // test empty database file path
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.SQLiteConfiguration.fromConfig(ConfigFactory.parseString(
                """
                database-file-path = ""
                table-prefix = "potato"
                pool-size = 4
                """
            ), "")
        );
        // test no table prefix (defaults to "tt_")
        assertDoesNotThrow(() -> BotConfiguration.DataStorage.SQLiteConfiguration.fromConfig(ConfigFactory.parseString(
            """
            database-file-path = "database.db"
            pool-size = 4
            """
        ), ""));
        // test no pool size (defaults to 4)
        assertDoesNotThrow(() -> BotConfiguration.DataStorage.SQLiteConfiguration.fromConfig(ConfigFactory.parseString(
            """
            database-file-path = "database.db"
            table-prefix = "potato"
            """
        ), ""));
        // test invalid pool sizes
        assertThrows(
            IllegalArgumentException.class,
            () -> BotConfiguration.DataStorage.SQLiteConfiguration.fromConfig(ConfigFactory.parseString(
                """
                database-file-path = "database.db"
                table-prefix = "potato"
                pool-size = 0
                """
            ), "")
        );
        assertThrows(
            ConfigException.WrongType.class,
            () -> BotConfiguration.DataStorage.SQLiteConfiguration.fromConfig(ConfigFactory.parseString(
                """
                database-file-path = "database.db"
                table-prefix = "potato"
                pool-size = "potato"
                """
            ), "")
        );
    }
}
