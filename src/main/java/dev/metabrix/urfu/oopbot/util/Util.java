package dev.metabrix.urfu.oopbot.util;

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
}
