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
package de.featjar.formula.structure.atomic.literal;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.TerminalFormula;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.term.Term;
import java.util.Collections;
import java.util.List;

/**
 * Expresses a tautology.
 * Always evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class True extends TerminalFormula implements Literal {

    private static final True INSTANCE = new True();

    protected True() {
    }

    public static True getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "true";
    }

    @Override
    public Boolean evaluate(List<?> values) {
        return Boolean.TRUE;
    }

    @Override
    public True cloneNode() {
        return this;
    }

    @Override
    public False flip() {
        return Formula.FALSE;
    }
}
