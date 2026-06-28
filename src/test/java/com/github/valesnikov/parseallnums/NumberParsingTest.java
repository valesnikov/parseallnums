package com.github.valesnikov.parseallnums;

import org.junit.jupiter.api.Test;

import com.github.valesnikov.parseallnums.parser.StrState;

import static com.github.valesnikov.parseallnums.parser.combinators.Base.*;
import static com.github.valesnikov.parseallnums.parser.combinators.Num.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.math3.fraction.BigFraction;

public class NumberParsingTest {

    @Test
    public void testBasicNumbers() {
        assertParsedAs("0", new BigFraction(0));
        assertParsedAs("123", new BigFraction(123));
        assertParsedAs("-456", new BigFraction(-456));
        assertParsedAs("+789", new BigFraction(789));
        assertParsedAs("5e2", new BigFraction(500));
        assertParsedAs("2e+3", new BigFraction(2000));
    }

    @Test
    public void testDecimalNumbers() {
        assertParsedAs("1.0", new BigFraction(1));
        assertParsedAs("0.5", new BigFraction(1, 2));
        assertParsedAs("10.", new BigFraction(10));
        assertParsedAs(".25", new BigFraction(1, 4));
        assertParsedAs("-3.1415", new BigFraction(-31415, 10000));
        assertParsedAs("1.5p2", new BigFraction(6));
        assertParsedAs("2p3", new BigFraction(16));
        assertParsedAs("1.5e2", new BigFraction(150));
        assertParsedAs("0.5e3", new BigFraction(500));
    }

    @Test
    public void testScientificNotation() {
        assertParsedAs("1e10", new BigFraction(10000000000L));
        assertParsedAs("1e+10", new BigFraction(10000000000L));
        assertParsedAs("1e-10", new BigFraction(1, 10000000000L));
        assertParsedAs("3.14e2", new BigFraction(314));
        assertParsedAs("-2.5e-3", new BigFraction(-25, 10000));
        assertParsedAs(".5e4", new BigFraction(5000));
        assertParsedAs("10.e-2", new BigFraction(1, 10));
        assertParsedAs("1p10", new BigFraction(1024));
        assertParsedAs("0.5p3", new BigFraction(4));
        assertParsedAs("10p-2", new BigFraction(5, 2));
    }

    @Test
    public void testBinaryNumbers() {
        assertParsedAs("0b1010", new BigFraction(10));
        assertParsedAs("0b1", new BigFraction(1));
        assertParsedAs("-0b1101", new BigFraction(-13));
        assertParsedAs("0b1.01", new BigFraction(5, 4));
        assertParsedAs("0b.101", new BigFraction(5, 8));
        assertParsedAs("0b10.", new BigFraction(2));
        assertParsedAs("0b1p+3", new BigFraction(8));
        assertParsedAs("0b1.1p2", new BigFraction(6));
        assertParsedAs("0b.101p-4", new BigFraction(5, 128));
        assertParsedAs("-0b10.01p+1", new BigFraction(-9, 2));
    }

    @Test
    public void testOctalNumbers() {
        assertParsedAs("0o17", new BigFraction(15));
        assertParsedAs("0o755", new BigFraction(493));
        assertParsedAs("-0o12", new BigFraction(-10));
        assertParsedAs("0o7.", new BigFraction(7));
        assertParsedAs("0o3.4", new BigFraction(28, 8));
        assertParsedAs("0o.52", new BigFraction(42, 64));
        assertParsedAs("0o7p2", new BigFraction(28));
        assertParsedAs("0o1.2p-1", new BigFraction(5, 8));
    }

    @Test
    public void testHexadecimalNumbers() {
        assertParsedAs("0xFF", new BigFraction(255));
        assertParsedAs("0x1A3", new BigFraction(419));
        assertParsedAs("-0x4B", new BigFraction(-75));
        assertParsedAs("0x1.F", new BigFraction(31, 16));
        assertParsedAs("0x.A", new BigFraction(10, 16));
        assertParsedAs("0x10.", new BigFraction(16));
        assertParsedAs("0x1p10", new BigFraction(1024));
        assertParsedAs("0x1.8p+4", new BigFraction(24));
        assertParsedAs("0x.FFp-8", new BigFraction(255, 65536));
        assertParsedAs("-0x2.3p-2", new BigFraction(-35, 64));
        assertParsedAs("0xFFe1", new BigFraction(65505)); // FFe1 in hex = 15*16^3 + 15*16^2 + 14*16^1 + 1*16^0 = 61440
                                                          // + 3840 + 224 + 1 = 65505
    }

    @Test
    public void testSpecialCases() {
        assertParsedAs("+0", new BigFraction(0));
        assertParsedAs("-0", new BigFraction(0));
        assertParsedAs("0e0", new BigFraction(0));
        assertParsedAs("0x0p0", new BigFraction(0));
        assertParsedAs("1e0", new BigFraction(1));
        assertParsedAs("1p0", new BigFraction(1));
    }

    @Test
    public void testInvalidInputsShouldFail() {
        assertParseFails(".e10");
        assertParseFails("0x.p2");
        assertParseFails("0b2");
        assertParseFails("0o9");
        assertParseFails("");
        assertParseFails(".");
        assertParseFails("-.");
        assertParseFails("e10");
        assertParseFails("0xp");
        assertParseFails("0bp2");
        assertParseFails("0op");
        assertParseFails("0x1p");
        assertParseFails("0x1p+");
        assertParseFails("0b1p");
        assertParseFails("0o1p");
        assertParseFails("..1");
        assertParseFails("1..2");
        assertParseFails("--1");
        assertParseFails("++1");
    }

    private void assertParsedAs(String input, BigFraction expectedValue) {
        var result = skipR(number(), eof())
                .parse(StrState.fromString(input));

        assertTrue(result.isRight(), "Parsing should succeed for input: " + input);

        var state = result.fold(error -> null, stateValue -> stateValue);
        assertNotNull(state, "State should not be null when parsing succeeds");

        var actualValue = state.value();

        assertEquals(expectedValue, actualValue, "Wrong parsed value for input: " + input);
    }

    private void assertParseFails(String input) {
        var result = skipR(number(), eof())
                .parse(StrState.fromString(input));

        assertTrue(result.isLeft(), "Parsing should fail for input: " + input);
    }
}