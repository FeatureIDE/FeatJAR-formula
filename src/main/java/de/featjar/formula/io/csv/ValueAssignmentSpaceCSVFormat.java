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
package de.featjar.formula.io.csv;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.AValueAssignment;
import de.featjar.formula.analysis.value.ValueAssignmentSpace;
import java.util.List;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class ValueAssignmentSpaceCSVFormat implements IFormat<ValueAssignmentSpace> {
    private static final String ASSIGNMENT_COLUMN_NAME = "ID";
    private static final String GROUP_COLUMN_NAME = "Group";
    private static final String VALUE_SEPARATOR = ";";
    private static final String LINE_SEPARATOR = "\n";

    @Override
    public Result<String> serialize(ValueAssignmentSpace assignmentSpace) {
        final StringBuilder csv = new StringBuilder();
        csv.append(ASSIGNMENT_COLUMN_NAME);
        csv.append(VALUE_SEPARATOR);
        csv.append(GROUP_COLUMN_NAME);
        final VariableMap variableMap = assignmentSpace.getVariableMap();
        final List<String> names = variableMap.getVariableNames();
        for (final String name : names) {
            csv.append(VALUE_SEPARATOR);
            csv.append(name);
        }
        csv.append(LINE_SEPARATOR);
        int groupIndex = 0;
        int configurationIndex = 0;
        final List<List<AValueAssignment>> groups = assignmentSpace.getGroups();
        for (List<AValueAssignment> group : groups) {
            for (final AValueAssignment configuration : group) {
                csv.append(configurationIndex++);
                csv.append(VALUE_SEPARATOR);
                csv.append(groupIndex);
                for (final String name : names) {
                    csv.append(VALUE_SEPARATOR);
                    configuration.getValue(name).ifPresent(csv::append);
                }
                csv.append(LINE_SEPARATOR);
            }
            groupIndex++;
        }
        return Result.of(csv.toString());
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
    public String getName() {
        return "ValueAssignmentCSV";
    }
}
