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
package de.featjar.formula.io.csv;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.io.NonEmptyLineIterator;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.ParseProblem;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.base.io.output.AOutputMapper;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.ABooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanSolution;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BooleanAssignmentGroupsCSVFormat implements IFormat<BooleanAssignmentGroups> {
    private static final String ASSIGNMENT_COLUMN_NAME = "ID";
    private static final String GROUP_COLUMN_NAME = "Group";
    private static final String VALUE_SEPARATOR = ";";
    private static final String LINE_SEPARATOR = "\n";
    private static final String POSITIVE_VALUE = "+";
    private static final String NEGATIVE_VALUE = "-";
    private static final String NULL_VALUE = "0";

    @Override
    public void write(BooleanAssignmentGroups assignmentGroups, AOutputMapper outputMapper) throws IOException {
        final VariableMap variableMap = assignmentGroups.getVariableMap();
        final List<Pair<Integer, String>> namePairs = variableMap.stream().collect(Collectors.toList());
        final List<? extends List<? extends ABooleanAssignment>> groups = assignmentGroups.getGroups();

        outputMapper.get().write(serializeHeader(namePairs));

        int groupIndex = 0;
        int assignmentIndex = 0;
        for (List<? extends ABooleanAssignment> group : groups) {
            for (final ABooleanAssignment assignment : group) {
                outputMapper.get().write(serializeAssignment(namePairs, groupIndex, assignmentIndex, assignment));
                assignmentIndex++;
            }
            groupIndex++;
        }
    }

    @Override
    public Result<String> serialize(BooleanAssignmentGroups assignmentGroups) {
        final VariableMap variableMap = assignmentGroups.getVariableMap();
        final List<Pair<Integer, String>> namePairs = variableMap.stream().collect(Collectors.toList());
        final List<? extends List<? extends ABooleanAssignment>> groups = assignmentGroups.getGroups();

        final StringBuilder csv = new StringBuilder();
        csv.append(serializeHeader(namePairs));

        int groupIndex = 0;
        int assignmentIndex = 0;
        for (List<? extends ABooleanAssignment> group : groups) {
            for (final ABooleanAssignment assignment : group) {
                csv.append(serializeAssignment(namePairs, groupIndex, assignmentIndex, assignment));
                assignmentIndex++;
            }
            groupIndex++;
        }

        return Result.of(csv.toString());
    }

    private String serializeHeader(final List<Pair<Integer, String>> namePairs) {
        final StringBuilder header = new StringBuilder();
        header.append(ASSIGNMENT_COLUMN_NAME);
        header.append(VALUE_SEPARATOR);
        header.append(GROUP_COLUMN_NAME);
        for (final Pair<Integer, String> namePair : namePairs) {
            final String name = namePair.getValue();
            if (name != null) {
                header.append(VALUE_SEPARATOR);
                header.append(name);
            }
        }
        header.append(LINE_SEPARATOR);
        return header.toString();
    }

    private String serializeAssignment(
            final List<Pair<Integer, String>> namePairs,
            int groupIndex,
            int assignmentIndex,
            final ABooleanAssignment configuration) {
        final StringBuilder line = new StringBuilder();
        line.append(assignmentIndex);
        line.append(VALUE_SEPARATOR);
        line.append(groupIndex);
        for (final Pair<Integer, String> namePair : namePairs) {
            line.append(VALUE_SEPARATOR);
            final Result<Boolean> value = configuration.getValue(namePair.getKey());
            line.append(value.isPresent() ? ((boolean) value.get() ? POSITIVE_VALUE : NEGATIVE_VALUE) : NULL_VALUE);
        }
        line.append(LINE_SEPARATOR);
        return line.toString();
    }

    @Override
    public Result<BooleanAssignmentGroups> parse(AInputMapper inputMapper) {
        try {
            final NonEmptyLineIterator lines = inputMapper.get().getNonEmptyLineIterator();
            final String[] headerColumns = lines.get().split(VALUE_SEPARATOR);
            if (headerColumns.length < 2) {
                throw new ParseException(
                        "Missing first two columns " + ASSIGNMENT_COLUMN_NAME + " and " + GROUP_COLUMN_NAME,
                        lines.getLineCount());
            }
            if (!ASSIGNMENT_COLUMN_NAME.equals(headerColumns[0])) {
                throw new ParseException("First column name must be " + ASSIGNMENT_COLUMN_NAME, lines.getLineCount());
            }
            if (!GROUP_COLUMN_NAME.equals(headerColumns[1])) {
                throw new ParseException("Second column name must be " + GROUP_COLUMN_NAME, lines.getLineCount());
            }
            final VariableMap variableMap = new VariableMap();
            for (int i = 2; i < headerColumns.length; i++) {
                variableMap.add(headerColumns[i]);
            }
            final ArrayList<List<ABooleanAssignment>> groups = new ArrayList<>();
            for (String line = lines.get(); line != null; line = lines.get()) {
                final String[] values = line.split(VALUE_SEPARATOR);
                if (headerColumns.length != values.length) {
                    throw new ParseException(
                            String.format(
                                    "Number of values (%d) does not match number of columns (%d)",
                                    values.length, headerColumns.length),
                            lines.getLineCount());
                }
                try {
                    Integer.parseInt(values[0]);
                } catch (NumberFormatException e) {
                    throw new ParseException(
                            String.format("First value must be a number, but was %s", values[0]), lines.getLineCount());
                }
                final List<ABooleanAssignment> group;
                try {
                    final int groupIndex = Integer.parseInt(values[1]);
                    for (int i = groups.size() - 1; i < groupIndex; i++) {
                        groups.add(new ArrayList<>());
                    }
                    group = groups.get(groupIndex);
                } catch (NumberFormatException e) {
                    throw new ParseException(
                            String.format("Second value must be a number, but was %s", values[0]),
                            lines.getLineCount());
                }
                final int[] literals = new int[values.length - 2];
                for (int i = 2; i < values.length; i++) {
                    String value = values[i];
                    switch (value) {
                        case POSITIVE_VALUE:
                            literals[i - 2] = i - 1;
                            break;
                        case NEGATIVE_VALUE:
                            literals[i - 2] = -(i - 1);
                            break;
                        case NULL_VALUE:
                            literals[i - 2] = 0;
                            break;
                        default:
                            throw new ParseException(String.format("Unknown value %s", value), lines.getLineCount());
                    }
                }
                group.add(new BooleanSolution(literals, false));
            }
            return Result.of(new BooleanAssignmentGroups(variableMap, groups));
        } catch (final ParseException e) {
            return Result.empty(new ParseProblem(e, e.getErrorOffset()));
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    @Override
    public String getFileExtension() {
        return "csv";
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
        return "BooleanAssignmentCSV";
    }
}
