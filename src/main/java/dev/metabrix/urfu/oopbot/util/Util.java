package dev.metabrix.urfu.oopbot.util;

import org.jetbrains.annotations.NotNull;

/**
 * Полезные методы.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class Util {
    /**
     * Возвращает метку времени от произвольной точки во времени. Полезно для измерения
     * длительности.
     *
     * @return метка времени в миллисекундах
     * @since 1.0.0
     * @author metabrix
     */
    public static long monotonicMillis() {
        return System.nanoTime() / 1_000_000L;
    }

    /**
     * Экранирует все специальные для Telegram-сообщений символы в строке.
     *
     * @param string строка
     * @return экранированная строка
     * @since 1.1.0
     * @author metabrix
     */
    public static @NotNull String sanitizeString(@NotNull String string) {
        return string.replaceAll("([_*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");
    }
}
