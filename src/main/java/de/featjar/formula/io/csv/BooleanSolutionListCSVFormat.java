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

import de.featjar.base.data.Result;
import de.featjar.base.io.NonEmptyLineIterator;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.ParseProblem;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.ABooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanSolution;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BooleanSolutionListCSVFormat implements IFormat<BooleanAssignmentGroups> {

    private static final String ID_COLUMN = "Configuration";
    private static final String VALUE_SEPARATOR = ";";
    private static final String LINE_SEPARATOR = "\n";
    private static final String POSITIVE_VALUE = "+";
    private static final String NEGATIVE_VALUE = "-";
    private static final String NULL_VALUE = "0";

    public static final String ID = BooleanSolutionListCSVFormat.class.getCanonicalName();

    @Override
    public Result<String> serialize(BooleanAssignmentGroups configurationList) {
        final StringBuilder csv = new StringBuilder();
        csv.append(ID_COLUMN);
        final List<String> names = configurationList.getVariableMap().getVariableNames();
        for (final String name : names) {
            csv.append(VALUE_SEPARATOR);
            csv.append(name);
        }
        csv.append(LINE_SEPARATOR);
        int configurationIndex = 0;
        for (final ABooleanAssignment configuration : configurationList.getFirstGroup()) {
            csv.append(configurationIndex++);
            final int[] literals = configuration.get();
            for (int i = 0; i < literals.length; i++) {
                csv.append(VALUE_SEPARATOR);
                final int l = literals[i];
                csv.append(l == 0 ? NULL_VALUE : l > 0 ? POSITIVE_VALUE : NEGATIVE_VALUE);
            }
            csv.append(LINE_SEPARATOR);
        }
        return Result.of(csv.toString());
    }

    @Override
    public Result<BooleanAssignmentGroups> parse(AInputMapper inputMapper) {
        try {
            final NonEmptyLineIterator lines = inputMapper.get().getNonEmptyLineIterator();
            final String[] headerColumns = lines.get().split(VALUE_SEPARATOR);
            if (headerColumns.length < 1) {
                throw new ParseException("Missing first column " + ID_COLUMN, lines.getLineCount());
            }
            if (!ID_COLUMN.equals(headerColumns[0])) {
                throw new ParseException("First column name must be " + ID_COLUMN, lines.getLineCount());
            }
            final VariableMap variableMap = new VariableMap();
            for (int i = 1; i < headerColumns.length; i++) {
                variableMap.add(headerColumns[i]);
            }
            final List<ABooleanAssignment> group = new ArrayList<>();
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
                final int[] literals = new int[values.length - 1];
                for (int i = 1; i < values.length; i++) {
                    String value = values[i];
                    switch (value) {
                        case POSITIVE_VALUE:
                            literals[i - 1] = i;
                            break;
                        case NEGATIVE_VALUE:
                            literals[i - 1] = -(i);
                            break;
                        case NULL_VALUE:
                            literals[i - 1] = 0;
                            break;
                        default:
                            throw new ParseException(String.format("Unknown value %s", value), lines.getLineCount());
                    }
                }
                group.add(new BooleanSolution(literals, false));
            }
            return Result.of(new BooleanAssignmentGroups(variableMap, List.of(group)));
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
    public BooleanSolutionListCSVFormat getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
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
        return "ConfigurationList";
    }
}
