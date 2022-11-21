package de.featjar.formula.structure.formula;

import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {
    Formula formula = new Implies(new Literal("a"), Expressions.False);
    Formula cnf = new Or(new Literal(false, "a"), Expressions.False);
    Formula dnf = new And(new Literal(false, "a"), Expressions.False);
    Formula clausalCnf = new And(new Or(new Literal(false, "a"), Expressions.False));
    Formula clausalDnf = new Or(new And(new Literal(false, "a"), Expressions.False));

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
        assertFalse(formula.isClausalNormalForm(Formula.NormalForm.CNF));
        assertFalse(cnf.isClausalNormalForm(Formula.NormalForm.CNF));
        assertTrue(clausalCnf.isClausalNormalForm(Formula.NormalForm.CNF));
        assertFalse(dnf.isClausalNormalForm(Formula.NormalForm.CNF));
        assertFalse(clausalDnf.isClausalNormalForm(Formula.NormalForm.CNF));
    }

    @Test
    void isClausalDNF() {
        assertFalse(formula.isClausalNormalForm(Formula.NormalForm.DNF));
        assertFalse(cnf.isClausalNormalForm(Formula.NormalForm.DNF));
        assertFalse(clausalCnf.isClausalNormalForm(Formula.NormalForm.DNF));
        assertFalse(dnf.isClausalNormalForm(Formula.NormalForm.DNF));
        assertTrue(clausalDnf.isClausalNormalForm(Formula.NormalForm.DNF));
    }

    @Test
    void toCNF() {
        // todo
    }

    @Test
    void toDNF() {
        // todo
    }

    @Test
    void toClausalCNF() {
        // todo
    }

    @Test
    void toClausalDNF() {
        // todo
    }
}