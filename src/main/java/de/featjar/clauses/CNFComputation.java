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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.clauses;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.FormulaComputation;
import de.featjar.base.data.Store;
import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;

import java.nio.file.Path;

/**
 * Abstract creator to derive an element from a {@link Store}.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface CNFComputation extends Computation<CNF> {
    static CNFComputation empty() {
        return (c, m) -> Result.empty();
    }

    static CNFComputation of(CNF cnf) {
        return (c, m) -> Result.of(cnf);
    }

    static CNFComputation in(Store store) {
        return (c, m) -> store.get(identifier);
    }

    static CNFComputation loader(Path path) {
        return (c, m) -> IO.load(path, FormulaFormats.getInstance()).map(Clauses::convertToCNF);
    }

    static <T> CNFComputation fromFormula() {
        return (c, m) -> Computation.convert(c, FormulaComputation.CNF.fromFormula(), new FormulaToCNF(), m);
    }

    static <T> CNFComputation fromTseytinFormula() {
        return (c, m) -> Computation.convert(c, FormulaComputation.CNF.fromFormula(0), new FormulaToCNF(), m);
    }
}
