package de.featjar.formula.visitor;

import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.formula.Formula;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {
    public static void traverseAndAssertSameFormula(Formula oldFormula, ITreeVisitor<Formula, ?> treeVisitor) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblems().isEmpty());
        assertEquals(oldFormula, newFormula);
    }

    public static void traverseAndAssertFormulaEquals(Formula oldFormula, ITreeVisitor<Formula, ?> treeVisitor, Formula assertFormula) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblems().isEmpty());
        assertNotEquals(oldFormula, newFormula);
        assertEquals(assertFormula, newFormula);
    }
}
