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
package de.featjar.formula.transform;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;

import java.util.List;

public class CNFTester extends NFTester {

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof And) {
            if (path.size() > 1) {
                isNf = false;
                isClausalNf = false;
                return TraversalAction.SKIP_ALL;
            }
            for (final Expression child : expression.getChildren()) {
                if (!(child instanceof Or)) {
                    if (!(child instanceof Predicate)) {
                        isNf = false;
                        isClausalNf = false;
                        return TraversalAction.SKIP_ALL;
                    }
                    isClausalNf = false;
                }
            }
            return TraversalAction.CONTINUE;
        } else if (expression instanceof Or) {
            if (path.size() > 2) {
                isNf = false;
                isClausalNf = false;
                return TraversalAction.SKIP_ALL;
            }
            if (path.size() < 2) {
                isClausalNf = false;
            }
            for (final Expression child : expression.getChildren()) {
                if (!(child instanceof Predicate)) {
                    isNf = false;
                    isClausalNf = false;
                    return TraversalAction.SKIP_ALL;
                }
            }
            return TraversalAction.CONTINUE;
        } else if (expression instanceof Predicate) {
            if (path.size() > 3) {
                isNf = false;
                isClausalNf = false;
                return TraversalAction.SKIP_ALL;
            }
            if (path.size() < 3) {
                isClausalNf = false;
            }
            return TraversalAction.SKIP_CHILDREN;
        } else {
            isNf = false;
            isClausalNf = false;
            return TraversalAction.SKIP_ALL;
        }
    }
}
