/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.analysis.value;

import de.featjar.base.data.Sets;
import de.featjar.base.io.IO;
import de.featjar.formula.analysis.IAssignmentList;
import de.featjar.formula.io.value.ValueAssignmentListFormat;
import java.io.IOException;
import java.util.*;

/**
 * A list of value assignments.
 *
 * @param <U> the type of the implementing subclass
 * @param <T> the type of the literal list
 * @author Elias Kuiter
 */
public abstract class AValueAssignmentList<T extends AValueAssignmentList<?, U>, U extends ValueAssignment>
        implements IAssignmentList<U>, IValueRepresentation {
    protected final List<U> literalLists;

    public AValueAssignmentList() {
        literalLists = new ArrayList<>();
    }

    public AValueAssignmentList(int size) {
        literalLists = new ArrayList<>(size);
    }

    public AValueAssignmentList(AValueAssignmentList<T, U> other) {
        this(other.literalLists);
    }

    public AValueAssignmentList(Collection<? extends U> literalLists) {
        this.literalLists = new ArrayList<>(literalLists);
    }

    protected abstract T newAssignmentList(List<U> literalLists);

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public T clone() {
        return newAssignmentList(literalLists);
    }

    @Override
    public List<U> getAll() {
        return literalLists;
    }

    @Override
    public LinkedHashSet<String> getVariableNames() {
        return literalLists.stream()
                .map(ValueAssignment::getVariableNames)
                .flatMap(LinkedHashSet::stream)
                .collect(Sets.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AValueAssignmentList<?, ?> that = (AValueAssignmentList<?, ?>) o;
        return Objects.equals(literalLists, that.literalLists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalLists);
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentListFormat<>());
        } catch (IOException e) {
            return e.toString();
        }
    }
}
