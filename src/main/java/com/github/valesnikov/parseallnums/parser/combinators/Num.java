package com.github.valesnikov.parseallnums.parser.combinators;

import static com.github.valesnikov.parseallnums.parser.combinators.Base.*;
import static com.github.valesnikov.parseallnums.parser.combinators.Char.*;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.valesnikov.parseallnums.parser.Parser;
import com.github.valesnikov.parseallnums.utils.Arr;

public class Num {
    private static Parser<BigInteger> digitSeq(int radix) {
        return many1(digit(radix))
                .map(Arr::concatAll)
                .map(Arr::cpToStr)
                .map(s -> new BigInteger(s, radix));
    }

    private static Parser<BigFraction> intPart(int radix) {
        return digitSeq(radix).map(BigFraction::new);
    }

    private static Parser<BigFraction> signed(Parser<BigFraction> p) {
        return optional(or(chr('-'), chr('+')), (int) '+')
                .flatMap(sign -> p
                        .map(value -> sign == '-'
                                ? value.negate()
                                : value));
    }

    private static Parser<BigInteger> signedBI(Parser<BigInteger> p) {
        return optional(or(chr('-'), chr('+')), (int) '+')
                .flatMap(sign -> p
                        .map(value -> sign == '-'
                                ? value.negate()
                                : value));
    }

    private static Parser<BigFraction> fractPart(int radix) {
        return many1(digit(radix))
                .map(Arr::concatAll)
                .map(Arr::cpToStr)
                .map(s -> s.replaceAll("0+$", ""))
                .map(s -> {
                    if (s.isEmpty()) {
                        return BigFraction.ZERO;
                    } else {
                        final var fracLen = s.length();
                        final var nom = new BigInteger(s, radix);
                        final var denom = BigInteger.valueOf(radix).pow(fracLen);
                        return new BigFraction(nom, denom);
                    }
                });
    }

    private static Parser<BigFraction> dotFractPart(int radix) {
        return skip(chr('.'), fractPart(radix));
    }

    private static Parser<BigFraction> dotNum(int radix) {
        return choise(
                intPart(radix).flatMap(i -> dotFractPart(radix).map(f -> i.add(f))),
                skipR(intPart(radix), chr('.')),
                dotFractPart(radix),
                intPart(radix));
    }

    private static Parser<BigFraction> expDec() {
        return skip(
                chr('e'),
                signedBI(digitSeq(10)))
                .map(n -> n.compareTo(BigInteger.ZERO) < 0
                        ? new BigFraction(BigInteger.ONE, BigInteger.TEN.pow(-n.intValue()))
                        : new BigFraction(BigInteger.TEN.pow(n.intValue())));
    }

    private static Parser<BigFraction> expBin() {
        return skip(
                chr('p'),
                signedBI(digitSeq(10)))
                .map(n -> n.compareTo(BigInteger.ZERO) < 0
                        ? new BigFraction(BigInteger.ONE, BigInteger.TWO.pow(-n.intValue()))
                        : new BigFraction(BigInteger.TWO.pow(n.intValue())));
    }

    private static Parser<BigFraction> exp() {
        return or(expDec(), expBin());
    }

    private static Parser<BigFraction> dotNumExp(int radix) {
        return dotNum(radix)
                .flatMap(num -> optional(exp(), BigFraction.ONE)
                        .map(ex -> num.multiply(ex)));
    }

    public static Parser<BigFraction> number() {
        return signed(choise(
                skip(and(chr('0'), chr('x')), dotNumExp(16)),
                skip(and(chr('0'), chr('d')), dotNumExp(10)),
                skip(and(chr('0'), chr('o')), dotNumExp(8)),
                skip(and(chr('0'), chr('b')), dotNumExp(2)),
                dotNumExp(10)));
    }
}