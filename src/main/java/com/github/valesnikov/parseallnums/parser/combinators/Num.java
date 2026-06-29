package com.github.valesnikov.parseallnums.parser.combinators;

import static com.github.valesnikov.parseallnums.parser.combinators.Base.*;
import static com.github.valesnikov.parseallnums.parser.combinators.Char.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.valesnikov.parseallnums.parser.Parser;
import com.github.valesnikov.parseallnums.utils.Arr;

public class Num {

    private static Parser<Integer> digitWithSeparator(int radix) {
        return or(skip(or(chr('_'), chr('\'')), digit(radix)), digit(radix));
    }

    private static Parser<String> digitSeqStr(int radix) {
        return digit(radix)
                .flatMap(first -> many(digitWithSeparator(radix))
                        .map(rest -> Arr.concatAll(List.of(first), rest))
                        .map(Arr::cpToStr));
    }

    private static Parser<BigInteger> digitSeq(int radix) {
        return digitSeqStr(radix)
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
        return digitSeqStr(radix)
                .map(s -> s.isEmpty()
                        ? BigFraction.ZERO
                        : new BigFraction(
                                new BigInteger(s, radix),
                                BigInteger.valueOf(radix).pow(s.length())));
    }

    private static Parser<BigFraction> dotFractPart(int radix) {
        return skip(chr('.'), fractPart(radix));
    }

    private static Parser<BigFraction> dotNum(int radix) {
        return choice(
                intPart(radix).flatMap(i -> dotFractPart(radix).map(f -> i.add(f))),
                skipR(intPart(radix), chr('.')),
                dotFractPart(radix),
                intPart(radix));
    }

    private static Parser<BigFraction> expDec() {
        return skip(
                anyCaseChr('e'),
                signedBI(digitSeq(10)))
                .map(n -> n.compareTo(BigInteger.ZERO) < 0
                        ? new BigFraction(BigInteger.ONE, BigInteger.TEN.pow(-n.intValue()))
                        : new BigFraction(BigInteger.TEN.pow(n.intValue())));
    }

    private static Parser<BigFraction> expBin() {
        return skip(
                anyCaseChr('p'),
                signedBI(digitSeq(10)))
                .map(n -> n.compareTo(BigInteger.ZERO) < 0
                        ? new BigFraction(BigInteger.ONE, BigInteger.TWO.pow(-n.intValue()))
                        : new BigFraction(BigInteger.TWO.pow(n.intValue())));
    }

    private static Parser<BigFraction> dotNumExpDec() {
        return dotNum(10).flatMap(num -> optional(or(expDec(), expBin()), BigFraction.ONE).map(ex -> num.multiply(ex)));
    }

    private static Parser<BigFraction> dotNumExpBin(int radix) {
        return dotNum(radix).flatMap(num -> optional(expBin(), BigFraction.ONE).map(ex -> num.multiply(ex)));
    }

    private static Parser<BigFraction> dotNumExp(int radix) {
        return radix == 10 ? dotNumExpDec() : dotNumExpBin(radix);
    }

    public static Parser<BigFraction> number() {
        return signed(choice(
                skip(and(chr('0'), anyCaseChr('x')), dotNumExp(16)),
                skip(and(chr('0'), anyCaseChr('d')), dotNumExp(10)),
                skip(and(chr('0'), anyCaseChr('o')), dotNumExp(8)),
                skip(and(chr('0'), anyCaseChr('b')), dotNumExp(2)),
                dotNumExp(10)));
    }
}