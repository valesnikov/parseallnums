package com.github.valesnikov.calculans.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Arr {
    @SafeVarargs
    public static String concatAllStr(Collection<Integer>... lists) {
        return concatAll(lists).stream()
                .collect(StringBuilder::new,
                        (sb, cp) -> sb.appendCodePoint(cp),
                        StringBuilder::append)
                .toString();
    }

    @SafeVarargs
    public static <T> List<T> concatAll(Collection<T>... lists) {
        return Arrays.stream(lists)
                .flatMap(Collection::stream)
                .toList();
    }

}
