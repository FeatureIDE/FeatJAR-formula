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
package de.featjar.formula.io.textual;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.base.io.list.StringListFormat;
import de.featjar.base.log.Log;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentValueMap;
import java.util.*;

public class BooleanAssignmentValueMapParser {

    private static final char VALUE_SEPARATOR = '=';
    private static final char LITERAL_SEPARATOR = ',';

    public Result<BooleanAssignmentValueMap> parse(AInputMapper inputMapper) {
        Map<BooleanAssignment, Integer> assignmentValueMap = new HashMap<>();
        VariableMap variableMap = new VariableMap();
        List<Problem> problems = new ArrayList<>();

        Result<List<String>> stringListResult = new StringListFormat().parse(inputMapper);
        if (stringListResult.isEmpty()) {
            return Result.empty(new Problem("Cannot parse string list"));
        }
        for (String line : stringListResult.get()) {
            try {
                String[] assignmentSplit = line.split(String.valueOf(VALUE_SEPARATOR));
                if (assignmentSplit.length != 2) {
                    return Result.empty();
                }
                String value = assignmentSplit[1];
                String[] literalStrings = assignmentSplit[0].split(String.valueOf(LITERAL_SEPARATOR));

                List<Integer> literals = new ArrayList<>();
                for (String literalString : literalStrings) {
                    literalString = literalString.trim();
                    if (literalString.isEmpty()) {
                        continue;
                    }
                    String absoluteLiteral = literalString;
                    int signum = 1;
                    if (literalString.startsWith("+")) {
                        absoluteLiteral = literalString.substring(1);
                    }
                    if (literalString.startsWith("-")) {
                        absoluteLiteral = literalString.substring(1);
                        signum = -1;
                    }
                    if (variableMap.has(absoluteLiteral)) {
                        literals.add(signum * variableMap.get(absoluteLiteral).orElseThrow());
                    } else {
                        literals.add(signum * variableMap.add(absoluteLiteral));
                    }
                }
                int[] literalsArray =
                        literals.stream().mapToInt(Integer::intValue).toArray();
                BooleanAssignment booleanAssignment = new BooleanAssignment(literalsArray);
                assignmentValueMap.put(booleanAssignment, Integer.parseInt(value));
            } catch (NumberFormatException e) {
                problems.add(new Problem(e, Problem.Severity.WARNING));
            }
        }

        BooleanAssignmentValueMap map = new BooleanAssignmentValueMap(variableMap, assignmentValueMap);
        return Result.of(map, problems);
    }

    public Result<String> serialize(BooleanAssignmentValueMap booleanAssignmentValueMap) {
        VariableMap variableMap = booleanAssignmentValueMap.getVariableMap();
        List<String> stringList = new ArrayList<>();

        for (Map.Entry<BooleanAssignment, Integer> entry : booleanAssignmentValueMap) {
            StringBuilder stringBuilder = new StringBuilder();
            LinkedHashMap<Integer, Boolean> literals = entry.getKey().getAll();
            for (Integer literal : literals.keySet()) {
                String literalString = variableMap.get(literal).orElseLog(Log.Verbosity.WARNING);
                stringBuilder.append(literalString).append(LITERAL_SEPARATOR);
            }
            if (stringBuilder.charAt(stringBuilder.length() - 1) == LITERAL_SEPARATOR) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            stringBuilder.append(VALUE_SEPARATOR).append(entry.getValue());
            stringList.add(stringBuilder.toString());
        }
        return new StringListFormat().serialize(stringList);
    }
}
