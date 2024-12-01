package dev.metabrix.urfu.oopbot.storage.model;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Чат в боте.
 *
 * @param id внутренний ID чата
 * @param telegramId ID чата в Telegram
 * @param installedAt дата-время начала работы с ботом в чате
 * @param updatedAt дата-время последнего обновления данных чата
 * @since 1.1.0
 * @author metabrix
 */
public record Chat(
    int id,
    long telegramId,
    int installedById,
    @NotNull Instant installedAt,
    @Nullable Integer updatedById,
    @Nullable Instant updatedAt
) {
}
