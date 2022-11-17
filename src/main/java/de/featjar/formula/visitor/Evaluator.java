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

import de.featjar.formula.analysis.Assignment;
import de.featjar.formula.structure.Expression;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.term.value.Variable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Given a variable assignment, evaluates a formula.
 *
 * @author Sebastian Krieter
 */
public class Evaluator implements TreeVisitor<Expression, Object> {
    private final LinkedList<Object> values = new LinkedList<>();

    private final Assignment variableAssignment;
    private Boolean defaultBooleanValue;

    public Evaluator(Assignment variableAssignment) {
        this.variableAssignment = variableAssignment;
    }

    public Boolean getDefaultBooleanValue() {
        return defaultBooleanValue;
    }

    public void setDefaultBooleanValue(Boolean defaultBooleanValue) {
        this.defaultBooleanValue = defaultBooleanValue;
    }

    @Override
    public void reset() {
        values.clear();
    }

    @Override
    public Optional<Object> getResult() {
        return Optional.ofNullable(values.peek());
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Variable) {
            final Variable variable = (Variable) expression;
            final String variableName = variable.getName();
            final Object value = variableAssignment.get(variableName).orElse(null);
            if (value != null) {
                if (variable.getType().isInstance(value)) {
                    values.push(value);
                } else {
                    throw new IllegalArgumentException(String.valueOf(value));
                }
            } else {
                if (variable.getType() == Boolean.class) {
                    values.push(defaultBooleanValue);
                } else {
                    values.push(null);
                }
            }
        } else {
            final List<Object> arguments = values.subList(0, expression.getChildren().size());
            Collections.reverse(arguments);
            final Object value = expression.evaluate(arguments);
            arguments.clear();
            values.push(value);
        }
        return TraversalAction.CONTINUE;
    }
}
