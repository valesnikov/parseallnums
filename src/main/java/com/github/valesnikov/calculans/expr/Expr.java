package com.github.valesnikov.calculans.expr;

public interface Expr {
    String show();

    double doubleValue();

    Expr simplify();

    Expr add(Expr other);

    default Expr subtract(Expr other) {
        return add(other.negate());
    }

    Expr multiply(Expr other);

    Expr divide(Expr other);

    Expr negate();

    Expr abs();

    int compareTo(Expr other);
}
