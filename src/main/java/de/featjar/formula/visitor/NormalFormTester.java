/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Predicate;

import java.util.List;

/**
 * Tests whether a formula is in (clausal) normal form.
 * Clausal normal form is a special case of each normal form and usually easier to process in an automated fashion.
 * Thus, we usually allow normal forms as input and use clausal normal form as output.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class NormalFormTester implements ITreeVisitor<Formula, Boolean> {

    protected boolean isNormalForm = true;
    protected boolean isClausalNormalForm = true;

    @Override
    public void reset() {
        isNormalForm = true;
        isClausalNormalForm = true;
    }

    @Override
    public Result<Boolean> getResult() {
        return Result.of(isNormalForm);
    }

    public boolean isNormalForm() {
        return isNormalForm;
    }

    public boolean isClausalNormalForm() {
        return isClausalNormalForm;
    }

    protected TraversalAction processLevelOne(List<Formula> path, Formula formula, Class<? extends Connective> connectiveClass) {
        if (path.size() > 1) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        for (final Expression child : formula.getChildren()) {
            if (!connectiveClass.isInstance(child)) {
                if (!(child instanceof Predicate)) {
                    isNormalForm = false;
                    isClausalNormalForm = false;
                    return TraversalAction.SKIP_ALL;
                }
                isClausalNormalForm = false;
            }
        }
        return TraversalAction.CONTINUE;
    }

    protected TraversalAction processLevelTwo(List<Formula> path, Formula formula) {
        if (path.size() > 2) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        if (path.size() < 2) {
            isClausalNormalForm = false;
        }
        for (final Expression child : formula.getChildren()) {
            if (!(child instanceof Predicate)) {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
        }
        return TraversalAction.CONTINUE;
    }

    protected TraversalAction processLevelThree(List<Formula> path) {
        if (path.size() > 3) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        if (path.size() < 3) {
            isClausalNormalForm = false;
        }
        return TraversalAction.SKIP_CHILDREN;
    }

    /**
     * Tests whether a formula is in negation normal form.
     * The formula {@code new Not(new Not(new Literal("x")))} is neither in NNF nor in clausal NNF.
     * The formula {@code new Not(new Literal("x"))} is in NNF, but not in clausal NNF.
     * The formula {@code new Literal(false, "x")} is in NNF and in clausal NNF.
     * TODO: is Implies(a, b) in NNF? do we allow complex operators for NNF or not? currently we do.
     */
    public static class NNF extends NormalFormTester {

        @Override
        public TraversalAction firstVisit(List<Formula> path) {
            final Formula formula = getCurrentNode(path);
            if (formula instanceof Predicate) {
                return TraversalAction.SKIP_CHILDREN;
            } else if (formula instanceof Connective) {
                if (formula instanceof Not) {
                    isClausalNormalForm = false;
                    if (!(((Not) formula).getExpression() instanceof Predicate)) {
                        isNormalForm = false;
                    }
                }
                return TraversalAction.CONTINUE;
            } else {
                return TraversalAction.FAIL;
            }
        }
    }

    /**
     * Tests whether a formula is in conjunctive normal form.
     * The formula {@code new Or(new And(new Literal("x")))} is neither in CNF nor in clausal CNF.
     * The formula {@code new Literal("x")} is in CNF, but not in clausal CNF.
     * The formula {@code new And(new Or(new Literal("x")))} is in CNF and in clausal CNF.
     */
    public static class CNF extends NormalFormTester {

        @Override
        public TraversalAction firstVisit(List<Formula> path) {
            final Formula formula = getCurrentNode(path);
            if (formula instanceof And) {
                return processLevelOne(path, formula, Or.class);
            } else if (formula instanceof Or) {
                return processLevelTwo(path, formula);
            } else if (formula instanceof Predicate) {
                return processLevelThree(path);
            } else {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
        }
    }

    /**
     * Tests whether a formula is in disjunctive normal form.
     * The formula {@code new And(new Or(new Literal("x")))} is neither in DNF nor in clausal DNF.
     * The formula {@code new Literal("x")} is in DNF, but not in clausal DNF.
     * The formula {@code new Or(new And(new Literal("x")))} is in DNF and in clausal DNF.
     */
    public static class DNF extends NormalFormTester {

        @Override
        public TraversalAction firstVisit(List<Formula> path) {
            final Formula formula = getCurrentNode(path);
            if (formula instanceof Or) {
                return processLevelOne(path, formula, And.class);
            } else if (formula instanceof And) {
                return processLevelTwo(path, formula);
            } else if (formula instanceof Predicate) {
                return processLevelThree(path);
            } else {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
        }
    }
}
