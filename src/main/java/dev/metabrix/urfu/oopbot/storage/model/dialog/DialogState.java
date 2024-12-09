package dev.metabrix.urfu.oopbot.storage.model.dialog;

import dev.metabrix.urfu.oopbot.interaction.MessageInteraction;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Состояние диалога.
 *
 * @since 1.1.0
 * @author metabrix
 */
public interface DialogState {
    /**
     * Обрабатывает сообщение для этого состояния диалога.
     *
     * @param interaction взаимодействие с ботом
     * @since 1.1.0
     * @author metabrix
     */
    void handleMessage(@NotNull MessageInteraction interaction);

    /**
     * Возвращает тип состояния диалога.
     *
     * @return тип состояния диалога
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull DialogStateType type();

    /**
     * Сериализует данные состояния диалога в JSON-объект.
     *
     * @return JSON-объект данных этого состояния диалога
     * @since 1.1.0
     * @author metabrix
     */
    @NotNull JSONObject toJson();
}
