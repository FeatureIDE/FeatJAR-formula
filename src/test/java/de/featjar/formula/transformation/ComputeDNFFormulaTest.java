package de.featjar.formula.transformation;

import static de.featjar.formula.structure.Expressions.*;

class ComputeDNFFormulaTest {
    // @Test
    public void toDNF() {
        // TODO: currently buggy
        TransformationTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), or(literal("c"))),
                ComputeDNFFormula::new,
                or(and(literal("c"), literal("b")), and(literal("c"), literal("a"))));
        TransformationTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), literal("c")),
                ComputeDNFFormula::new,
                or(and(literal("c"), literal("b")), and(literal("a"), literal("c"))));
    }
}
