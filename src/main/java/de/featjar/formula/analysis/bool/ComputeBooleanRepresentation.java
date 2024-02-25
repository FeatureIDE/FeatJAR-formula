/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.analysis.bool;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.AComputation;
import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.IValueRepresentation;
import de.featjar.formula.structure.formula.connective.Reference;
import java.util.List;

/**
 * Transforms a formula, which is assumed to be in strict conjunctive normal form, into an indexed CNF representation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class ComputeBooleanRepresentation<T extends IValueRepresentation, U extends IBooleanRepresentation>
        extends AComputation<Pair<U, VariableMap>> {

    protected static final Dependency<Object> VALUE_REPRESENTATION = Dependency.newDependency();

    public ComputeBooleanRepresentation(IComputation<T> valueRepresentation) {
        super(valueRepresentation);
    }

    protected ComputeBooleanRepresentation(ComputeBooleanRepresentation<T, U> other) {
        super(other);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Pair<U, VariableMap>> compute(List<Object> dependencyList, Progress progress) {
        T vp = (T) VALUE_REPRESENTATION.get(dependencyList);
        FeatJAR.log().debug("initializing variable map for " + vp.getClass().getName());
        VariableMap variableMap = VariableMap.of(vp);
        FeatJAR.log().debug(variableMap);
        if (vp instanceof Reference) {
            vp = (T) ((Reference) vp).getExpression();
        }
        return vp.toBoolean(variableMap).map(u -> new Pair<>((U) u, variableMap));
    }
}
