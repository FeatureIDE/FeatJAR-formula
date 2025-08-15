/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
package de.featjar.formula.io.dimacs;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.ParseProblem;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BooleanAssignmentGroupsDimacsListFormat implements IFormat<BooleanAssignmentGroups> {

    @Override
    public Result<String> serialize(BooleanAssignmentGroups assignmentSpace) {
        Objects.requireNonNull(assignmentSpace);

        final StringBuilder sb = new StringBuilder();
        DimacsSerializer.writeVariables(sb, assignmentSpace.getVariableMap());
        int variableCount = assignmentSpace.getVariableMap().getVariableCount();
        for (BooleanAssignmentList booleanAssignmentList : assignmentSpace) {
            DimacsSerializer.writeProblem(sb, variableCount, booleanAssignmentList.size());
            DimacsSerializer.writeClauses(sb, booleanAssignmentList.getAll(), BooleanAssignment::get);
        }
        return Result.of(sb.toString());
    }

    @Override
    public Result<BooleanAssignmentGroups> parse(AInputMapper inputMapper) {
        final DimacsListParser parser = new DimacsListParser();
        parser.setReadingVariableDirectory(true);
        try {
            Pair<VariableMap, List<List<int[]>>> parsingResult = parser.parseList(inputMapper);
            VariableMap variableMap = parsingResult.getKey();
            BooleanAssignmentGroups booleanAssignmentGroups = new BooleanAssignmentGroups(variableMap);
            for (List<int[]> list : parsingResult.getValue()) {
                booleanAssignmentGroups
                        .getGroups()
                        .add(new BooleanAssignmentList(
                                variableMap,
                                list.stream().map(BooleanAssignment::new).collect(Collectors.toList())));
            }
            return Result.of(booleanAssignmentGroups);
        } catch (final ParseException e) {
            return Result.empty(new ParseProblem(e, e.getErrorOffset()));
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    @Override
    public String getFileExtension() {
        return "dimacslist";
    }

    @Override
    public boolean supportsWrite() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "BooleanAssignmentDimacsList";
    }
}
