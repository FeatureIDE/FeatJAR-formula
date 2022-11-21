package de.featjar.formula.visitor;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class ConnectiveSimplifierTest {

    @Test
    void doesNotSimplifyAndOrNot() {
        VisitorTest.traverseAndAssertSameFormula(and(literal("x"), not(literal("y"))), new ConnectiveSimplifier());
    }

    @Test
    void simplifiesNestedImplies() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(implies(literal("x"), literal("y"))),
                new ConnectiveSimplifier(),
                and(or(not(literal("x")), literal("y"))));
    }

    // todo: this does not do anything right now (because the tree visitor does not modify the current node), but logically, it should also be simplified!
    @Test
    void doesNotSimplifyRootImplies() {
        VisitorTest.traverseAndAssertSameFormula(implies(literal("x"), literal("y")), new ConnectiveSimplifier());
    }

    @Test
    void simplifiesNestedAtMostK() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(atMost(2, literal("x"), literal("y"), literal("z"))),
                new ConnectiveSimplifier(),
                and(and(or(not(literal("x")), not(literal("y")), not(literal("z"))))));
    }

    // todo: test other operators
}