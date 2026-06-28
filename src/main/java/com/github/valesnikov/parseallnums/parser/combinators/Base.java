package com.github.valesnikov.parseallnums.parser.combinators;

import static com.github.valesnikov.parseallnums.utils.Arr.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.valesnikov.parseallnums.parser.ParseError;
import com.github.valesnikov.parseallnums.parser.Parser;
import com.github.valesnikov.parseallnums.parser.State;
import com.github.valesnikov.parseallnums.parser.Unit;
import com.github.valesnikov.parseallnums.utils.Either;
import com.github.valesnikov.parseallnums.utils.Pair;

public class Base {

    public static Parser<Integer> satisfy(Predicate<Integer> pred) {
        return s -> !s.empty() && pred.test(s.chr())
                ? Either.right(State.of(s.chr(), s.next()))
                : Either.left(ParseError.of(s,
                        s.empty()
                                ? "Unexpected EOF"
                                : "Unexpected character '" + (char) s.chr() + "'"));
    }

    public static <T> Parser<T> fail(String message) {
        return s -> Either.left(ParseError.of(s, message));
    }

    public static Parser<Unit> success() {
        return s -> Either.right(State.of(Unit.INSTANCE, s));
    }

    public static <T> Parser<T> pure(T value) {
        return s -> Either.right(State.of(value, s));
    }

    public static <T> Parser<T> or(Parser<T> a, Parser<T> b) {
        return s -> a.parse(s).or(() -> b.parse(s));
    }

    public static <T> Parser<T> lazy(Supplier<Parser<T>> supplier) {
        return input -> supplier.get().parse(input);
    }

    public static <E, T, U> Parser<T> between(Parser<E> left, Parser<T> target, Parser<U> right) {
        return skip(left, skipR(target, right));
    }

    @SafeVarargs
    public static <T> Parser<T> choice(Parser<T>... parsers) {
        return Arrays.stream(parsers)
                .reduce((p1, p2) -> or(p1, p2)).orElse(fail("No parsers provided"));
    }

    public static <T, U> Parser<Pair<T, U>> and(Parser<T> p1, Parser<U> other) {
        return p1.flatMap(r1 -> other.map(r2 -> Pair.of(r1, r2)));
    }

    public static <T, U> Parser<U> skip(Parser<T> a, Parser<U> b) {
        return and(a, b).map(Pair::snd);
    }

    public static <T, U> Parser<T> skipR(Parser<T> a, Parser<U> b) {
        return and(a, b).map(Pair::fst);
    }

    public static Parser<Unit> eof() {
        return s -> s.empty()
                ? Either.right(State.of(Unit.INSTANCE, s))
                : Either.left(ParseError.of(s, "End of input expected"));
    }

    public static <T> Parser<Unit> not(Parser<T> p) {
        return s -> p.parse(s).fold(
                error -> Either.right(State.of(Unit.INSTANCE, s)),
                success -> Either.left(ParseError.of(s, "Unexpected token")));
    }

    public static <T> Parser<Optional<T>> optional(Parser<T> p) {
        return s -> p.parse(s)
                .map(r -> State.of(Optional.of(r.value()), r.state()))
                .or(() -> Either.right(State.of(Optional.empty(), s)));
    }

    public static <T, U> Parser<T> optional(Parser<T> p, T dflt) {
        return or(p, pure(dflt));
    }

    public static <T> Parser<List<T>> many1(Parser<T> p) {
        return p.flatMap(one -> many(p)
                .map(others -> concatAll(List.of(one), others)));
    }

    public static <T> Parser<List<T>> many(Parser<T> p) {
        return s -> p.parse(s)
                .map(match -> many(p).parse(match.state())
                        .map(rest -> {
                            return State.of(
                                    concatAll(
                                            List.of(match.value()),
                                            rest.value()),
                                    rest.state());
                        }))
                .orElse(Either.right(State.of(List.of(), s)));
    }

    public static <T> Parser<List<T>> sequence(List<Parser<T>> parsers) {
        return parsers.isEmpty()
                ? pure(List.of())
                : parsers.get(0)
                        .flatMap(val -> sequence(parsers.subList(1, parsers.size()))
                                .map(vals -> concatAll(List.of(val), vals)));

    }

}
