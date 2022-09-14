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

import de.featjar.formula.structure.NamedTermMap.ValueTerm;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.structure.Traversable;
import de.featjar.formula.structure.atomic.literal.False;
import de.featjar.formula.structure.atomic.literal.True;

import java.util.List;
import java.util.Optional;

/**
 * A logical formula.
 * Can be propositional (using {@link TermMap} and {@link de.featjar.formula.structure.atomic.literal.Literal})
 * or first-order (using {@link de.featjar.formula.structure.atomic.predicate.Predicate},
 * {@link de.featjar.formula.structure.term.Term}, and {@link de.featjar.formula.structure.connective.Quantifier}).
 * Implemented recursively as a tree.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Formula extends Traversable<Formula> {
    /**
     * A tautological formula.
     */
    True TRUE = True.getInstance();

    /**
     * A contradictory formula.
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
     * {@return this formula's variable map, if any}
     * It is guaranteed that all subformulas of a formula share the same {@link TermMap}.
     * That is, {@code getChildren().stream().allMatch(child -> child.getVariableMap() == getVariableMap())} holds.
     * A formula that has no variables (e.g., {@link de.featjar.formula.structure.atomic.literal.True} or
     * {@link de.featjar.formula.structure.atomic.literal.False}) has no {@link TermMap}.
     */
    default Optional<TermMap> getTermMap() {
        return Trees.preOrderStream(this)
                .filter(n -> n instanceof ValueTerm)
                .map(n -> ((ValueTerm) n).getMap())
                .findAny();
    }

    /**
     * {@return the evaluation of this formula on a given list of values}
     *
     * @param values the values
     */
    Object evaluate(List<?> values); // todo: add easy call to a valuevisitor // evaluateNode, evaluateTree?

    @Override
    Formula cloneNode();
}
