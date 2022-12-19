package de.featjar.formula.transformer;

import static de.featjar.formula.structure.Expressions.*;

class ComputeDNFFormulaTest {
    //@Test
    public void toDNF() {
        // TODO: currently buggy
        TransformerTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), or(literal("c"))),
                ComputeDNFFormula::new,
                or(and(literal("c"), literal("b")), and(literal("c"), literal("a"))));
        TransformerTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), literal("c")),
                ComputeDNFFormula::new,
                or(and(literal("c"), literal("b")), and(literal("a"), literal("c"))));
    }
}