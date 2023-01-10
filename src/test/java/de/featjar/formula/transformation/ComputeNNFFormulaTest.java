package de.featjar.formula.transformation;

import static de.featjar.formula.structure.Expressions.*;

import org.junit.jupiter.api.Test;

class ComputeNNFFormulaTest {
    @Test
    public void toNNF() {
        TransformationTest.traverseAndAssertFormulaEquals(
                implies(literal("a"), False), ComputeNNFFormula::new, literal(false, "a"));
        TransformationTest.traverseAndAssertFormulaEquals(
                not(or(literal("a"), literal("b"))),
                ComputeNNFFormula::new,
                and(literal(false, "a"), literal(false, "b")));
    }
}
