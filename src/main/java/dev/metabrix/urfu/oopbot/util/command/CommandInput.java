package dev.metabrix.urfu.oopbot.util.command;

import java.util.StringTokenizer;
import org.jetbrains.annotations.NotNull;

/**
 * Введённая командная строка.
 */
public interface CommandInput {
    /**
     * Возвращает {@link CommandInput} для указанной строки.
     *
     * @param input командная строка
     * @return обёртка над строкой
     * @since 1.0.0
     * @author metabrix
     */
    static @NotNull CommandInput of(@NotNull String input) {
        return new CommandInputImpl(input);
    }

    /**
     * Возвращает новый пустой {@link CommandInput}.
     *
     * @return пустой {@link CommandInput}
     */
    static @NotNull CommandInput empty() {
        return new CommandInputImpl("");
    }

    /**
     * Возвращает всю исходную строку.
     *
     * @return исходная строка
     */
    @NotNull String getRawInput();

    /**
     * Возвращает позицию курсора.
     *
     * @return позиция курсора
     * @since 1.0.0
     * @author metabrix
     */
    int getCursor();

    /**
     * Устанавливает позицию курсора.
     *
     * @param position the new position
     * @return тот же {@link CommandInput}
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull CommandInput setCursor(int position);

    /**
     * Перемещает курсор на указанное количество символов.
     *
     * @param chars количество символов
     * @throws CursorOutOfBoundsException если количество символов превышает {@link #getRemainingLength() оставшуюся длину ввода}
     * @since 1.0.0
     * @author metabrix
     */
    void moveCursor(int chars);

    /**
     * Возвращает длину исходной строки.
     *
     * @return длина исходной строки
     * @since 1.0.0
     * @author metabrix
     */
    default int getRawLength() {
        return this.getRawInput().length();
    }

    /**
     * Возвращает длину непрочитанной части ввода.
     *
     * @return длина непрочитанной части ввода
     * @since 1.0.0
     * @author metabrix
     */
    default int getRemainingLength() {
        return this.getRawLength() - this.getCursor();
    }

    /**
     * Возвращает количество оставшихся токенов.
     *
     * @return количество непрочитанных токенов
     * @since 1.0.0
     * @author metabrix
     */
    default int countRemainingTokens() {
        return new StringTokenizer(this.getRemainingInput(), " ").countTokens();
    }

    /**
     * Возвращает непрочитанную часть ввода.
     *
     * @return непрочитанная часть ввода
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String getRemainingInput() {
        return this.getRawInput().substring(this.getCursor());
    }

    /**
     * Возвращает прочитанную часть ввода.
     *
     * @return прочитанная часть ввода
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String getReadInput() {
        return this.getRawInput().substring(0, this.getCursor());
    }

    /**
     * Добавляет строку к {@link #getRawInput()} и возвращает новую командную строку.
     *
     * @param string строка
     * @return новая командная строка
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull CommandInput appendString(@NotNull String string);

    /**
     * Добавляет токен к {@link #getRawInput()} через пробел и возвращает новую командную строку.
     *
     * @param token строка токена
     * @return новая командная строка
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull CommandInput appendToken(@NotNull String token) {
        return this.appendString(this.hasRemainingInput() && !this.getRemainingInput().endsWith(" ") ? " " + token : token);
    }

    /**
     * Проверяет, есть ли ещё непрочитанный ввод.
     *
     * @return есть ли непрочитанный ввод
     * @since 1.0.0
     * @author metabrix
     */
    default boolean hasRemainingInput() {
        return this.getCursor() < this.getRawLength();
    }

    /**
     * Проверяет, пуст ли оставшийся ввод.
     *
     * @return пуст ли оставшийся ввод
     * @since 1.0.0
     * @author metabrix
     */
    default boolean isEmpty() {
        return !this.hasRemainingInput();
    }

    /**
     * Читает указанное количество символов, не перемещая курсор.
     *
     * @param chars количество символов
     * @return прочитанная строка
     * @throws CursorOutOfBoundsException если количество символов превышает {@link #getRemainingLength() оставшуюся длину ввода}
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String peekString(int chars) {
        String remainingInput = this.getRemainingInput();
        if (chars > remainingInput.length()) throw new CursorOutOfBoundsException(this.getCursor() + chars, this.getRawLength());

        return remainingInput.substring(0, chars);
    }

    /**
     * Читает указанное количество символов, перемещая курсор.
     *
     * @param chars количество символов
     * @return прочитанная строка
     * @throws CursorOutOfBoundsException если количество символов превышает {@link #getRemainingLength() оставшуюся длину ввода}
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String readString(int chars) {
        String readString = this.peekString(chars);
        this.moveCursor(chars);
        return readString;
    }

    /**
     * Читает символ на курсоре, не перемещая курсор.
     *
     * @return символ на курсоре
     * @throws CursorOutOfBoundsException если курсор вышел за пределы ввода
     * @since 1.0.0
     * @author metabrix
     */
    default char peekChar() {
        if (this.getCursor() >= this.getRawInput().length()) {
            throw new CursorOutOfBoundsException(
                    this.getCursor(),
                    this.getRawLength()
            );
        }
        return this.getRawInput().charAt(this.getCursor());
    }

    /**
     * Читает символ на курсоре, перемещая курсор.
     *
     * @return символ на курсоре
     * @throws CursorOutOfBoundsException если курсор вышел за пределы ввода
     * @since 1.0.0
     * @author metabrix
     */
    default char readChar() {
        char readChar = this.peekChar();
        this.moveCursor(1 /* chars */);
        return readChar;
    }

