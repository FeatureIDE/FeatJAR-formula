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
package de.featjar.formula.structure.atomic.predicate;

import de.featjar.formula.structure.BinaryFormula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.term.Term;

import java.util.List;

/**
 * A comparing predicate formula.
 *
 * @author Sebastian Krieter
 */
public abstract class ComparingPredicate extends Predicate implements BinaryFormula {
    protected ComparingPredicate(Term... terms) {
        super(terms);
    }

    protected ComparingPredicate(List<? extends Term> terms) {
        super(terms);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Boolean evaluate(List<?> values) {
        Formulas.assertInstanceOf(Comparable.class, values);
        final Comparable v1 = (Comparable) values.get(0);
        final Comparable v2 = (Comparable) values.get(1);
        return (v1 != null && v2 != null) ? compareDifference(v1.compareTo(v2)) : null;
    }

    protected abstract boolean compareDifference(int diff);
}
