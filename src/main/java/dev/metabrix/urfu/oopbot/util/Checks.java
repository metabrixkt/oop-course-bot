package dev.metabrix.urfu.oopbot.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Утилита для различных проверок.
 *
 * @since 1.0.0
 * @author metabrix
 */
public final class Checks {
    private Checks() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Проверяет, что указанное значение не равно <code>null</code> и возвращает его, в противном случае
     * выбрасывает {@link NullPointerException} с указанным сообщением.
     *
     * @param value значение для проверки
     * @param message сообщение для исключения, если значение окажется <code>null</code>
     * @return аргумент <code>value</code>
     * @throws NullPointerException если аргумент <code>value</code> равен <code>null</code>
     * @param <T> тип проверяемого значения <code>value</code>
     * @since 1.0.0
     * @author metabrix
     */
    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> @NotNull T checkNotNull(@Nullable T value, @NotNull String message) {
        if (value == null) throw new NullPointerException(message);
        return value;
    }

    /**
     * Проверяет, что указанное выражение равно <code>true</code>, в противном случае выбрасывает
     * {@link IllegalArgumentException} с указанным сообщением.
     *
     * @param expression выражение для проверки
     * @param message сообщение для исключения, если значение окажется <code>false</code>
     * @throws IllegalArgumentException если аргумент <code>expression</code> равен <code>false</code>
     * @since 1.0.0
     * @author metabrix
     */
    public static void checkArgument(boolean expression, @NotNull String message) {
        if (!expression) throw new IllegalArgumentException(message);
    }


    /**
     * Проверяет, что указанное выражение равно <code>true</code>, в противном случае выбрасывает
     * {@link IllegalStateException} с указанным сообщением.
     *
     * @param expression выражение для проверки
     * @param message сообщение для исключения, если значение окажется <code>false</code>
     * @throws IllegalStateException если аргумент <code>expression</code> равен <code>false</code>
     * @since 1.0.0
     * @author metabrix
     */
    public static void checkState(boolean expression, @NotNull String message) {
        if (!expression) throw new IllegalStateException(message);
    }
}
