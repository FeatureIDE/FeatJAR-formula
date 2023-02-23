/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.term.Term;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Sebastian Krieter
 */
public abstract class ComparingPredicate extends Predicate {

    public ComparingPredicate(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected ComparingPredicate() {
        super();
    }

    public void setArguments(Term leftArgument, Term rightArgument) {
        setChildren(Arrays.asList(leftArgument, rightArgument));
    }

    @Override
    public void setChildren(List<? extends Formula> children) {
        if (children.size() != 2) {
            throw new IllegalArgumentException("Must specify exactly two children");
        }
        final Iterator<? extends Formula> iterator = children.iterator();
        final Class<?> type1 = iterator.next().getType();
        final Class<?> type2 = iterator.next().getType();
        if (type1 != type2) {
            throw new IllegalArgumentException("Type of children differs: " + type1 + " != " + type2);
        }
        super.setChildren(children);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Boolean eval(List<?> values) {
        assert Formula.checkValues(2, values);
        assert Formula.checkValues(Comparable.class, values);
        final Comparable v1 = (Comparable) values.get(0);
        final Comparable v2 = (Comparable) values.get(1);
        return (v1 != null && v2 != null) ? compareDiff(v1.compareTo(v2)) : null;
    }

    protected abstract boolean compareDiff(int diff);
}
