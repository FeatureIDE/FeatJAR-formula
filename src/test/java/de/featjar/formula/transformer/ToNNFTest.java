package de.featjar.formula.transformer;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class ToNNFTest {
    @Test
    public void toNNF() {
        TransformerTest.traverseAndAssertFormulaEquals(
                implies(literal("a"), False),
                ToNNF::new,
                or(literal(false, "a"), False));
        TransformerTest.traverseAndAssertFormulaEquals(
                not(or(literal("a"), literal("b"))),
                ToNNF::new,
                and(literal(false, "a"), literal(false, "b")));
    }
}