package com.github.valesnikov.calculans.parser;

public record State<T>(T value, StrState state) {
    public static <T> State<T> of(T value, StrState state) {
        return new State<>(value, state);
    }
}
