package de.featjar.formula.visitor;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

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
                and(literal("x"), or(literal("x"))),
                new AndOrSimplifier(),
                and(literal("x"), literal("x")));
    }

    @Test
    void simplifiesUnaryAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), and(literal("x"))),
                new AndOrSimplifier(),
                or(literal("x"), literal("x")));
    }

    @Test
    void mergesAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), and(literal("x"))),
                new AndOrSimplifier(),
                and(literal("x"), literal("x")));
    }

    @Test
    void mergesOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), or(literal("x"))),
                new AndOrSimplifier(),
                or(literal("x"), literal("x")));
    }

    @Test
    void trueDominatesOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), True),
                new AndOrSimplifier(),
                or(True)); // TODO: this could even be implemented more strict by simplifying to True
    }

    @Test
    void falseDominatesAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), False),
                new AndOrSimplifier(),
                and(False)); // TODO: this could even be implemented more strict by simplifying to True
    }
}