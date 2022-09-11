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
package de.featjar.formula.structure;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.transform.CNFTransformer;
import de.featjar.formula.structure.transform.DNFTransformer;
import de.featjar.base.data.Store;
import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.task.Monitor;
import java.nio.file.Path;

/**
 * Provides formulas in different forms (as loaded from a file or transformed
 * into CNF/DNF).
 *
 * @author Sebastian Krieter
 */
public interface FormulaComputation {

    static Computation<Formula> empty() {
        return Computation.empty();
    }

    static Computation<Formula> of(Formula formula) {
        return Computation.of(formula);
    }

    static Computation<Formula> of(Path path) {
        return Computation.of(IO.load(path, FormulaFormats.getInstance()));
    }

    class CNF implements Computation<Formula> {
        private final int maximumNumberOfLiterals;

        private CNF() {
            this(Integer.MAX_VALUE);
        }

        private CNF(int maximumNumberOfLiterals) {
            this.maximumNumberOfLiterals = maximumNumberOfLiterals;
        }

        @Override
        public Object getParameters() {
            return maximumNumberOfLiterals;
        }

        @Override
        public Result<Formula> apply(Store c, Monitor m) {
            final CNFTransformer cnfTransformer = new CNFTransformer();
            cnfTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
            return Computation.convert(c, FormulaComputation.identifier, cnfTransformer, m);
        }

        public static CNF fromFormula() {
            return new CNF();
        }

        public static CNF fromFormula(int maximumNumberOfLiterals) {
            return new CNF(maximumNumberOfLiterals);
        }
    }

    @FunctionalInterface
    interface DNF extends FormulaComputation {
        static DNF fromFormula() {
            return (c, m) -> Computation.convert(c, FormulaComputation.identifier, new DNFTransformer(), m);
        }
    }
}
