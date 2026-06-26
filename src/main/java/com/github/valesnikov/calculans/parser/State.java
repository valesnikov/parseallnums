package com.github.valesnikov.calculans.parser;

import com.github.valesnikov.calculans.parser.input.StrState;

public record State<T>(T value, StrState state) {
}
