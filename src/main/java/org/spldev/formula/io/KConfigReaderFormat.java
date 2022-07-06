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
package org.spldev.formula.io;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.io.textual.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.data.*;
import org.spldev.util.io.format.*;

public class KConfigReaderFormat implements Format<Formula> {

	public static final String ID = KConfigReaderFormat.class.getCanonicalName();

	@Override
	public Result<Formula> parse(SourceMapper sourceMapper) {
		final ArrayList<Problem> problems = new ArrayList<>();
		final NodeReader nodeReader = new NodeReader();
		nodeReader.setSymbols(PropositionalModelSymbols.INSTANCE);
		return Result.of(new And(sourceMapper.getMainSource().getLines() //
			.map(String::trim) //
			.filter(l -> !l.isEmpty()) //
			.filter(l -> !l.startsWith("#")) //
			// fix non-boolean constraints
			.map(l -> l.replace("=", "_"))
			.map(l -> l.replace(":", "_"))
			.map(l -> l.replace(".", "_"))
			.map(l -> l.replace(",", "_"))
			.map(l -> l.replace("/", "_"))
			.map(l -> l.replace("\\", "_"))
			.map(l -> l.replace(" ", "_"))
			.map(l -> l.replace("-", "_"))
			.map(l -> l.replaceAll("def\\((\\w+)\\)", "$1"))
			.map(nodeReader::read) //
			.peek(r -> problems.addAll(r.getProblems()))
			.filter(Result::isPresent)
			.map(Result::get) //
			.collect(Collectors.toList())), problems);
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return false;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public String getFileExtension() {
		return "model";
	}

	@Override
	public String getName() {
		return "KConfigReader";
	}

}
