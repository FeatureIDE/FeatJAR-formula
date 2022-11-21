package de.featjar.formula.visitor;

import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.formula.Formula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VisitorTest {
    public static void traverseAndAssertSameFormula(Formula oldFormula, TreeVisitor<Formula, ?> treeVisitor) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        Trees.traverse(newFormula, treeVisitor);
        assertEquals(oldFormula, newFormula);
    }

    public static void traverseAndAssertFormulaEquals(Formula oldFormula, TreeVisitor<Formula, ?> treeVisitor, Formula assertFormula) {
        Formula newFormula = (Formula) oldFormula.cloneTree();
        Trees.traverse(newFormula, treeVisitor);
        assertNotEquals(oldFormula, newFormula);
        assertEquals(assertFormula, newFormula);
    }
}
