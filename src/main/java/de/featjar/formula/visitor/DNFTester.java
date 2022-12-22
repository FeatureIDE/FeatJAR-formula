package de.featjar.formula.visitor;

import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.IPredicate;

import java.util.List;

/**
 * Tests whether a formula is in disjunctive normal form.
 * The formula {@code new And(new Or(new Literal("x")))} is neither in DNF nor in clausal DNF.
 * The formula {@code new Literal("x")} is in DNF, but not in clausal DNF.
 * The formula {@code new Or(new And(new Literal("x")))} is in DNF and in clausal DNF.
 */
public class DNFTester extends ANormalFormTester {

    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IFormula formula = getCurrentNode(path);
        if (formula instanceof Or) {
            return processLevelOne(path, formula, And.class);
        } else if (formula instanceof And) {
            return processLevelTwo(path, formula);
        } else if (formula instanceof IPredicate) {
            return processLevelThree(path);
        } else {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
    }
}
