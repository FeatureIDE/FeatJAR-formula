package de.featjar.formula.tmp;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.connective.And;

import java.util.Arrays;

public class FormulaBuilder {
    enum Policy {
        CLONE_ALWAYS, CLONE_IF_NEEDED, THROW
    }

    protected final TermMap termMap;

    public FormulaBuilder() {
        termMap = new TermMap();
    }

    public FormulaBuilder(Expression expression) {
        this.termMap = expression.getTermMap().get();
    }

    public FormulaBuilder(Expression... expressions) {
        // need to merge, consider special case 0 and 1 formulas
    }

    public static FormulaBuilder of(Expression... expressions) {

    }

    // also implement Formula.getBuilder(Formula... formulas)

    public Expression and(Expression... expressions) {
        And and = new And(); // maybe remove all constructors? rename to AndFormula/Connective?
        and.setChildren(Arrays.asList(expressions));
    }

    // what if x is renamed in a submodel to y? what happens to the x in the termmap of the parent model?
    // maybe a termmap is a tree of termmap, falling back to children?
    // to support uvl, we need to support that a knows b, but b does not know a, and all changes of b reflect on a (so no cloning, no adapting)
}
