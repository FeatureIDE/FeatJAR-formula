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

import de.featjar.base.io.IO;
import de.featjar.formula.VariableMap;
import de.featjar.formula.io.textual.ValueAssignmentListFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A list of value assignments.
 *
 * @param <T> the type of the literal list
 * @author Elias Kuiter
 */
public abstract class AValueAssignmentList<T extends ValueAssignment>
        implements IAssignmentList<T>, IValueRepresentation {
    protected final List<T> literalLists;
    protected final VariableMap variableMap;

    public AValueAssignmentList(VariableMap variableMap) {
        literalLists = new ArrayList<>();
        this.variableMap = variableMap;
    }

    public AValueAssignmentList(VariableMap variableMap, int size) {
        literalLists = new ArrayList<>(size);
        this.variableMap = variableMap;
    }

    public AValueAssignmentList(VariableMap variableMap, Collection<? extends T> literalLists) {
        this.literalLists = new ArrayList<>(literalLists);
        this.variableMap = variableMap;
    }

    public AValueAssignmentList(AValueAssignmentList<T> other) {
        this(other.variableMap, other.getAll());
    }

    @Override
    public List<T> getAll() {
        return literalLists;
    }

    @Override
    public ValueAssignmentList toAssignmentList() {
        return new ValueAssignmentList(
                variableMap,
                literalLists.stream().map(ValueAssignment::toAssignment).collect(Collectors.toList()));
    }

    @Override
    public ValueClauseList toClauseList() {
        return new ValueClauseList(
                variableMap,
                literalLists.stream().map(ValueAssignment::toClause).collect(Collectors.toList()));
    }

    @Override
    public ValueSolutionList toSolutionList() {
        return new ValueSolutionList(
                variableMap,
                literalLists.stream().map(ValueAssignment::toSolution).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AValueAssignmentList<?> that = (AValueAssignmentList<?>) o;
        return Objects.equals(literalLists, that.literalLists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalLists);
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentListFormat());
        } catch (IOException e) {
            return e.toString();
        }
    }

    public VariableMap getVariableMap() {
        return variableMap;
    }
}
