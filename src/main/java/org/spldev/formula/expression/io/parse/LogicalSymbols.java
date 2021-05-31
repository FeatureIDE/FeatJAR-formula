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
package org.spldev.formula.expression.io.parse;

/**
 * Symbols for a logical representation. These are best used for displaying to
 * the user due to brevity and beauty. Since they consist of unwieldy Unicode
 * characters, do not use them for editing or serialization.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class LogicalSymbols extends Symbols {

	public static final Symbols INSTANCE = new LogicalSymbols();

	private LogicalSymbols() {
		super();
		setSymbol(Operator.NOT, "\u00AC");
		setSymbol(Operator.AND, "\u2227");
		setSymbol(Operator.OR, "\u2228");
		setSymbol(Operator.IMPLIES, "\u21D2");
		setSymbol(Operator.EQUALS, "\u21D4");
		setTextual(false);
	}

}
