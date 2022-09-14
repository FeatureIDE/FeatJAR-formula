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
package de.featjar.formula.structure.formula.literal;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.TerminalFormula;
import de.featjar.formula.structure.formula.PolarPredicate;

import java.util.List;

/**
 * Expresses a contradiction.
 * Always evaluates to {@code false}.
 *
 * @author Sebastian Krieter
 */
public class False extends TerminalFormula implements PolarPredicate {

    private static final False INSTANCE = new False();

    protected False() {
    }

    public static False getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "false";
    }

    @Override
    public Boolean evaluate(List<?> values) {
        return Boolean.FALSE;
    }

    @Override
    public False cloneNode() {
        return this;
    }

    @Override
    public True invert() {
        return Formula.TRUE;
    }

    @Override
    public boolean isPositive() {
        return false;
    }
}