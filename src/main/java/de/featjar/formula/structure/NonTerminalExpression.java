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
package de.featjar.formula.structure;

import de.featjar.base.tree.structure.ATree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A non-terminal node in a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class NonTerminalExpression extends ATree<Expression> implements Expression {
    protected NonTerminalExpression(Expression... children) {
        if (children.length > 0)
            super.setChildren(Arrays.asList(children));
    }

    protected NonTerminalExpression(List<? extends Expression> children) {
        super.setChildren(children);
    }

    @Override
    public boolean equalsNode(Expression other) {
        return (getClass() == other.getClass()) &&
                Objects.equals(getName(), other.getName()) &&
                Objects.equals(getType(), other.getType());
    }

    @Override
    public int hashCodeNode() {
        return Objects.hash(getClass(), getName(), getType());
    }

    @Override
    public String toString() {
        if (hasChildren()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append("(");
            for (final Expression child : getChildren()) {
                sb.append(child.getName());
                sb.append(", ");
            }
            sb.replace(sb.length() - 2, sb.length(), ")");
            return sb.toString();
        } else {
            return getName();
        }
    }
}
