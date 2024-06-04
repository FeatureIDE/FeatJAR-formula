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
package de.featjar.formula.io.textual;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.assignment.AValueAssignment;
import de.featjar.formula.assignment.AValueAssignmentList;
import de.featjar.formula.assignment.ValueAssignment;
import de.featjar.formula.assignment.ValueAssignmentList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Textual format for serializing and parsing a list of {@link AValueAssignment value assignments}.
 *
 * @author Elias Kuiter
 * @author Sebastian Krieter
 */
public class ValueAssignmentListFormat implements IFormat<AValueAssignmentList<?>> {

    @Override
    public Result<String> serialize(AValueAssignmentList<?> valueAssignmentList) {
        return Result.of(valueAssignmentList.getAll().stream()
                .map(AValueAssignment::print)
                .collect(Collectors.joining(";")));
    }

    @Override
    public Result<AValueAssignmentList<?>> parse(AInputMapper inputMapper) {
        List<Problem> problems = new ArrayList<>();
        ValueAssignmentFormat format = new ValueAssignmentFormat();
        ValueAssignmentList valueAssignmentList = new ValueAssignmentList();
        for (String valueClause : inputMapper
                .get()
                .getLineStream()
                .collect(Collectors.joining(";"))
                .split(";")) {
            Result<AValueAssignment> valueAssignment = IO.load(valueClause.trim(), format);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent()) {
                valueAssignmentList.add((ValueAssignment) valueAssignment.get());
            }
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
