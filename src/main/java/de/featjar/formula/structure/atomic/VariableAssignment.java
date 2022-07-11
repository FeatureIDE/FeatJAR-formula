/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
 * 
 * This file is part of formula.
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.formula.structure.atomic;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.data.Pair;
import de.featjar.formula.structure.atomic.literal.*;
import de.featjar.formula.structure.atomic.literal.NamedTermMap.*;
import de.featjar.formula.structure.atomic.literal.VariableMap.*;
import de.featjar.util.data.*;

public class VariableAssignment implements Assignment {

	protected final LinkedHashMap<Integer, Object> assignments;
	protected final VariableMap variables;

	public VariableAssignment(VariableMap variables) {
		this.variables = Objects.requireNonNull(variables);
		final int assignmentSize = variables.getVariableCount() + 1;
		assignments = new LinkedHashMap<>(assignmentSize);
	}

	@Override
	public void set(int index, Object assignment) {
		final Variable sig = variables.getVariableSignature(index).orElseThrow(() -> new NoSuchElementException(String
			.valueOf(index)));
		if (assignment == null) {
			assignments.remove(index);
		} else {
			if (assignment.getClass() == sig.getType()) {
				assignments.put(index, assignment);
			} else {
				throw new ClassCastException(String.valueOf(sig.getType()));
			}
		}
	}

	public void set(String name, Object assignment) {
		final Variable sig = variables.getVariable(name).orElseThrow(() -> new NoSuchElementException(name));
		if (assignment == null) {
			assignments.remove(sig.getIndex());
		} else {
			if (assignment.getClass() == sig.getType()) {
				assignments.put(sig.getIndex(), assignment);
			} else {
				throw new ClassCastException(String.valueOf(sig.getType()));
			}
		}
	}

	@Override
	public void unsetAll() {
		assignments.clear();
	}

	@Override
	public Optional<Object> get(int index) {
		return Optional.ofNullable(assignments.get(index));
	}

	public Optional<Object> get(String name) {
		return variables.getVariable(name).map(ValueTerm::getIndex).map(assignments::get);
	}

	public List<Pair<Integer, Object>> getAll() {
		return assignments.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).collect(Collectors
			.toList());
	}

	public VariableMap getVariables() {
		return variables;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<Integer, Object> entry : assignments.entrySet()) {
			sb.append(variables.getVariableName(entry.getKey()));
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

}
