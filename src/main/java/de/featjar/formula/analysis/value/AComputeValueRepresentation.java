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
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.*;

/**
 * ...
 *
 * @author Elias Kuiter
 */
public abstract class AComputeValueRepresentation<T extends IBooleanRepresentation, U extends IValueRepresentation>
        extends AComputation<U> implements IAnalysis<Pair<T, VariableMap>, U> {
    protected final static Dependency<?> VALUE_REPRESENTATION = newDependency();

    public AComputeValueRepresentation(IComputation<Pair<T, VariableMap>> valueRepresentation) {
        dependOn(VALUE_REPRESENTATION);
        setInput(valueRepresentation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Dependency<Pair<T, VariableMap>> getInputDependency() {
        return (Dependency<Pair<T, VariableMap>>) VALUE_REPRESENTATION;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FutureResult<U> compute() {
        return getInput().get().thenComputeResult(((pair, monitor) -> {
                    T t = pair.getKey();
                    VariableMap variableMap = pair.getValue();
                    return (Result<U>) t.toValue(variableMap);
                }));
    }

}
