package de.featjar.formula.transformer;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class TransformNNFFormulaTest {
    @Test
    public void toNNF() {
        TransformerTest.traverseAndAssertFormulaEquals(
                implies(literal("a"), False),
                TransformNNFFormula::new,
                or(literal(false, "a"), False));
        TransformerTest.traverseAndAssertFormulaEquals(
                not(or(literal("a"), literal("b"))),
                TransformNNFFormula::new,
                and(literal(false, "a"), literal(false, "b")));
    }
}