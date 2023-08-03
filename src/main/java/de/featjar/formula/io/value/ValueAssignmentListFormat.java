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
package de.featjar.formula.io.value;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.analysis.value.AValueAssignment;
import de.featjar.formula.analysis.value.AValueAssignmentList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Elias Kuiter
 */
public class ValueAssignmentListFormat<T extends AValueAssignmentList<U>, U extends AValueAssignment>
        implements IFormat<T> {
    protected final Supplier<T> listConstructor;
    protected final ValueAssignmentFormat<U> valueAssignmentFormat;
    // todo: serialize as CSV, parse as CSV

    public ValueAssignmentListFormat() {
        this(null, null);
    }

    public ValueAssignmentListFormat(
            Supplier<T> listConstructor, Function<LinkedHashMap<String, Object>, U> constructor) {
        this.listConstructor = listConstructor;
        this.valueAssignmentFormat = new ValueAssignmentFormat<>(constructor);
    }

    @Override
    public Result<String> serialize(T valueAssignmentList) {
        return Result.of(valueAssignmentList.getAll().stream()
                .map(AValueAssignment::print)
                .collect(Collectors.joining("; ")));
    }

    @Override
    public Result<T> parse(AInputMapper inputMapper) {
        if (listConstructor == null || valueAssignmentFormat == null)
            return Result.empty(new Problem("cannot parse unknown value assignment", Problem.Severity.ERROR));
        T valueAssignmentList = listConstructor.get();
        List<Problem> problems = new ArrayList<>();
        for (String valueClause : inputMapper
                .get()
                .getLineStream()
                .collect(Collectors.joining(";"))
                .split(";")) {
            Result<U> valueAssignment = IO.load(valueClause.trim(), valueAssignmentFormat);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent()) valueAssignmentList.add(valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "ValueAssignmentList";
    }
}
