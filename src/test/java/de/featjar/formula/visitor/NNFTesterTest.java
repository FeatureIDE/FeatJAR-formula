package de.featjar.formula.visitor;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

import de.featjar.formula.structure.formula.IFormula;
import org.junit.jupiter.api.Test;

class NNFTesterTest {
    @Test
    void testNNF() {
        assertFalse(not(not(literal("x"))).isNNF());
        assertFalse(not(not(literal("x"))).isClausalNormalForm(IFormula.NormalForm.NNF));
        assertTrue(not(literal("x")).isNNF());
        assertFalse(not(literal("x")).isClausalNormalForm(IFormula.NormalForm.NNF));
        assertTrue(literal(false, "x").isNNF());
        assertTrue(literal(false, "x").isClausalNormalForm(IFormula.NormalForm.NNF));
        assertFalse(implies(literal("x"), literal("y")).isNNF());
        assertFalse(implies(literal("x"), literal("y")).isClausalNormalForm(IFormula.NormalForm.NNF));
    }
}
