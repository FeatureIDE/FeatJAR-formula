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
package de.featjar.formula.assignment;

import de.featjar.formula.VariableMap;
import de.featjar.formula.structure.predicate.Literal;
import de.featjar.formula.structure.term.value.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Util class for assignments.
 *
 * @author Sebastian Krieter
 */
public final class Assignments {

    private Assignments() {}

    public static List<Variable> variablesFromMap(VariableMap variableMap) {
        return variableMap.getVariableNames().stream().map(Variable::new).collect(Collectors.toList());
    }

    public static List<Variable> variablesFromMap(VariableMap variableMap, BooleanAssignment variables) {
        return IntStream.of(variables.get())
                .mapToObj(l -> new Variable(variableMap.get(Math.abs(l)).orElseThrow()))
                .collect(Collectors.toList());
    }

    public static List<Literal> toLiterals(VariableMap variableMap, BooleanAssignment assignment) {
        List<Literal> list = new ArrayList<>(assignment.size());
        for (int literal : assignment.get()) {
            if (literal != 0) {
                list.add(new Literal(
                        literal > 0, variableMap.get(Math.abs(literal)).get()));
            }
        }
        return list;
    }
}
