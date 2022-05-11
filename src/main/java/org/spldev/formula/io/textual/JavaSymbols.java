/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.io.textual;

import java.util.*;

import org.spldev.util.data.*;

/**
 * Symbols for a representation like in Java. These are inherently incomplete
 * and should only be used if absolutely necessary.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class JavaSymbols extends Symbols {

	public static final Symbols INSTANCE = new JavaSymbols();

	private JavaSymbols() {
		super(Arrays.asList(
			new Pair<>(Operator.NOT, "!"),
			new Pair<>(Operator.AND, "&&"),
			new Pair<>(Operator.OR, "||"),
			new Pair<>(Operator.EQUALS, "==")),
			false);
	}

}
