package de.featjar.formula.transformer;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class ToDNFTest {
    @Test
    public void doesNothing() {
        TransformerTest.traverseAndAssertSameFormula(or(and(literal("a"), literal("b")), and(literal("c"))), ToDNF::new);
    }

    //@Test
    public void toDNF() {
        // todo: currently buggy
        TransformerTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), or(literal("c"))),
                ToDNF::new,
                or(and(literal("c"), literal("b")), and(literal("c"), literal("a"))));
        TransformerTest.traverseAndAssertFormulaEquals(
                and(or(literal("a"), literal("b")), literal("c")),
                ToDNF::new,
                or(and(literal("c"), literal("b")), and(literal("a"), literal("c"))));
    }
}