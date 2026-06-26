package com.github.valesnikov.calculans;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.valesnikov.calculans.parser.State;
import static com.github.valesnikov.calculans.parser.combinators.Base.*;
import static com.github.valesnikov.calculans.parser.combinators.Num.*;
import com.github.valesnikov.calculans.parser.input.StrState;

public class Main {
    public static void main(String[] args) {

        String[] strs = {
                "0",
                "123",
                "-456",
                "+789",
                "1.0",
                "0.5",
                "10.",
                ".25",
                "-3.1415",
                "1e10",
                "1e+10",
                "1e-10",
                "3.14e2",
                "-2.5e-3",
                ".5e4",
                "10.e-2",
                "0b1010",
                "0b1",
                "-0b1101",
                "0b1.01",
                "0b.101",
                "0b10.",
                "0b1p+3",
                "0b1.1p2",
                "0b.101p-4",
                "-0b10.01p+1",
                "0o17",
                "0o755",
                "-0o12",
                "0o7.",
                "0o3.4",
                "0o.52",
                "0o7p2",
                "0o1.2p-1",
                "0xFF",
                "0x1A3",
                "-0x4B",
                "0x1.F",
                "0x.A",
                "0x10.",
                "0x1p10",
                "0x1.8p+4",
                "0x.FFp-8",
                "-0x2.3p-2",
                "+0",
                "-0",
                "0e0",
                "0x0p0",
                // edge invalid cases (should be rejected by parser)
                ".e10",
                "0x.p2",
                "0b2",
                "0o9",
                "",
                ".",
                "-.",
                "e10",
                "0xp",
                "0bp2",
                "0op",
                "0x1p",
                "0x1p+",
                "0b1p",
                "0o1p",
                "..1",
                "1..2",
                "--1",
                "++1",
        };

        for (var s : strs) {
            System.out.println(s + "  ->  " +
                    skipR(number(), eof()).parse(StrState.fromString(s))
                            .map(State::value)
                            .map(BigFraction::toString)
                            .orElse("<fail>"));
        }
    }
}