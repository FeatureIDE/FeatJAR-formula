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
package org.spldev.formula.structure.atomic.literal;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;

/**
 * A special {@link Literal} that holds an unparsable sub expression from a
 * formula.
 *
 * @author Sebastian Krieter
 */
public class ErrorLiteral extends Terminal implements Literal {

	private String error;
	private boolean positive;

	public ErrorLiteral(String error) {
		this(error, true);
	}

	public ErrorLiteral(String error, boolean positive) {
		this.error = error;
		this.positive = positive;
	}

	@Override
	public List<? extends Term<?>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public ErrorLiteral cloneNode() {
		return new ErrorLiteral(error, positive);
	}

	@Override
	public String getName() {
		return "Error: " + error;
	}

	@Override
	public Literal flip() {
		return new ErrorLiteral(error, !positive);
	}

	@Override
	public boolean isPositive() {
		return positive;
	}

}
