package com.github.valesnikov.calculans.parser.combinators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.valesnikov.calculans.parser.Pair;
import com.github.valesnikov.calculans.parser.Parser;
import com.github.valesnikov.calculans.parser.State;
import com.github.valesnikov.calculans.parser.Unit;
import com.github.valesnikov.calculans.utils.Arr;

public class Base {

    public static Parser<Integer> satisfy(Predicate<Integer> pred) {
        return s -> !s.empty() && pred.test(s.chr())
                ? Optional.of(new State<>(s.chr(), s.next()))
                : Optional.empty();
    }

    public static <T> Parser<T> fail() {
        return s -> Optional.empty();
    }

    public static Parser<Unit> success() {
        return s -> Optional.of(new State<Unit>(Unit.INSTANCE, s));
    }

    public static <T> Parser<T> pure(T value) {
        return s -> Optional.of(new State<T>(value, s));
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
    public static <T> Parser<T> choise(Parser<T>... parsers) {
        return Arrays.stream(parsers)
                .reduce((p1, p2) -> or(p1, p2)).orElse(fail());
    }

    public static <T, U> Parser<Pair<T, U>> and(Parser<T> p1, Parser<U> other) {
        return p1.bind(r1 -> other.map(r2 -> new Pair<>(r1, r2)));
    }

    public static <T, U> Parser<U> skip(Parser<T> a, Parser<U> b) {
        return and(a, b).map(Pair::snd);
    }

    public static <T, U> Parser<T> skipR(Parser<T> a, Parser<U> b) {
        return and(a, b).map(Pair::fst);
    }

    public static Parser<Unit> eof() {
        return s -> s.empty()
                ? Optional.of(new State<>(Unit.INSTANCE, s))
                : Optional.empty();
    }

    public static <T> Parser<Unit> not(Parser<T> p) {
        return s -> p.parse(s).isEmpty()
                ? Optional.of(new State<>(Unit.INSTANCE, s))
                : Optional.empty();
    }

    public static <T> Parser<Optional<T>> optional(Parser<T> p) {
        return s -> p.parse(s)
                .map(r -> new State<>(Optional.of(r.value()), r.state()))
                .or(() -> Optional.of(new State<>(Optional.empty(), s)));
    }

    public static <T, U> Parser<T> optional(Parser<T> p, T dflt) {
        return or(p, pure(dflt));
    }

    public static <T> Parser<List<T>> many1(Parser<T> p) {
        return p.bind(one -> many(p)
                .map(others -> {
                    List<T> all = new ArrayList<>();
                    all.add(one);
                    all.addAll(others);
                    return all;
                }));
    }

    public static <T> Parser<List<T>> many(Parser<T> p) {
        return s -> p.parse(s)
                .map(match -> many(p).parse(match.state())
                        .map(rest -> {
                            return new State<>(
                                    Arr.concatAll(
                                            List.of(match.value()),
                                            rest.value()),
                                    rest.state());
                        }))
                .orElse(Optional.of(new State<>(List.of(), s)));
    }

    public static <T> Parser<List<T>> sequence(List<Parser<T>> parsers) {
        return parsers.isEmpty()
                ? pure(List.of())
                : parsers.get(0)
                        .bind(val -> sequence(parsers.subList(1, parsers.size()))
                                .map(vals -> Arr.concatAll(List.of(val), vals)));

    }

}
