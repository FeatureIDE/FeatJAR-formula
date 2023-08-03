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

import de.featjar.base.data.Maps;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Problem.Severity;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.analysis.value.AValueAssignment;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Elias Kuiter
 */
public class ValueAssignmentFormat<T extends AValueAssignment> implements IFormat<T> {
    protected final Function<LinkedHashMap<String, Object>, T> constructor;

    public ValueAssignmentFormat(Function<LinkedHashMap<String, Object>, T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Result<String> serialize(T valueAssignment) {
        return Result.of(valueAssignment.getAll().entrySet().stream()
                .map(e -> {
                    String variable = e.getKey();
                    Object value = e.getValue();
                    if (value instanceof Boolean) return String.format("%s%s", ((Boolean) value) ? "" : "-", variable);
                    return String.format("%s = %s", variable, value);
                })
                .collect(Collectors.joining(", ")));
    }

    @Override
    public Result<T> parse(AInputMapper inputMapper) {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        for (String variableValuePair : inputMapper
                .get()
                .getLineStream()
                .collect(Collectors.joining(","))
                .split(",")) {
            String[] parts = variableValuePair.split("=");
            if (parts.length == 0 || parts.length > 2)
                return Result.empty(
                        new Problem("expected variable-value pair, got " + variableValuePair, Severity.ERROR));
            else if (parts.length == 1) {
                String variable = parts[0].trim();
                if (variable.startsWith("-")) variableValuePairs.put(variable.substring(1), false);
                else variableValuePairs.put(variable, true);
            } else variableValuePairs.put(parts[0].trim(), parseValue(parts[1].trim()));
        }
        return Result.of(constructor.apply(variableValuePairs));
    }

    public static Object parseValue(String s) {
        if (s == null || s.equalsIgnoreCase("null")) return null;
        else if (s.equalsIgnoreCase("true")) return true;
        else if (s.equalsIgnoreCase("false")) return false;
        else if (s.toLowerCase().endsWith("f") || s.toLowerCase().endsWith("d")) return Double.valueOf(s);
        else return Long.valueOf(s);
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
        return "ValueAssignment";
    }
}
