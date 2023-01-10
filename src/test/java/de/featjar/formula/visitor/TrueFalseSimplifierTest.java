package de.featjar.formula.visitor;

import static de.featjar.formula.structure.Expressions.*;
import static de.featjar.formula.structure.Expressions.literal;

import org.junit.jupiter.api.Test;

class TrueFalseSimplifierTest {
    @Test
    void trueDominatesOr() {
        VisitorTest.traverseAndAssertFormulaEquals(or(literal("x"), True), new TrueFalseSimplifier(), or(True));
    }

    @Test
    void falseDominatesAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(and(literal("x"), False), new TrueFalseSimplifier(), and(False));
    }

    @Test
    void trueNeutralAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), True), new TrueFalseSimplifier(), and(literal("x")));
    }

    @Test
    void falseNeutralOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), False), new TrueFalseSimplifier(), or(literal("x")));
    }

    @Test
    void simplifyComplex() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(
                        literal("a"),
                        and(literal("b"), literal("c"), True),
                        and(literal("b"), False),
                        or(literal("x"), False)),
                new TrueFalseSimplifier(),
                // this could be simplified even further
                and(literal("a"), and(literal("b"), literal("c")), and(False), or(literal("x"))));
    }
}
