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
package de.featjar.clauses;

import java.nio.file.Path;

import de.featjar.formula.io.FormulaFormatManager;
import de.featjar.formula.structure.FormulaProvider;
import de.featjar.util.data.Cache;
import de.featjar.util.data.Identifier;
import de.featjar.util.data.Provider;
import de.featjar.util.data.Result;

/**
 * Abstract creator to derive an element from a {@link Cache}.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface CNFProvider extends Provider<CNF> {

	Identifier<CNF> identifier = new Identifier<>();

	@Override
	default Identifier<CNF> getIdentifier() {
		return identifier;
	}

	static CNFProvider empty() {
		return (c, m) -> Result.empty();
	}

	static CNFProvider of(CNF cnf) {
		return (c, m) -> Result.of(cnf);
	}

	static CNFProvider in(Cache cache) {
		return (c, m) -> cache.get(identifier);
	}

	static CNFProvider loader(Path path) {
		return (c, m) -> Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF);
	}

	static <T> CNFProvider fromFormula() {
		return (c, m) -> Provider.convert(c, FormulaProvider.CNF.fromFormula(), new FormulaToCNF(), m);
	}

	static <T> CNFProvider fromTseytinFormula() {
		return (c, m) -> Provider.convert(c, FormulaProvider.CNF.fromFormula(0), new FormulaToCNF(), m);
	}

}
