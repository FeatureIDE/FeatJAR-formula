package org.spldev.formula.assignment;

import java.util.*;

/**
 * Variables of a formula.
 *
 * @author Sebastian Krieter
 */
public class VariableMap implements Cloneable {

	private final ArrayList<String> indexToVar;
	private final LinkedHashMap<String, Integer> varToIndex;

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

	public Optional<Integer> getIndex(String name) {
		return Optional.ofNullable(varToIndex.get(name));
	}

	public Optional<String> getName(final int index) {
		return isValidIndex(index) ? Optional.ofNullable(indexToVar.get(index)) : Optional.empty();
	}

	private boolean isValidIndex(final int index) {
		return (index >= 0) && (index < indexToVar.size());
	}

	public List<String> getNames() {
		return new ArrayList<>(varToIndex.keySet());
	}

	public int size() {
		return varToIndex.size();
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

	public VariableMap removeAll(Collection<Integer> indices) {
		Objects.requireNonNull(indices);
		final ArrayList<String> indexToVar = new ArrayList<>(this.indexToVar);
		final LinkedHashMap<String, Integer> varToIndex = new LinkedHashMap<>(this.varToIndex);
		for (final Integer index : indices) {
			if (index != null) {
				if (isValidIndex(index)) {
					final String name = indexToVar.get(index);
					if (name != null) {
						indexToVar.set(index, null);
						varToIndex.remove(name);
					}
				} else {
					throw new NoSuchElementException(String.valueOf(index));
				}
			}
		}
		return new VariableMap(indexToVar, varToIndex);
	}

	public VariableMap removeVariables(Collection<String> names) {
		Objects.requireNonNull(names);
		final ArrayList<String> indexToVar = new ArrayList<>(this.indexToVar);
		final LinkedHashMap<String, Integer> varToIndex = new LinkedHashMap<>(this.varToIndex);
		for (final String name : names) {
			if (name != null) {
				final Integer index = varToIndex.get(name);
				if (index != null) {
					indexToVar.set(index, null);
					varToIndex.remove(name);
				} else {
					throw new NoSuchElementException(name);
				}
			}
		}
		return new VariableMap(indexToVar, varToIndex);
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
		return "Variables [" + indexToVar + "]";
	}

}
