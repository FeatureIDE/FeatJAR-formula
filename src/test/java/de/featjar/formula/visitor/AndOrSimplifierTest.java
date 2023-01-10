package de.featjar.formula.visitor;

import static de.featjar.formula.structure.Expressions.*;

import org.junit.jupiter.api.Test;

class AndOrSimplifierTest {
    @Test
    void doesNothingForTautology() {
        VisitorTest.traverseAndAssertSameFormula(or(literal("x"), not(literal("x"))), new AndOrSimplifier());
    }

    @Test
    void nothingHappensForContradiction() {
        VisitorTest.traverseAndAssertSameFormula(and(literal("x"), not(literal("x"))), new AndOrSimplifier());
    }

    @Test
    void simplifiesUnaryOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), or(literal("x"))), new AndOrSimplifier(), and(literal("x"), literal("x")));
    }

    @Test
    void simplifiesUnaryAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), and(literal("x"))), new AndOrSimplifier(), or(literal("x"), literal("x")));
    }

    @Test
    void mergesAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), and(literal("x"))), new AndOrSimplifier(), and(literal("x"), literal("x")));
    }

    @Test
    void mergesOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), or(literal("x"))), new AndOrSimplifier(), or(literal("x"), literal("x")));
    }

    @Test
    void simplifyComplex() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(
                        literal("a"),
                        and(literal("b"), literal("c"), True),
                        and(literal("b"), False),
                        or(literal("x"), False)),
                new AndOrSimplifier(),
                and(literal("a"), literal("b"), literal("c"), True, literal("b"), False, or(literal("x"), False)));
    }
}
