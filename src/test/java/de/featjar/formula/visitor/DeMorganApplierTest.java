package de.featjar.formula.visitor;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class DeMorganApplierTest {
    @Test
    void doesNothingForNNF() {
        VisitorTest.traverseAndAssertSameFormula(and(literal("x"), literal(false, "y")), new DeMorganApplier());
    }

    @Test
    void eliminatesNotBeforeLiteral() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), not(literal("y"))),
                new DeMorganApplier(),
                and(literal("x"), literal(false, "y")));
    }

    @Test
    void eliminatesNotBeforeAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                not(and(literal("x"), literal("y"))),
                new DeMorganApplier(),
                or(literal(false, "x"), literal(false, "y")));
    }

    @Test
    void eliminatesNotBeforeAndAndBeforeLiteral() {
        VisitorTest.traverseAndAssertFormulaEquals(
                not(and(literal("x"), not(literal("y")))),
                new DeMorganApplier(),
                or(literal(false, "x"), literal(true, "y")));
    }
}