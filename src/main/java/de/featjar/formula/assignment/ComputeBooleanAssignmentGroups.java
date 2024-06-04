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
package de.featjar.formula.assignment;

import de.featjar.base.computation.AComputation;
import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.VariableMap;
import java.util.List;

/**
 * Transforms an indexed CNF representation into an {@link BooleanAssignmentGroups}.
 *
 * @author Sebastian Krieter
 */
public class ComputeBooleanAssignmentGroups<U extends IBooleanRepresentation>
        extends AComputation<BooleanAssignmentGroups> {

    @SuppressWarnings("rawtypes")
    protected static final Dependency<Pair> CNF = Dependency.newDependency(Pair.class);

    public ComputeBooleanAssignmentGroups(IComputation<Pair<U, VariableMap>> valueRepresentation) {
        super(valueRepresentation);
    }

    protected ComputeBooleanAssignmentGroups(ComputeBooleanAssignmentGroups<U> other) {
        super(other);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<BooleanAssignmentGroups> compute(List<Object> dependencyList, Progress progress) {
        final Pair<U, VariableMap> vp = (Pair<U, VariableMap>) CNF.get(dependencyList);
        final U key = vp.getKey();
        if (key instanceof ABooleanAssignment) {
            return Result.of(new BooleanAssignmentGroups(vp.getValue(), List.of(List.of((ABooleanAssignment) key))));
        } else if (key instanceof ABooleanAssignmentList<?>) {
            return Result.of(new BooleanAssignmentGroups(
                    vp.getValue(), List.of(((ABooleanAssignmentList<ABooleanAssignment>) key).getAll())));
        } else {
            return Result.empty();
        }
    }
}
