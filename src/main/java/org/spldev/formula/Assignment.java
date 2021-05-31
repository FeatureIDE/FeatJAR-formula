/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula;

import java.util.*;

public class Assignment {

	private final List<Object> assignments;
	private final VariableMap variables;

	public static Assignment emptyAssignment(VariableMap variables) {
		return new Assignment(variables);
	}

	public VariableMap getVariables() {
		return variables;
	}

	public Assignment(VariableMap variables) {
		this.variables = Objects.requireNonNull(variables);
		final int assignmentSize = variables.size() + 1;
		assignments = new ArrayList<>(assignmentSize);
		for (int i = 0; i < assignmentSize; i++) {
			assignments.add(null);
		}
	}

	public void set(int index, Object assignment) {
		if (variables.hasVariable(index)) {
			assignments.set(index, assignment);
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	public void set(String name, Object assignment) {
		final int index = variables.getIndex(name).orElseThrow(() -> new NoSuchElementException(name));
		assignments.set(index, assignment);
	}

	public Optional<Object> get(int index) {
		if (variables.hasVariable(index)) {
			return Optional.ofNullable(assignments.get(index));
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	public Optional<Object> get(String name) {
		final int index = variables.getIndex(name).orElseThrow(() -> new NoSuchElementException(name));
		return Optional.ofNullable(assignments.get(index));
	}

}
