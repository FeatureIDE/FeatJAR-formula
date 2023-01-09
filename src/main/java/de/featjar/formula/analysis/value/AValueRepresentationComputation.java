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
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.base.computation.Progress;
import de.featjar.formula.analysis.IVariableMapDependency;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.*;

/**
 * ...
 *
 * @author Elias Kuiter
 */
public abstract class AValueRepresentationComputation<T extends IBooleanRepresentation, U extends IValueRepresentation>
        extends AComputation<U> implements IAnalysis<T, U>, IVariableMapDependency {
    protected final static Dependency<?> BOOLEAN_REPRESENTATION = newRequiredDependency();
    protected final static Dependency<VariableMap> VARIABLE_MAP = newRequiredDependency();

    public AValueRepresentationComputation(IComputation<T> booleanRepresentation, IComputation<VariableMap> variableMap) {
        dependOn(BOOLEAN_REPRESENTATION, VARIABLE_MAP);
        setInput(booleanRepresentation);
        setVariableMap(variableMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Dependency<T> getInputDependency() {
        return (Dependency<T>) BOOLEAN_REPRESENTATION;
    }

    @Override
    public Dependency<VariableMap> getVariableMapDependency() {
        return VARIABLE_MAP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<U> compute(DependencyList dependencyList, Progress progress) {
        T t = (T) dependencyList.get(BOOLEAN_REPRESENTATION);
        VariableMap variableMap = dependencyList.get(VARIABLE_MAP);
        return (Result<U>) t.toValue(variableMap);
    }
}
