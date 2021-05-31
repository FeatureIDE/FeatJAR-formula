/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.atomic.literal;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.term.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public abstract class Literal extends Terminal implements Atomic {

	public static final True True = org.spldev.formula.expression.atomic.literal.True.getInstance();

	public static final False False = org.spldev.formula.expression.atomic.literal.False.getInstance();

	@Override
	public abstract Literal flip();

	@Override
	public abstract Literal cloneNode();

	@Override
	public List<? extends Term<?>> getChildren() {
		return Collections.emptyList();
	}

	public boolean isPositive() {
		return true;
	}

}
