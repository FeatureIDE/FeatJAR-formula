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

    // todo: this does not do anything right now (because the tree visitor does not modify the current node), but logically, it should also be simplified!
    @Test
    void doesNotEliminateNotBeforeAndNotNested() {
        VisitorTest.traverseAndAssertSameFormula(not(and(literal("x"), literal("y"))), new DeMorganApplier());
    }

    @Test
    void eliminatesNotBeforeAndNested() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(not(and(literal("x"), literal("y")))),
                new DeMorganApplier(),
                and(or(literal(false, "x"), literal(false, "y"))));
    }

    @Test
    void eliminatesNotBeforeAndAndBeforeLiteralNested() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(not(and(literal("x"), not(literal("y"))))),
                new DeMorganApplier(),
                and(or(literal(false, "x"), literal(true, "y"))));
    }
}