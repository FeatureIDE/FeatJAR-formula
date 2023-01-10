package de.featjar.formula.visitor;

import static de.featjar.formula.structure.Expressions.*;

import org.junit.jupiter.api.Test;

class ConnectiveSimplifierTest {

    @Test
    void doesNotSimplifyAndOrNot() {
        VisitorTest.traverseAndAssertSameFormula(and(literal("x"), not(literal("y"))), new ConnectiveSimplifier());
    }

    @Test
    void simplifiesImplies() {
        VisitorTest.traverseAndAssertFormulaEquals(
                implies(literal("x"), literal("y")), new ConnectiveSimplifier(), or(not(literal("x")), literal("y")));
    }

    @Test
    void simplifiesAtMostK() {
        VisitorTest.traverseAndAssertFormulaEquals(
                atMost(2, literal("x"), literal("y"), literal("z")),
                new ConnectiveSimplifier(),
                and(or(not(literal("x")), not(literal("y")), not(literal("z")))));
    }

    // TODO: test other operators
}
