package com.github.valesnikov.parseallnums.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Arr {
    public static String cpToStr(Collection<Integer> codePoints) {
        return codePoints.stream()
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
