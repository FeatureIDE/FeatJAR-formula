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

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;

import java.util.List;

/**
 * Tests whether a formula is in disjunctive normal form.
 * The formula {@code new Literal("x")} is in DNF, but not in clausal DNF.
 * The formula {@code new Or(new And(new Literal("x")))} is in DNF and in clausal DNF.
 * *
 * @author Sebastian Krieter
 */
public class DNFTester extends NormalFormTester {

    @Override
    public TraversalAction firstVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if (formula instanceof Or) {
            if (path.size() > 1) {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
            for (final Expression child : formula.getChildren()) {
                if (!(child instanceof And)) {
                    if (!(child instanceof Predicate)) {
                        isNormalForm = false;
                        isClausalNormalForm = false;
                        return TraversalAction.SKIP_ALL;
                    }
                    isClausalNormalForm = false;
                }
            }
            return TraversalAction.CONTINUE;
        } else if (formula instanceof And) {
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
        } else if (formula instanceof Predicate) {
            if (path.size() > 3) {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
            if (path.size() < 3) {
                isClausalNormalForm = false;
            }
            return TraversalAction.SKIP_CHILDREN;
        } else {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
    }
}
