package dev.metabrix.urfu.oopbot.storage.model.dialog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DialogStateTypeTest {
    @ParameterizedTest
    @MethodSource("sourceTestJsonSerialization")
    @EnabledIf("enabledTestJsonSerialization")
    public void testJsonSerialization(@NotNull DialogStateType type, @NotNull JSONObject expectedJson) {
        // Act
        DialogState state = type.deserializeFromJson(expectedJson);
        JSONObject actualJson = state.toJson();

        // Assert
        assertEquals(expectedJson, actualJson);
    }

    private static @NotNull Stream<@NotNull Arguments> sourceTestJsonSerialization() {
        return Stream.of(
        );
    }

    private static boolean enabledTestJsonSerialization() {
        return sourceTestJsonSerialization().findAny().isPresent();
    }

    @Test
    public void testAllTypesHaveTests() {
        // Arrange
        Set<DialogStateType> expectedTypes = new HashSet<>(Arrays.asList(DialogStateType.values()));
        Set<DialogStateType> actualTypes = sourceTestJsonSerialization()
            .map(arguments -> arguments.get()[0])
            .map(DialogStateType.class::cast)
            .collect(Collectors.toCollection(HashSet::new));

        // Act
        List<DialogStateType> missingTypes = expectedTypes.stream()
            .filter(type -> !actualTypes.contains(type))
            .sorted()
            .toList();

        // Assert
        Assertions.assertTrue(
            missingTypes.isEmpty(),
            () -> "Missing " + DialogStateType.class.getSimpleName() + " tests for " + missingTypes
        );
    }
}
