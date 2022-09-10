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
package de.featjar.formula;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.FormulaComputation;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.data.Store;
import de.featjar.util.data.Computation;
import de.featjar.util.data.Result;
import de.featjar.util.io.IO;
import de.featjar.util.log.Logger;
import java.nio.file.Path;

/**
 * Representation of a feature model as a formula {@link #formula}, where
 * features are mapped to {@link #variables}. Analysis results are stored in a
 * {@link #store} for later reuse.
 */
public class ModelRepresentation {

    private final Store store = new Store();
    private final Formula formula;
    private final VariableMap variables;

    public static Result<ModelRepresentation> load(final Path modelFile) {
        return IO.load(modelFile, FormulaFormats.getInstance()) //
                .map(ModelRepresentation::new);
    }

    public ModelRepresentation(Formula formula) {
        this.formula = formula;
        this.variables = formula.getVariableMap().orElseThrow();
        store.set(FormulaComputation.of(formula));
    }

    public <T> Result<T> getResult(Computation<T> computation) {
        return store.get(computation, null);
    }

    // todo: also allow to use extensions
    public <T> T get(Computation<T> computation) {
        return store.get(computation).orElse(Logger::logProblems);
    }

    public Store getCache() {
        return store;
    }

    public Formula getFormula() {
        return formula;
    }

    public VariableMap getVariables() {
        return variables;
    }
}
