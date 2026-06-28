package com.github.valesnikov.parseallnums.utils;

public record Pair<T, E>(T fst, E snd) {
    public static <T, E> Pair<T, E> of(T fst, E snd) {
        return new Pair<>(fst, snd);
    }
}
