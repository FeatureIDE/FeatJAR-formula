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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.io.textual;

import java.util.Arrays;

import de.featjar.util.data.Pair;

/**
 * Symbols for a representation like in Java. These are inherently incomplete
 * and should only be used if absolutely necessary.
 * 
 * @author Sebastian Krieter
 */
public class PropositionalModelSymbols extends Symbols {

	public static final Symbols INSTANCE = new PropositionalModelSymbols();

	private PropositionalModelSymbols() {
		super(Arrays.asList(
			new Pair<>(Operator.NOT, "!"),
			new Pair<>(Operator.AND, "&"),
			new Pair<>(Operator.OR, "|"),
			new Pair<>(Operator.EQUALS, "=="),
			new Pair<>(Operator.IMPLIES, "=>")),
			false);
	}

}
