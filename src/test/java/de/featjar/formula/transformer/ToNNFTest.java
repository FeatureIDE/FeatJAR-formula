package de.featjar.formula.transformer;

import de.featjar.base.data.Computation;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.visitor.VisitorTest;
import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;
import static de.featjar.formula.structure.Expressions.literal;

class ToNNFTest {

    // todo: this does not do anything right now (because the tree visitor does not modify the current node), but logically, it should also be transformed!
    @Test
    public void doesNothing() {
        TransformerTest.traverseAndAssertSameFormula(new Implies(new Literal("a"), Expressions.False), ToNNF::new);
        TransformerTest.traverseAndAssertSameFormula(not(or(literal("a"), literal("b"))), ToNNF::new);
    }

    @Test
    public void toNNFNested() {
        TransformerTest.traverseAndAssertFormulaEquals(and(implies(literal("a"), False)), ToNNF::new, and(or(literal(false, "a"), False)));
        TransformerTest.traverseAndAssertFormulaEquals(
                and(not(or(literal("a"), literal("b")))),
                ToNNF::new,
                and(literal(false, "a"), literal(false, "b")));
    }
}