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
import de.featjar.util.data.Cache;
import de.featjar.util.data.Identifier;
import de.featjar.util.data.Provider;
import de.featjar.util.data.Result;
import de.featjar.util.task.Monitor;
import java.nio.file.Path;

/**
 * Provides formulas in different forms (as loaded from a file or transformed
 * into CNF/DNF).
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
        return (c, m) -> Provider.load(path, FormulaFormats.getInstance());
    }

    public static class CNF implements FormulaProvider {
        public static final Identifier<Formula> identifier = new Identifier<>();
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
        public Identifier<Formula> getIdentifier() {
            return identifier;
        }

        @Override
        public Result<Formula> apply(Cache c, Monitor m) {
            final CNFTransformer cnfTransformer = new CNFTransformer();
            cnfTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
            return Provider.convert(c, FormulaProvider.identifier, cnfTransformer, m);
        }

        public static CNF fromFormula() {
            return new CNF();
        }

        public static CNF fromFormula(int maximumNumberOfLiterals) {
            return new CNF(maximumNumberOfLiterals);
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
            return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new DNFTransformer(), m);
        }
    }
}
