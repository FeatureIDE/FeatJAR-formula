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
package de.featjar.formula.analysis.bool;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.*;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.task.IMonitor;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.*;

import java.util.List;

/**
 * Transforms a formula, which is assumed to be in conjunctive normal form, into an indexed CNF representation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class AComputeBooleanRepresentation<T extends IValueRepresentation, U extends IBooleanRepresentation>
        extends AComputation<Pair<U, VariableMap>> implements IAnalysis<T, Pair<U, VariableMap>> {
    protected final static Dependency<?> VALUE_REPRESENTATION = newRequiredDependency();

    public AComputeBooleanRepresentation(IComputation<T> valueRepresentation) {
        dependOn(VALUE_REPRESENTATION);
        setInput(valueRepresentation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Dependency<T> getInputDependency() {
        return (Dependency<T>) VALUE_REPRESENTATION;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Pair<U, VariableMap>> computeResult(List<?> results, IMonitor monitor) {
        T t = (T) VALUE_REPRESENTATION.get(results);
        FeatJAR.log().debug("initializing variable map for " + t.getClass().getName());
        VariableMap variableMap = VariableMap.of(t);
        FeatJAR.log().debug(variableMap);
        return t.toBoolean(variableMap).map(u -> new Pair<>((U) u, variableMap));
    }
}
