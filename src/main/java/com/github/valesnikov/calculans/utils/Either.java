package com.github.valesnikov.calculans.utils;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<L, R> permits Either.Left, Either.Right {

    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    <T> T fold(Function<? super L, ? extends T> leftFn,
            Function<? super R, ? extends T> rightFn);

    default boolean isRight() {
        return fold(l -> false, r -> true);
    }

    default boolean isLeft() {
        return !isRight();
    }

    default <U> Either<L, U> map(Function<? super R, ? extends U> mapper) {
        return fold(Either::left, r -> Either.right(mapper.apply(r)));
    }

    default <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, U>> mapper) {
        return fold(Either::left, mapper);
    }

    default Either<L, R> or(Supplier<? extends Either<L, R>> supplier) {
        return fold(l -> supplier.get(), r -> this);
    }

    default R orElse(R defaultValue) {
        return fold(l -> defaultValue, r -> r);
    }

    record Left<L, R>(L value) implements Either<L, R> {
        @Override
        public <T> T fold(Function<? super L, ? extends T> leftFn,
                Function<? super R, ? extends T> rightFn) {
            return leftFn.apply(value);
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {
        @Override
        public <T> T fold(Function<? super L, ? extends T> leftFn,
                Function<? super R, ? extends T> rightFn) {
            return rightFn.apply(value);
        }
    }
}