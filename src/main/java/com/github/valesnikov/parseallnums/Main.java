package com.github.valesnikov.parseallnums;

import static com.github.valesnikov.parseallnums.parser.combinators.Base.*;
import static com.github.valesnikov.parseallnums.parser.combinators.Char.*;
import static com.github.valesnikov.parseallnums.parser.combinators.Num.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.valesnikov.parseallnums.parser.State;
import com.github.valesnikov.parseallnums.parser.StrState;

public class Main {
    public static void main(String[] args) {
        try (final var reader = new BufferedReader(new InputStreamReader(System.in))) {
            Stream.generate(() -> {
                System.out.print("> ");
                try {
                    return reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).takeWhile(line -> line != null)
                    .map(line -> between(many(whiteSpace()), number(), many(whiteSpace()))
                            .parse(StrState.fromString(line))
                            .map(State::value)
                            .map(BigFraction::toString)
                            .fold(error -> error.toString(), value -> value))
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}