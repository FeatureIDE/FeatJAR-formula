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

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

/**
 * Variables of a formula.
 *
 * @author Sebastian Krieter
 */
public class VariableMap implements Cloneable, Serializable {

	private static final long serialVersionUID = 4436189744440916565L;

	private final ArrayList<String> indexToVar;
	private final LinkedHashMap<String, Integer> varToIndex;
	
	public VariableMap() {
		indexToVar = new ArrayList<>();
		varToIndex = new LinkedHashMap<>();
		indexToVar.add(null);
	}
	
	public VariableMap(Collection<String> names) {
		Objects.requireNonNull(names);

		indexToVar = new ArrayList<>(names.size() + 1);
		varToIndex = new LinkedHashMap<>((int) (1.5 * names.size()));

		indexToVar.add(null);
		for (final String name : names) {
			if (name != null) {
				varToIndex.put(name, indexToVar.size());
				indexToVar.add(name);
			}
		}
	}

	public VariableMap(Map<Integer, String> nameMap) {
		Objects.requireNonNull(nameMap);
		final Integer maxIndex = nameMap.keySet().stream().max(Integer::compare).orElseThrow();

		indexToVar = new ArrayList<>(maxIndex + 1);
		varToIndex = new LinkedHashMap<>((int) (1.5 * maxIndex));

		for (int i = 0; i <= maxIndex; i++) {
			indexToVar.add(null);
		}
		nameMap.forEach((key, value) -> indexToVar.set(key, value));

		for (int i = 1; i < indexToVar.size(); i++) {
			String name = indexToVar.get(i);
			if (name == null) {
				name = String.valueOf(i);
				indexToVar.set(i, name);
			}
			varToIndex.put(name, i);
		}
	}

	private VariableMap(ArrayList<String> indexToVar, LinkedHashMap<String, Integer> varToIndex) {
		this.indexToVar = indexToVar;
		this.varToIndex = varToIndex;
	}

	private VariableMap(VariableMap otherVariables) {
		indexToVar = new ArrayList<>(otherVariables.indexToVar);
		varToIndex = new LinkedHashMap<>(otherVariables.varToIndex);
	}

	public boolean hasVariable(int index) {
		return isValidIndex(index) && (indexToVar.get(index) != null);
	}

	public boolean hasVariable(String name) {
		return varToIndex.containsKey(name);
	}

	public Optional<Integer> getVariable(String name) {
		return Optional.ofNullable(varToIndex.get(name));
	}

	public List<Integer> getVariables(List<String> names) {
		return names.stream().map(varToIndex::get).collect(Collectors.toList());
	}

	public Optional<Integer> getIndex(String name) {
		return Optional.ofNullable(varToIndex.get(name));
	}

	public Optional<String> getName(final int index) {
		return isValidIndex(index) ? Optional.ofNullable(indexToVar.get(index)) : Optional.empty();
	}

	private boolean isValidIndex(final int index) {
		return (index > 0) && (index < indexToVar.size());
	}

	public List<String> getNames() {
		return new ArrayList<>(varToIndex.keySet());
	}

	public int size() {
		return indexToVar.size() - 1;
	}

	public int getMinIndex() {
		return 1;
	}

	public int getMaxIndex() {
		return indexToVar.size() - 1;
	}

