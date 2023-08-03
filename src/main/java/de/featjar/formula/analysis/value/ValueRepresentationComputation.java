/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.*;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.IVariableMapDependency;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.*;
import java.util.List;

/**
 * ...
 *
 * @author Elias Kuiter
 */
public class ValueRepresentationComputation<T extends IBooleanRepresentation, U extends IValueRepresentation>
        extends AComputation<U> implements IVariableMapDependency {
    protected static final Dependency<?> BOOLEAN_REPRESENTATION = Dependency.newDependency();
    protected static final Dependency<VariableMap> VARIABLE_MAP = Dependency.newDependency(VariableMap.class);

    public ValueRepresentationComputation(
            IComputation<T> booleanRepresentation, IComputation<VariableMap> variableMap) {
        super(booleanRepresentation, variableMap);
    }

    protected ValueRepresentationComputation(ValueRepresentationComputation<T, U> other) {
        super(other);
    }

    @Override
    public Dependency<VariableMap> getVariableMapDependency() {
        return VARIABLE_MAP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<U> compute(List<Object> dependencyList, Progress progress) {
        T t = (T) BOOLEAN_REPRESENTATION.get(dependencyList);
        VariableMap variableMap = VARIABLE_MAP.get(dependencyList);
        return (Result<U>) t.toValue(variableMap);
    }
}