    /**
     * Читает токен, не перемещая курсор. Пропускает пробелы перед ним и останавливается на пробеле после него.
     *
     * @return токен
     * @throws CursorOutOfBoundsException если курсор вышел за пределы ввода
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String peekToken() {
        if (!this.hasRemainingInput()) {
            return "";
        }

        String remainingInput = this.getRemainingInput();
        if (remainingInput.indexOf(' ') == -1) return remainingInput;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < remainingInput.length(); i++) {
            char currentChar = remainingInput.charAt(i);
            if (Character.isWhitespace(currentChar)) {
                if (builder.isEmpty()) continue; // пропустим пробелы перед токеном
                else break; // остановимся на пробеле после токена
            }
            builder.append(currentChar);
        }

        return builder.toString();
    }

    /**
     * Читает токен, перемещая курсор. Пропускает пробелы перед ним и останавливается на пробеле после него.
     *
     * @return прочитанный токен
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String readToken() {
        return this.skipWhitespace().readTokenUntil(' ');
    }

    /**
     * Читает токен до указанного символа.
     *
     * @param endChar символ окончания токена
     * @return прочитанный токен
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull String readTokenUntil(char endChar) {
        if (!this.hasRemainingInput()) return "";

        String remainingInput = this.getRemainingInput();
        int endCharIndex = remainingInput.indexOf(endChar);
        if (endCharIndex == -1) {
            this.moveCursor(this.getRemainingLength());
            return remainingInput;
        } else {
            // endCharIndex также является количеством символов,
            // которые нужно прочитать из оставшегося ввода
            return this.readString(endCharIndex);
        }
    }

    /**
     * Пропускает до указанного количества пробелов.
     *
     * @param maxSpaces максимальное количество пробелов
     * @return {@code this}
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull CommandInput skipWhitespace(int maxSpaces) {
        for (int i = 0; i < maxSpaces && this.hasRemainingInput() && Character.isWhitespace(this.peekChar()); i++) {
            this.readChar();
        }
        return this;
    }

    /**
     * Пропускает все пробелы до непробельного символа.
     *
     * @return {@code this}
     * @since 1.0.0
     * @author metabrix
     */
    default @NotNull CommandInput skipWhitespace() {
        return this.skipWhitespace(Integer.MAX_VALUE);
    }

    /**
     * Читает токен в {@link Byte}, не перемещая курсор.
     *
     * @return {@link Byte}
     * @since 1.0.0
     * @author metabrix
     */
    default byte peekByte() {
        return Byte.parseByte(this.peekToken());
    }

    /**
     * Читает токен в {@link Short}, не перемещая курсор.
     *
     * @return {@link Short}
     * @since 1.0.0
     * @author metabrix
     */
    default short peekShort() {
        return Short.parseShort(this.peekToken());
    }

    /**
     * Читает токен в {@link Integer}, не перемещая курсор.
     *
     * @return {@link Integer}
     * @since 1.0.0
     * @author metabrix
     */
    default int peekInt() {
        return Integer.parseInt(this.peekToken());
    }

    /**
     * Читает токен в {@link Long}, не перемещая курсор.
     *
     * @return {@link Long}
     * @since 1.0.0
     * @author metabrix
     */
    default long peekLong() {
        return Long.parseLong(this.peekToken());
    }

    /**
     * Читает токен в {@link Double}, не перемещая курсор.
     *
     * @return {@link Double}
     * @since 1.0.0
     * @author metabrix
     */
    default double peekDouble() {
        return Double.parseDouble(this.peekToken());
    }

    /**
     * Читает токен в {@link Float}, не перемещая курсор.
     *
     * @return {@link Float}
     * @since 1.0.0
     * @author metabrix
     */
    default float peekFloat() {
        return Float.parseFloat(this.peekToken());
    }

    /**
     * Читает токен в {@link Byte}, перемещая курсор.
     *
     * @return {@link Byte}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Byte}
     * @since 1.0.0
     * @author metabrix
     */
    default byte readByte() {
        return Byte.parseByte(this.readToken());
    }

    /**
     * Читает токен в {@link Short}, перемещая курсор.
     *
     * @return {@link Short}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Short}
     * @since 1.0.0
     * @author metabrix
     */
    default short readShort() {
        return Short.parseShort(this.readToken());
    }

    /**
     * Читает токен в {@link Integer}, перемещая курсор.
     *
     * @return {@link Integer}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Integer}
     * @since 1.0.0
     * @author metabrix
     */
    default int readInt() {
        return Integer.parseInt(this.readToken());
    }

    /**
     * Читает токен в {@link Long}, перемещая курсор.
     *
     * @return {@link Long}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Long}
     * @since 1.0.0
     * @author metabrix
     */
    default long readLong() {
        return Long.parseLong(this.readToken());
    }

    /**
     * Читает токен в {@link Double}, перемещая курсор.
     *
     * @return {@link Double}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Double}
     * @since 1.0.0
     * @author metabrix
     */
    default double readDouble() {
        return Double.parseDouble(this.readToken());
    }

    /**
     * Читает токен в {@link Float}, перемещая курсор.
     *
     * @return {@link Float}
     * @throws NumberFormatException если введённая строка не может быть преобразована в {@link Float}
     * @since 1.0.0
     * @author metabrix
     */
    default float readFloat() {
        return Float.parseFloat(this.readToken());
    }

    /**
     * Копирует этот {@link CommandInput}.
     *
     * @return копия этого объекта
     * @since 1.0.0
     * @author metabrix
     */
    @NotNull CommandInput copy();
}
