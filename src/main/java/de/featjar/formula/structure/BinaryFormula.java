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

import java.util.Collections;

/**
 * A logical formula that has exactly two operands.
 *
 * @author Elias Kuiter
 */
public interface BinaryFormula extends Formula {
    default Range getChildrenCountRange() {
        return Range.exactly(2);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default Formula getLeftFormula() {
        return getFirstChild().get();
    }

    default void setLeftFormula(Formula formula) {
        replaceChild(0, formula);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default Formula getRightFormula() {
        return getLastChild().get();
    }

    default void setRightFormula(Formula formula) {
        replaceChild(1, formula);
    }
}
