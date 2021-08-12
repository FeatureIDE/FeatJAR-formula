/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.atomic;

import java.util.*;
import java.util.Map.*;

import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.term.*;

public class Assignment {

	private final LinkedHashMap<Variable<?>, Object> assignments;
	private final VariableMap variables;

	public static Assignment emptyAssignment(VariableMap variables) {
		return new Assignment(variables);
	}

	public Assignment(VariableMap variables) {
		this.variables = Objects.requireNonNull(variables);
		final int assignmentSize = variables.size() + 1;
		assignments = new LinkedHashMap<>(assignmentSize);
	}

	public void set(int index, Object assignment) {
		final Variable<?> variable = variables.getVariable(index)
			.orElseThrow(() -> new NoSuchElementException(String.valueOf(index)));
		if (assignment == null) {
			assignments.remove(variable);
		} else {
			if (assignment.getClass() == variable.getType()) {
				assignments.put(variable, assignment);
			} else {
				throw new ClassCastException(String.valueOf(variable.getType()));
			}
		}
	}

	public void set(String name, Object assignment) {
		final Variable<?> variable = variables.getVariable(name)
			.orElseThrow(() -> new NoSuchElementException(name));
		if (assignment == null) {
			assignments.remove(variable);
		} else {
			if (assignment.getClass() == variable.getType()) {
				assignments.put(variable, assignment);
			} else {
				throw new ClassCastException(String.valueOf(variable.getType()));
			}
		}
	}

	public Optional<Object> get(int index) {
		final Variable<?> variable = variables.getVariable(index)
			.orElseThrow(() -> new NoSuchElementException(String.valueOf(index)));
		return Optional.ofNullable(assignments.get(variable));
	}

	public Optional<Object> get(String name) {
		final Variable<?> variable = variables.getVariable(name)
			.orElseThrow(() -> new NoSuchElementException(name));
		return Optional.ofNullable(assignments.get(variable));
	}

	public Set<Entry<Variable<?>, Object>> getAll() {
		return assignments.entrySet();
	}

	public VariableMap getVariables() {
		return variables;
	}

}
