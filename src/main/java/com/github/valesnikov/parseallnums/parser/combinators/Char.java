package com.github.valesnikov.parseallnums.parser.combinators;

import static com.github.valesnikov.parseallnums.parser.combinators.Base.*;

import java.util.List;

import com.github.valesnikov.parseallnums.parser.Parser;
import com.github.valesnikov.parseallnums.utils.Arr;

public class Char {
    public static Parser<Integer> chr(int character) {
        return satisfy(c -> c == character);
    }

    public static Parser<String> str(String subs) {
        return sequence(subs
                .codePoints()
                .mapToObj(Char::chr)
                .toList())
                .map(Arr::concatAll)
                .map(Arr::cpToStr);
    }

    public static Parser<Integer> whiteSpace() {
        return satisfy(c -> Character.isWhitespace(c));
    }

    public static Parser<Integer> digit(int radix) {
        return satisfy(c -> Character.digit(c, radix) != -1);
    }

    public static Parser<Integer> letter() {
        return satisfy(c -> Character.isLetter(c));
    }

    public static Parser<Integer> letterOrDigit() {
        return satisfy(c -> Character.isLetterOrDigit(c));
    }

    public static Parser<Integer> anyCaseChr(int character) {
        return satisfy(c -> Character.toLowerCase(c) == Character.toLowerCase(character));
    }

    public static Parser<String> identifier() {
        return letter()
                .flatMap(c -> many(letterOrDigit())
                        .map(cs -> Arr.concatAll(List.of(c), cs))
                        .map(Arr::cpToStr));
    }
}
