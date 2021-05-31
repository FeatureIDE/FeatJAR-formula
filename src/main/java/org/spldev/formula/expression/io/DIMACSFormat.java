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
package org.spldev.formula.expression.io;

import java.io.IOException;
import java.text.ParseException;

import org.spldev.formula.expression.Formula;
import org.spldev.util.Result;
import org.spldev.util.io.format.Format;
import org.spldev.util.io.format.ParseProblem;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 * @author Timo G&uuml;nther
 */
public class DIMACSFormat implements Format<Formula> {

	public static final String ID = DIMACSFormat.class.getCanonicalName();

	@Override
	public String serialize(Formula formula) {
		final DimacsWriter w = new DimacsWriter(formula);
		w.setWritingVariableDirectory(true);
		return w.write();
	}

	@Override
	public Result<Formula> parse(CharSequence source) {
		final DimacsReader r = new DimacsReader();
		r.setReadingVariableDirectory(true);
		try {
			return Result.of(r.read(source.toString()));
		} catch (final ParseException e) {
			return Result.empty(new ParseProblem(e, e.getErrorOffset()));
		} catch (final IOException e) {
			return Result.empty(e);
		}
	}

	@Override
	public DIMACSFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "DIMACS";
	}

	@Override
	public String getFileExtension() {
		return "dimacs";
	}

}
