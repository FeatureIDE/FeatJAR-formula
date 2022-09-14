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
import de.featjar.formula.tmp.TermMap;
import de.featjar.formula.tmp.TermMap.Constant;
import de.featjar.formula.tmp.TermMap.Variable;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.List;

public class VariableMapSetter implements TreeVisitor<Void, Expression> {

    private final TermMap termMap;

    public VariableMapSetter(TermMap termMap) {
        this.termMap = termMap;
    }

    private Expression replaceValueTerms(Expression expression) {
        if (expression instanceof Variable) {
            final Variable replacement = termMap
                    .getVariable(expression.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain variable with name " + expression.getName()));
            return replacement;
        } else if (expression instanceof Constant) {
            final Constant replacement = termMap
                    .getConstant(expression.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain constant with name " + expression.getName()));
            return replacement;
        }
        return expression;
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        expression.replaceChildren(this::replaceValueTerms);
        return TreeVisitor.super.lastVisit(path);
    }
}
