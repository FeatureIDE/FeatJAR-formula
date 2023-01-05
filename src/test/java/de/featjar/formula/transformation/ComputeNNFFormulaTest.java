package de.featjar.formula.transformation;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class ComputeNNFFormulaTest {
    @Test
    public void toNNF() {
        TransformationTest.traverseAndAssertFormulaEquals(
                implies(literal("a"), False),
                ComputeNNFFormula::new,
                literal(false, "a"));
        TransformationTest.traverseAndAssertFormulaEquals(
                not(or(literal("a"), literal("b"))),
                ComputeNNFFormula::new,
                and(literal(false, "a"), literal(false, "b")));
    }
}