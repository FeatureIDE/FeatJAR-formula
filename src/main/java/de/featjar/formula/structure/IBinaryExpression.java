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

import de.featjar.base.data.Range;

/**
 * An expression with exactly two operands.
 *
 * @author Elias Kuiter
 */
public interface IBinaryExpression extends IExpression {
    default Range getChildrenCountRange() {
        return Range.exactly(2);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default IExpression getLeftExpression() {
        return getFirstChild().get();
    }

    default void setLeftExpression(IExpression expression) {
        replaceChild(0, expression);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default IExpression getRightFormula() {
        return getLastChild().get();
    }

    default void setRightExpression(IExpression expression) {
        replaceChild(1, expression);
    }
}
