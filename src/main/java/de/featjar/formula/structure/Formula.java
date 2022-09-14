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

import de.featjar.base.tree.structure.Traversable;
import de.featjar.formula.structure.assignment.Assignment;
import de.featjar.formula.structure.formula.literal.False;
import de.featjar.formula.structure.formula.literal.True;
import de.featjar.formula.tmp.ValueVisitor;

import java.util.List;

/**
 * A propositional or first-order formula.
 * Implemented recursively as a tree.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Formula<T extends Formula<T>> extends Traversable<T> {
    /**
     * A tautology.
     */
    True TRUE = True.getInstance();

    /**
     * A contradiction.
     */
    False FALSE = False.getInstance();

    /**
     * {@return the name of this formula's operator}
     */
    String getName();

    /**
     * {@return the type this formula evaluates to}
     */
    Class<?> getType();

    /**
     * {@return the evaluation of this formula on a given list of values}
     *
     * @param values the values
     */
    Object evaluate(List<?> values);

    /**
     * {@return the evaluation of this formula on a given assignment}
     *
     * @param assignment the assignment
     */
    default Object evaluate(Assignment assignment) {
        return traverse(new ValueVisitor(assignment));
    }

    @Override
    Formula cloneNode();
}
