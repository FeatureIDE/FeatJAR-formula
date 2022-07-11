/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.formula.io.textual;

import java.util.*;

import de.featjar.util.data.Pair;
import de.featjar.util.data.*;

/**
 * Symbols for a short textual representation. Best used for serialization since
 * they fall in the ASCII range but are still relatively short.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class ShortSymbols extends Symbols {

	public static final Symbols INSTANCE = new ShortSymbols();

	private ShortSymbols() {
		super(Arrays.asList(
			new Pair<>(Operator.NOT, "-"),
			new Pair<>(Operator.AND, "&"),
			new Pair<>(Operator.OR, "|"),
			new Pair<>(Operator.IMPLIES, "=>"),
			new Pair<>(Operator.EQUALS, "<=>")),
			false);
	}

}
