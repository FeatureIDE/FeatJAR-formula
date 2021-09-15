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
package org.spldev.formula.expression;

import org.spldev.formula.expression.io.FormulaFormatManager;
import org.spldev.formula.expression.transform.CNFDistributiveLawTransformer;
import org.spldev.formula.expression.transform.CNFTseytinTransformer;
import org.spldev.formula.expression.transform.DNFDistributiveLawTransformer;
import org.spldev.util.*;
import org.spldev.util.data.*;

import java.nio.file.Path;

/**
 * Provides formulas in different forms (as loaded from a file or transformed into CNF/DNF).
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface FormulaProvider extends Provider<Formula> {

	Identifier<Formula> identifier = new Identifier<>();

	@Override
	default Identifier<Formula> getIdentifier() {
		return identifier;
	}

	static FormulaProvider empty() {
		return (c, m) -> Result.empty();
	}

	static FormulaProvider of(Formula formula) {
		return (c, m) -> Result.of(formula);
	}

	static FormulaProvider in(Cache cache) {
		return (c, m) -> cache.get(identifier);
	}

	static FormulaProvider loader(Path path) {
		return (c, m) -> Provider.load(path, FormulaFormatManager.getInstance());
	}

	@FunctionalInterface
	interface CNF extends FormulaProvider {
		Identifier<Formula> identifier = new Identifier<>();

		@Override
		default Identifier<Formula> getIdentifier() {
			return identifier;
		}

		static CNF fromFormula() {
			return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new CNFDistributiveLawTransformer(), m);
		}
	}

	@FunctionalInterface
	interface DNF extends FormulaProvider {
		Identifier<Formula> identifier = new Identifier<>();

		@Override
		default Identifier<Formula> getIdentifier() {
			return identifier;
		}

		static DNF fromFormula() {
			return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new DNFDistributiveLawTransformer(), m);
		}
	}

	@FunctionalInterface
	interface TseytinCNF extends FormulaProvider {
		Identifier<Formula> identifier = new Identifier<>();

		@Override
		default Identifier<Formula> getIdentifier() {
			return identifier;
		}

		static TseytinCNF fromFormula(int threshold) {
			return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new CNFTseytinTransformer(threshold), m);
		}

		static TseytinCNF fromFormula() {
			return fromFormula(0);
		}
	}
}
