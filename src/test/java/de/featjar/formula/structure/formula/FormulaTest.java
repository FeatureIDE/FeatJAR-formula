package de.featjar.formula.structure.formula;

import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {
    IFormula formula = new Implies(new Literal("a"), Expressions.False);
    IFormula cnf = new Or(new Literal(false, "a"), Expressions.False);
    IFormula dnf = new And(new Literal(false, "a"), Expressions.False);
    IFormula clausalCnf = new And(new Or(new Literal(false, "a"), Expressions.False));
    IFormula clausalDnf = new Or(new And(new Literal(false, "a"), Expressions.False));

    @Test
    void getType() {
        assertEquals(Boolean.class, formula.getType());
    }

    @Test
    void isCNF() {
        assertFalse(formula.isCNF());
        assertTrue(cnf.isCNF());
        assertTrue(clausalCnf.isCNF());
        assertTrue(dnf.isCNF());
        assertFalse(clausalDnf.isCNF());
    }

    @Test
    void isDNF() {
        assertFalse(formula.isDNF());
        assertTrue(cnf.isDNF());
        assertFalse(clausalCnf.isDNF());
        assertTrue(dnf.isDNF());
        assertTrue(clausalDnf.isDNF());
    }

    @Test
    void isClausalCNF() {
        assertFalse(formula.isClausalNormalForm(IFormula.NormalForm.CNF));
        assertFalse(cnf.isClausalNormalForm(IFormula.NormalForm.CNF));
        assertTrue(clausalCnf.isClausalNormalForm(IFormula.NormalForm.CNF));
        assertFalse(dnf.isClausalNormalForm(IFormula.NormalForm.CNF));
        assertFalse(clausalDnf.isClausalNormalForm(IFormula.NormalForm.CNF));
    }

    @Test
    void isClausalDNF() {
        assertFalse(formula.isClausalNormalForm(IFormula.NormalForm.DNF));
        assertFalse(cnf.isClausalNormalForm(IFormula.NormalForm.DNF));
        assertFalse(clausalCnf.isClausalNormalForm(IFormula.NormalForm.DNF));
        assertFalse(dnf.isClausalNormalForm(IFormula.NormalForm.DNF));
        assertTrue(clausalDnf.isClausalNormalForm(IFormula.NormalForm.DNF));
    }

    @Test
    void toCNF() {
        // TODO
    }

    @Test
    void toDNF() {
        // TODO
    }

    @Test
    void toClausalCNF() {
        // TODO
    }

    @Test
    void toClausalDNF() {
        // TODO
    }
}