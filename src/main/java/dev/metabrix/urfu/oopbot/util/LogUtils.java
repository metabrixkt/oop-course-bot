package dev.metabrix.urfu.oopbot.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилита логирования.
 *
 * @since 1.0.0
 * @author metabrix
 */
public class LogUtils {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * Возвращает логгер для вызывающего класса.
     *
     * @return логгер для вызывающего класса
     * @since 1.0.0
     * @author metabrix
     * @apiNote Этот метод использует информацию о вызывающем классе, поэтому его нельзя
     *          оборачивать в другой метод.
     */
    public static @NotNull Logger getLogger() {
        return LoggerFactory.getLogger(STACK_WALKER.getCallerClass());
    }
}
