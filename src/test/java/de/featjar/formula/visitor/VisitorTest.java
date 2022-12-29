package de.featjar.formula.visitor;

import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.formula.IFormula;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {
    public static void traverseAndAssertSameFormula(IFormula oldFormula, ITreeVisitor<IFormula, ?> treeVisitor) {
        IFormula newFormula = (IFormula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblem().isEmpty());
        assertEquals(oldFormula, newFormula);
    }

    public static void traverseAndAssertFormulaEquals(IFormula oldFormula, ITreeVisitor<IFormula, ?> treeVisitor, IFormula assertFormula) {
        IFormula newFormula = (IFormula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblem().isEmpty());
        assertNotEquals(oldFormula, newFormula);
        assertEquals(assertFormula, newFormula);
    }
}
