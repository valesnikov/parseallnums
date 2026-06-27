package com.github.valesnikov.calculans.parser;

public class ParseError {
    private final int index;
    private final int line;
    private final int column;
    private final String message;

    public static ParseError of(StrState state, String message) {
        return new ParseError(state, message);
    }

    private ParseError(StrState state, String message) {
        this.index = state.index();
        this.line = state.line();
        this.column = state.column();
        this.message = message;
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

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("Error at %d:%d (index %d): %s", line, column, index, message);
    }
}
