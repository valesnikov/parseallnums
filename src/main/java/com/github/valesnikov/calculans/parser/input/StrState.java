package com.github.valesnikov.calculans.parser.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StrState {
    private final int[] slice;
    private final int index;
    private final int line;
    private final int column;

    private StrState(int[] slice, int index, int line, int column) {
        this.slice = slice;
        this.index = index;
        this.line = line;
        this.column = column;
    }

    public static StrState fromString(String s) {
        return new StrState(s.codePoints().toArray(), 0, 0, 0);
    }

    public static StrState fromFile(Path path) throws IOException {
        return fromString(Files.readString(path));
    }

    public int chr() {
        return slice[index];
    }

    public boolean empty() {
        return index >= slice.length;
    }

    public StrState next() {
        var isNewLine = chr() == '\n';
        return isNewLine
                ? new StrState(slice, index + 1, line + 1, 0)
                : new StrState(slice, index + 1, line, column + 1);
    }

    public int index() {
        return index;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }
}
