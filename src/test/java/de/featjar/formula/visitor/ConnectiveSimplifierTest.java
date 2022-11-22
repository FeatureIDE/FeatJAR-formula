package de.featjar.formula.visitor;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class ConnectiveSimplifierTest {

    @Test
    void doesNotSimplifyAndOrNot() {
        VisitorTest.traverseAndAssertSameFormula(reference(and(literal("x"), not(literal("y")))), new ConnectiveSimplifier());
    }

    @Test
    void simplifiesImplies() {
        VisitorTest.traverseAndAssertFormulaEquals(
                reference(implies(literal("x"), literal("y"))),
                new ConnectiveSimplifier(),
                reference(or(not(literal("x")), literal("y"))));
    }

    @Test
    void simplifiesAtMostK() {
        VisitorTest.traverseAndAssertFormulaEquals(
                reference(atMost(2, literal("x"), literal("y"), literal("z"))),
                new ConnectiveSimplifier(),
                reference(and(or(not(literal("x")), not(literal("y")), not(literal("z"))))));
    }

    // TODO: test other operators
}