	public void renameVariable(int index, String newName) {
		Objects.requireNonNull(newName);
		if (isValidIndex(index)) {
			final String oldName = indexToVar.get(index);
			if (oldName != null) {
				indexToVar.set(index, newName);
				varToIndex.remove(oldName);
				varToIndex.put(newName, index);
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
		final Integer index = varToIndex.get(oldName);
		if (index != null) {
			indexToVar.set(index, newName);
			varToIndex.remove(oldName);
			varToIndex.put(newName, index);
		} else {
			throw new NoSuchElementException(String.valueOf(oldName));
		}
	}

	public boolean addVariable(String name) {
		if (name != null && !varToIndex.containsKey(name)) {
			indexToVar.add(name);
			varToIndex.put(name, getMaxIndex());
			return true;
		} else {
			return false;
		}
	}

	public boolean removeVariable(String name) {
		final Integer index = varToIndex.get(name);
		if (index != null) {
			indexToVar.set(index, null);
			varToIndex.remove(name);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeIndex(int index) {
		String name = isValidIndex(index) ? indexToVar.get(index) : null;
		if (name != null) {
			indexToVar.set(index, null);
			varToIndex.remove(name);
			return true;
		} else {
			return false;
		}
	}

	public VariableMap addVariables(Collection<String> names) {
		Objects.requireNonNull(names);
		final ArrayList<String> indexToVar = new ArrayList<>(this.indexToVar);
		final LinkedHashMap<String, Integer> varToIndex = new LinkedHashMap<>(this.varToIndex);
		int newIndex = getMaxIndex() + 1;
		for (final String name : names) {
			if ((name != null) && !varToIndex.containsKey(name)) {
				indexToVar.add(name);
				varToIndex.put(name, newIndex++);
			}
		}
		return new VariableMap(indexToVar, varToIndex);
	}

	public VariableMap removeIndices(int... indices) {
		return removeIndeces(Arrays.stream(indices).boxed().collect(Collectors.toCollection(HashSet::new)));
	}

	public VariableMap removeIndices(Collection<Integer> indices) {
		Objects.requireNonNull(indices);
		return removeIndeces(indices instanceof Set ? (Set<Integer>) indices : new HashSet<>(indices));
	}

	private VariableMap removeIndeces(Set<Integer> indicesSet) {
		final int newSize = indexToVar.size() - indicesSet.size();
		final ArrayList<String> newIndexToVar = new ArrayList<>(newSize);
		final LinkedHashMap<String, Integer> newVarToIndex = new LinkedHashMap<>((int) (1.5 * newSize));
		newIndexToVar.add(null);

		varToIndex.entrySet().stream()
			.filter(e -> !indicesSet.contains(e.getValue()))
			.map(Entry::getKey)
			.forEach(name -> {
				newVarToIndex.put(name, newIndexToVar.size());
				newIndexToVar.add(name);
			});
		return new VariableMap(newIndexToVar, newVarToIndex);
	}

	public VariableMap retainIndices(Collection<Integer> indices) {
		Objects.requireNonNull(indices);

		final ArrayList<String> newIndexToVar = new ArrayList<>(indices.size());
		final LinkedHashMap<String, Integer> newVarToIndex = new LinkedHashMap<>((int) (1.5 * indices.size()));

		indices.stream()
			.filter(Objects::nonNull)
			.filter(this::isValidIndex)
			.map(indexToVar::get)
			.forEach(name -> {
				newIndexToVar.add(name);
				newVarToIndex.put(name, newIndexToVar.size());
			});

		return new VariableMap(newIndexToVar, newVarToIndex);
	}

	public VariableMap retainIndices(int... indices) {
		final ArrayList<String> newIndexToVar = new ArrayList<>(indices.length);
		final LinkedHashMap<String, Integer> newVarToIndex = new LinkedHashMap<>((int) (1.5 * indices.length));

		Arrays.stream(indices)
			.filter(this::isValidIndex)
			.mapToObj(indexToVar::get)
			.forEach(name -> {
				newIndexToVar.add(name);
				newVarToIndex.put(name, newIndexToVar.size());
			});

		return new VariableMap(newIndexToVar, newVarToIndex);
	}

	public VariableMap removeVariables(Collection<String> names) {
		Objects.requireNonNull(names);

		final Set<String> namesSet = names instanceof Set ? (Set<String>) names : new HashSet<>(names);
		final int newSize = indexToVar.size() - namesSet.size();
		final ArrayList<String> newIndexToVar = new ArrayList<>(newSize);
		final LinkedHashMap<String, Integer> newVarToIndex = new LinkedHashMap<>((int) (1.5 * newSize));

		indexToVar.stream()
			.filter(name -> !namesSet.contains(name))
			.forEach(name -> {
				newIndexToVar.add(name);
				newVarToIndex.put(name, newIndexToVar.size());
			});
		return new VariableMap(newIndexToVar, newVarToIndex);
	}

	public VariableMap retainVariables(Collection<String> names) {
		Objects.requireNonNull(names);

		final ArrayList<String> newIndexToVar = new ArrayList<>(names.size());
		final LinkedHashMap<String, Integer> newVarToIndex = new LinkedHashMap<>((int) (1.5 * names.size()));

		names.stream()
			.filter(Objects::nonNull)
			.filter(varToIndex::containsKey)
			.forEach(name -> {
				newIndexToVar.add(name);
				newVarToIndex.put(name, newIndexToVar.size());
			});

		return new VariableMap(newIndexToVar, newVarToIndex);
	}

	public VariableMap normalize() {
		if (varToIndex.size() != indexToVar.size()) {
			final int size = varToIndex.size();
			final List<String> indexToVar = new ArrayList<>(size);
			final Map<String, Integer> varToIndex = new LinkedHashMap<>((int) 1.5 * size);
			int index = 0;
			for (final String name : this.varToIndex.keySet()) {
				indexToVar.add(name);
				varToIndex.put(name, index++);
			}
		}
		return new VariableMap(indexToVar, varToIndex);
	}

	@Override
	public VariableMap clone() {
		return new VariableMap(this);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(indexToVar);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return Objects.equals(indexToVar, ((VariableMap) obj).indexToVar);
	}

	@Override
	public String toString() {
		return "Variables " + indexToVar.subList(1, indexToVar.size());
	}

	public boolean containsAll(VariableMap variables) {
		return varToIndex.keySet().containsAll(variables.varToIndex.keySet());
	}

}
