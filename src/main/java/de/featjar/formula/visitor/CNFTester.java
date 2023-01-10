package de.featjar.formula.visitor;

import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.IPredicate;
import java.util.List;

/**
 * Tests whether a formula is in conjunctive normal form.
 * The formula {@code new Or(new And(new Literal("x")))} is neither in CNF nor in clausal CNF.
 * The formula {@code new Literal("x")} is in CNF, but not in clausal CNF.
 * The formula {@code new And(new Or(new Literal("x")))} is in CNF and in clausal CNF.
 */
public class CNFTester extends ANormalFormTester {

    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IFormula formula = getCurrentNode(path);
        if (formula instanceof And) {
            return processLevelOne(path, formula, Or.class);
        } else if (formula instanceof Or) {
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
