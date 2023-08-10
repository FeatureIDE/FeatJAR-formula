/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.analysis.value.AValueAssignment;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.term.value.Variable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Given a variable assignment, evaluates a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Evaluator implements ITreeVisitor<IExpression, Object> {
    private final LinkedList<Object> values = new LinkedList<>();

    private final AValueAssignment valueAssignment;
    private Boolean defaultBooleanValue;

    public Evaluator(AValueAssignment valueAssignment) {
        this.valueAssignment = valueAssignment;
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
    public Result<Object> getResult() {
        return Result.ofNullable(values.peek());
    }

    @Override
    public TraversalAction lastVisit(List<IExpression> path) {
        final IExpression expression = ITreeVisitor.getCurrentNode(path);
        if (expression instanceof Variable) {
            final Variable variable = (Variable) expression;
            final String variableName = variable.getName();
            final Object value = valueAssignment.getValue(variableName).orElse(null);
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
            final List<Object> arguments = values.subList(0, expression.getChildrenCount());
            Collections.reverse(arguments);
            final Object value = expression.evaluate(arguments);
            arguments.clear();
            values.push(value);
        }
        return TraversalAction.CONTINUE;
    }
}
