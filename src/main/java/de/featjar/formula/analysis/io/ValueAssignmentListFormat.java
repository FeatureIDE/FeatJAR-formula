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
package de.featjar.formula.analysis.io;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.InputMapper;
import de.featjar.base.io.format.Format;
import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.analysis.value.ValueAssignmentList;
import de.featjar.formula.analysis.value.ValueClause;
import de.featjar.formula.analysis.value.ValueClauseList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Elias Kuiter
 */
abstract public class ValueAssignmentListFormat<T extends ValueAssignmentList<?, U>, U extends ValueAssignment> implements Format<T> {
    protected final Supplier<T> listConstructor;
    protected final Function<Map<String, Object>, ValueAssignment> constructor;
    protected final ValueAssignmentFormat valueAssignmentFormat;

    public ValueAssignmentListFormat(Supplier<T> listConstructor, Function<Map<String, Object>, ValueAssignment> constructor) {
        this.listConstructor = listConstructor;
        this.constructor = constructor;
        this.valueAssignmentFormat = new ValueAssignmentFormat(constructor);
    }

    // todo: serialize

    @SuppressWarnings("unchecked")
    @Override
    public Result<T> parse(InputMapper inputMapper) {
        T valueAssignmentList = listConstructor.get();
        List<Problem> problems = new ArrayList<>();
        for (String valueClause : inputMapper.get().getLineStream()
                .collect(Collectors.joining(";"))
                .split(";")) {
            Result<ValueAssignment> valueAssignment = IO.load(valueClause.trim(), valueAssignmentFormat);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent())
                valueAssignmentList.add((U) valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
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
