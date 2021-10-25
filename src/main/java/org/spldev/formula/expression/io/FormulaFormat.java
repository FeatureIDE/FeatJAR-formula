/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
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
package org.spldev.formula.expression.io;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.io.parse.*;
import org.spldev.util.*;
import org.spldev.util.io.format.*;

public class FormulaFormat implements Format<Formula> {

	public static final String ID = FormulaFormat.class.getCanonicalName();

	@Override
	public Result<Formula> parse(Input source) {
		return new NodeReader().read(source.getCompleteText().get());
	}

	@Override
	public String serialize(Formula object) {
		return new NodeWriter().write(object);
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getFileExtension() {
		return "formula";
	}

	@Override
	public String getName() {
		return "Formula";
	}

}
