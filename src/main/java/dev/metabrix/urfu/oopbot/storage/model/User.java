package dev.metabrix.urfu.oopbot.storage.model;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Пользователь бота.
 *
 * @param id внутренний ID пользователя
 * @param telegramId ID пользователя в Telegram
 * @param telegramUsername имя пользователя в Telegram
 * @param joinedAt дата-время первого начала работы пользователя с ботом
 * @param updatedAt дата-время последнего обновления данных пользователя
 * @since 1.1.0
 * @author metabrix
 */
public record User(
    int id,
    long telegramId,
    @Nullable String telegramUsername,
    @NotNull Instant joinedAt,
    @Nullable Instant updatedAt
) {
    public @NotNull String markdownMention() {
        return this.telegramUsername != null
            ? "@" + this.telegramUsername
            : "[<no username>](tg://user?id=" + this.telegramId + ")";
    }
}
