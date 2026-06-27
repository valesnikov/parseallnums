package com.github.valesnikov.calculans.parser;

import java.util.function.Function;
import java.util.function.Supplier;

import com.github.valesnikov.calculans.utils.Either;

@FunctionalInterface
public interface Parser<T> {
    Either<ParseError, State<T>> parse(StrState input);

    default <U> Parser<U> map(Function<T, U> f) {
        return s -> this.parse(s).map(r -> new State<>(f.apply(r.value()), r.state()));
    }

    default <U> Parser<U> map_(Supplier<U> f) {
        return s -> this.parse(s).map(r -> new State<>(f.get(), r.state()));
    }

    default <U> Parser<U> bind(Function<T, Parser<U>> f) {
        return s -> this.parse(s).flatMap(r -> f.apply(r.value()).parse(r.state()));
    }

    default <U> Parser<U> bind_(Supplier<Parser<U>> f) {
        return s -> this.parse(s).flatMap(r -> f.get().parse(r.state()));
    }
}