package dev.metabrix.urfu.oopbot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChecksTest {
    @Test
    public void testCheckNotNull() {
        assertEquals(
            assertThrows(NullPointerException.class, () -> Checks.checkNotNull(null, "message")).getMessage(),
            "message"
        );
        assertDoesNotThrow(() -> Checks.checkNotNull("potato", "message"));
    }

    @Test
    public void testCheckArgument() {
        assertEquals(
            assertThrows(IllegalArgumentException.class, () -> Checks.checkArgument(false, "message")).getMessage(),
            "message"
        );
        assertDoesNotThrow(() -> Checks.checkArgument(true, "message"));
    }

    @Test
    public void testCheckState() {
        assertEquals(
            assertThrows(IllegalStateException.class, () -> Checks.checkState(false, "message")).getMessage(),
            "message"
        );
        assertDoesNotThrow(() -> Checks.checkState(true, "message"));
    }
}
