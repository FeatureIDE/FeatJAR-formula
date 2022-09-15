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

import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.InvertiblePredicate;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Applies De Morgan's laws to push {@link Not} down towards a {@link Predicate}.
 *
 * @author Sebastian Krieter
 */
public class DeMorganApplier implements TreeVisitor<Formula, Void> {

    @Override
    public TraversalAction firstVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if (formula instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (formula instanceof Connective) {
            formula.replaceChildren(expression -> replace((Formula) expression));
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    protected Formula replace(Formula formula) {
        Formula newFormula = formula;
        while (newFormula instanceof Not) {
            final Formula notChild = (Formula) ((Not) newFormula).getExpression();
            if (notChild instanceof InvertiblePredicate) {
                newFormula = ((Literal) notChild).invert();
            } else if (notChild instanceof Not) {
                newFormula = (Formula) ((Not) notChild).getExpression();
            } else if (notChild instanceof Or) {
                newFormula = new And(notChild.getChildren().stream().map(Not::new).collect(Collectors.toList()));
            } else if (notChild instanceof And) {
                newFormula = new Or(notChild.getChildren().stream().map(Not::new).collect(Collectors.toList()));
            }
        }
        return newFormula;
    }
}
