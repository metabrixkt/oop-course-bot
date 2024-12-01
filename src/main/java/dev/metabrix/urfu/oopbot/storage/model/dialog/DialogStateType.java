package dev.metabrix.urfu.oopbot.storage.model.dialog;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Тип состояния диалога.
 *
 * @since 1.1.0
 * @author metabrix
 */
public enum DialogStateType {
    ;

    private static final @NotNull Map<@NotNull String, @NotNull DialogStateType> BY_ID =
        Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(
            DialogStateType::getId,
            Function.identity()
        ));

    private final @NotNull String id;
    private final @NotNull Function<@NotNull JSONObject, @NotNull DialogState> jsonDeserializer;

    DialogStateType(@NotNull Function<@NotNull JSONObject, @NotNull DialogState> jsonDeserializer) {
        this.id = this.name().toLowerCase(Locale.ROOT);
        this.jsonDeserializer = jsonDeserializer;
    }

    /**
     * Возвращает ID этого типа состояния диалога для использования в хранилище.
     *
     * @return строковой ID этого типа в snake_case
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull String getId() {
        return this.id;
    }

    /**
     * Десериализует состояние диалога этого типа из JSON-объекта.
     *
     * @param json JSON-объект данных состояния диалога
     * @return состояние диалога
     * @since 1.1.0
     * @author metabrix
     */
    public @NotNull DialogState deserializeFromJson(@NotNull JSONObject json) {
        return this.jsonDeserializer.apply(json);
    }

    /**
     * Возвращает тип состояния диалога по его ID.
     *
     * @param id строковой ID типа состояния диалога
     * @return тип состояния диалога или {@code null}, если такого типа нет
     * @since 1.1.0
     * @author metabrix
     */
    public static @Nullable DialogStateType byId(@NotNull String id) {
        return BY_ID.get(id);
    }
}
