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
package org.spldev.formula.expression.atomic.literal;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;
import org.spldev.formula.expression.term.integer.*;
import org.spldev.formula.expression.term.real.*;

/**
 * Variables of a formula.
 *
 * @author Sebastian Krieter
 */
public class VariableMap implements Cloneable, Serializable {

	private static final long serialVersionUID = 4436189744440916565L;

	private final ArrayList<String> names;
	private final ArrayList<Variable<?>> variables;
	private final LinkedHashMap<String, Integer> nameToIndex;

	public static VariableMap fromNames(Collection<String> names) {
		Objects.requireNonNull(names);
		final VariableMap variableMap = new VariableMap();
		names.forEach(variableMap::addBooleanVariable);
		return variableMap;
	}

	public static VariableMap fromNameMap(Map<Integer, String> nameMap) {
		return new VariableMap(nameMap);
	}

	public static VariableMap fromExpression(Expression expression) {
		return Formulas.getVariableStream(expression)
			.findAny().map(Variable::getVariableMap).orElseGet(VariableMap::new);
	}

	public static VariableMap emptyMap() {
		return new VariableMap();
	}

	private VariableMap(Map<Integer, String> nameMap) {
		final Integer maxIndex = nameMap.keySet().stream().max(Integer::compare).orElseThrow();

		names = new ArrayList<>(maxIndex + 1);
		variables = new ArrayList<>(maxIndex + 1);
		nameToIndex = new LinkedHashMap<>();
		for (int i = 0; i <= maxIndex; i++) {
			names.add(null);
			variables.add(null);
		}
		for (final Entry<Integer, String> entry : nameMap.entrySet()) {
			final String name = entry.getValue();
			final int index = entry.getKey();
			nameToIndex.put(name, index);
			names.set(index, name);
			variables.set(index, new BoolVariable(index, this));
		}
	}

	private VariableMap() {
		names = new ArrayList<>();
		variables = new ArrayList<>();
		nameToIndex = new LinkedHashMap<>();
		names.add(null);
		variables.add(null);
	}

	public boolean hasVariable(int index) {
		return isValidIndex(index) && (variables.get(index) != null);
	}

	public boolean hasVariable(String name) {
		return nameToIndex.containsKey(name);
	}

	public Optional<String> getName(final int index) {
		return isValidIndex(index)
			? Optional.ofNullable(names.get(index))
			: Optional.empty();
	}

	public Optional<Integer> getIndex(String name) {
		return Optional.ofNullable(nameToIndex.get(name));
	}

	public Optional<Variable<?>> getVariable(int index) {
		return isValidIndex(index)
			? Optional.ofNullable(variables.get(index))
			: Optional.empty();
	}

	public Optional<Variable<?>> getVariable(String name) {
		return Optional.ofNullable(variables.get(nameToIndex.get(name)));
	}

	public List<Variable<?>> getVariables(List<String> names) {
		return names.stream()
			.map(nameToIndex::get)
			.map(variables::get)
			.collect(Collectors.toList());
	}

	private boolean isValidIndex(final int index) {
		return (index > 0) && (index < variables.size());
	}

	public List<String> getNames() {
		return new ArrayList<>(nameToIndex.keySet());
	}

	public int size() {
		return variables.size() - 1;
	}

	public int getMinIndex() {
		return 1;
	}

	public int getMaxIndex() {
		return variables.size() - 1;
	}

	public void renameVariable(int index, String newName) {
		Objects.requireNonNull(newName);
		if (isValidIndex(index)) {
			final String oldName = names.get(index);
			if (oldName != null) {
				names.set(index, newName);
				nameToIndex.remove(oldName);
				nameToIndex.put(newName, index);
			} else {
				throw new NoSuchElementException(String.valueOf(index));
			}
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	public void renameVariable(String oldName, String newName) {
		Objects.requireNonNull(oldName);
		Objects.requireNonNull(newName);
		final Integer index = nameToIndex.get(oldName);
		if (index != null) {
			names.set(index, newName);
			nameToIndex.remove(oldName);
			nameToIndex.put(newName, index);
		} else {
			throw new NoSuchElementException(String.valueOf(oldName));
		}
	}

	/**
	 * Creates a new {@link BoolVariable boolean variable} with the given name or
	 * does nothing if a variable with the name already exists.
	 * 
	 * @param name the name of the variable.
	 * @return An {@link Optional optional} with the new variable or an empty
	 *         optional if a variable with the name already exists.
	 */
	public Optional<BoolVariable> addBooleanVariable(String name) {
		if ((name != null) && !nameToIndex.containsKey(name)) {
			final int newIndex = getMaxIndex() + 1;
			final BoolVariable variable = new BoolVariable(newIndex, this);
			names.add(name);
			variables.add(variable);
			nameToIndex.put(name, newIndex);
			return Optional.of(variable);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Creates a new {@link IntVariable integer variable} with the given name or
	 * does nothing if a variable with the name already exists.
	 * 
	 * @param name the name of the variable.
	 * @return An {@link Optional optional} with the new variable or an empty
	 *         optional if a variable with the name already exists.
	 */
	public Optional<IntVariable> addIntegerVariable(String name) {
		if ((name != null) && !nameToIndex.containsKey(name)) {
			final int newIndex = getMaxIndex() + 1;
			final IntVariable variable = new IntVariable(newIndex, this);
			names.add(name);
			variables.add(variable);
			nameToIndex.put(name, newIndex);
			return Optional.of(variable);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Creates a new {@link RealVariable real variable} with the given name or does
	 * nothing if a variable with the name already exists or the given name is
	 * {@code null}.
	 * 
	 * @param name the name of the variable.
	 * @return An {@link Optional optional} with the new variable or an empty
	 *         optional if a variable with the name already exists or the given name
	 *         is {@code null}.
	 */
	public Optional<RealVariable> addRealVariable(String name) {
		if ((name != null) && !nameToIndex.containsKey(name)) {
			final int newIndex = getMaxIndex() + 1;
			final RealVariable variable = new RealVariable(newIndex, this);
			names.add(name);
			variables.add(variable);
			nameToIndex.put(name, newIndex);
			return Optional.of(variable);
		} else {
			return Optional.empty();
		}
	}

	public boolean removeVariable(String name) {
		final Integer index = nameToIndex.get(name);
		if (index != null) {
			if (index == getMaxIndex()) {
				names.remove((int) index);
				variables.remove((int) index);
			} else {
				names.set(index, null);
				variables.set(index, null);
			}
			nameToIndex.remove(name);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeIndex(int index) {
		if (isValidIndex(index)) {
			if (index == getMaxIndex()) {
				names.remove(index);
				variables.remove(index);
			} else {
				names.set(index, null);
				variables.set(index, null);
			}
			getName(index).ifPresent(nameToIndex::remove);
			return true;
		} else {
			return false;
		}
	}

	public boolean hasGaps() {
		return nameToIndex.size() != names.size();

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(names);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return Objects.equals(names, ((VariableMap) obj).names);
	}

	@Override
	public String toString() {
		return "Variables " + names.subList(1, names.size());
	}

	public boolean containsAll(VariableMap variables) {
		return nameToIndex.keySet().containsAll(variables.nameToIndex.keySet());
	}

}
