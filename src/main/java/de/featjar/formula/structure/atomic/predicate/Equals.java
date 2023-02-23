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

import de.featjar.formula.structure.term.Term;
import java.util.List;

/**
 *
 * @author Sebastian Krieter
 */
public class Equals extends ComparingPredicate {

    public Equals(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected Equals() {
        super();
    }

    @Override
    public String getName() {
        return "=";
    }

    @Override
    public Equals cloneNode() {
        return new Equals();
    }

    @Override
    public NotEquals flip() {
        final List<? extends Term> children = getChildren();
        return new NotEquals(children.get(0), children.get(1));
    }

    @Override
    protected boolean compareDiff(int diff) {
        return diff == 0;
    }
}
