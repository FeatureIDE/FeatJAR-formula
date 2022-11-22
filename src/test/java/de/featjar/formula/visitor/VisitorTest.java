package de.featjar.formula.visitor;

import de.featjar.base.data.Problem;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.formula.Formula;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {
    public static void traverseAndAssertSameFormula(Formula oldFormula, TreeVisitor<Formula, ?> treeVisitor) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblems().isEmpty());
        assertEquals(oldFormula, newFormula);
    }

    public static void traverseAndAssertFormulaEquals(Formula oldFormula, TreeVisitor<Formula, ?> treeVisitor, Formula assertFormula) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        assertTrue(Trees.traverse(newFormula, treeVisitor).getProblems().isEmpty());
        assertNotEquals(oldFormula, newFormula);
        assertEquals(assertFormula, newFormula);
    }
}
