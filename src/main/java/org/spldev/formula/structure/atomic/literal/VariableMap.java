/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
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
package org.spldev.formula.structure.atomic.literal;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;
import org.spldev.formula.structure.term.bool.*;
import org.spldev.formula.structure.term.integer.*;
import org.spldev.formula.structure.term.real.*;
import org.spldev.util.logging.*;

/**
 * Variables of a formula.
 *
 * @author Sebastian Krieter
 */
public class VariableMap implements Cloneable, Serializable, Iterable<VariableMap.VariableSignature> {

	public final static class VariableSignature implements Cloneable, Serializable {

		private static final long serialVersionUID = 400642420402382937L;

		private final VariableMap map;
		private final String name;
		private final int index;
		private final Class<? extends Variable<?>> type;

		public VariableSignature(VariableMap map, String name, int index, Class<? extends Variable<?>> type) {
			this.map = map;
			this.name = name;
			this.index = index;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public int getIndex() {
			return index;
		}

		public Class<? extends Variable<?>> getType() {
			return type;
		}

		private VariableSignature rename(String newName) {
			return new VariableSignature(map, newName, index, type);
		}

		private Variable<?> newInstance() {
			try {
				return type.getConstructor(int.class, VariableMap.class).newInstance(index, map);
			} catch (final Exception e) {
				Logger.logError(e);
				return null;
			}
		}

		@Override
		public VariableSignature clone() {
			return new VariableSignature(map, name, index, type);
		}

		@Override
		public String toString() {
			return index + " (" + name + "): " + type.getSimpleName();
		}

		@Override
		public int hashCode() {
			return Objects.hash(index, name, type);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
				return false;
			}
			final VariableSignature other = (VariableSignature) obj;
			return (index == other.index) && (type == other.type) && Objects.equals(name, other.name);
		}

	}

	private static final long serialVersionUID = 4252808504545415221L;

	private final ArrayList<VariableSignature> indexToName;
	private final LinkedHashMap<String, VariableSignature> nameToIndex;

	public static VariableMap fixedSize(int size) {
		final VariableMap variableMap = new VariableMap();
		IntStream.range(1, size + 1).forEach(v -> variableMap.addBooleanVariable(Integer.toString(v)));
		return variableMap;
	}

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
		return expression.getVariableMap();
	}

	public static VariableMap emptyMap() {
		return new VariableMap();
	}

	public static VariableMap withoutIndexes(VariableMap map, Collection<Integer> indexesToRemove) {
		final VariableMap newMap = new VariableMap();
		final Set<Integer> indexSet = (indexesToRemove instanceof Set)
			? (Set<Integer>) indexesToRemove
			: new HashSet<>(indexesToRemove);
		for (VariableSignature sig : map.indexToName) {
			if ((sig != null) && !indexSet.contains(sig.index)) {
				final int newIndex = newMap.indexToName.size();
				sig = new VariableSignature(newMap, sig.name, newIndex, sig.type);
				newMap.indexToName.add(sig);
				newMap.nameToIndex.put(sig.name, sig);
			}
		}
		newMap.indexToName.trimToSize();
		return newMap;
	}

	public static VariableMap withoutNames(VariableMap map, Collection<String> namesToRemove) {
		final VariableMap newMap = new VariableMap();
		final Set<String> nameSet = (namesToRemove instanceof Set)
			? (Set<String>) namesToRemove
			: new HashSet<>(namesToRemove);
		for (VariableSignature sig : map.indexToName) {
			if ((sig != null) && !nameSet.contains(sig.name)) {
				final int newIndex = newMap.indexToName.size();
				sig = new VariableSignature(newMap, sig.name, newIndex, sig.type);
				newMap.indexToName.add(sig);
				newMap.nameToIndex.put(sig.name, sig);
			}
		}
		newMap.indexToName.trimToSize();
		return newMap;
	}

	private VariableMap(Map<Integer, String> nameMap) {
		nameToIndex = new LinkedHashMap<>();
		if (nameMap.isEmpty()) {
			indexToName = new ArrayList<>();
			indexToName.add(null);
		} else {
			final Integer maxIndex = nameMap.keySet().stream().max(Integer::compare).orElseThrow();
			indexToName = new ArrayList<>(maxIndex + 1);
			nameMap.entrySet().forEach(e -> addVariable(e.getValue(), e.getKey(), BoolVariable.class));
		}
	}

	private VariableMap(VariableMap otherMap, boolean normalize) {
		indexToName = new ArrayList<>(otherMap.indexToName.size());
		nameToIndex = new LinkedHashMap<>();
		if (normalize) {
			indexToName.add(null);
			for (VariableSignature sig : otherMap.indexToName) {
				if (sig != null) {
					final int newIndex = indexToName.size();
					sig = new VariableSignature(this, sig.name, newIndex, sig.type);
					indexToName.add(sig);
					nameToIndex.put(sig.name, sig);
				}
			}
			indexToName.trimToSize();
		} else {
			for (VariableSignature sig : otherMap.indexToName) {
				if (sig == null) {
					indexToName.add(null);
				} else {
					sig = sig.clone();
					indexToName.add(sig);
					nameToIndex.put(sig.name, sig);
				}
			}
		}
	}

	private VariableMap() {
		indexToName = new ArrayList<>();
		nameToIndex = new LinkedHashMap<>();
		indexToName.add(null);
	}

	public boolean hasVariable(int index) {
		return isValidIndex(index) && (indexToName.get(index) != null);
	}

	public boolean hasVariable(String name) {
		return nameToIndex.containsKey(name);
	}

	public Optional<String> getName(final int index) {
		return isValidIndex(index)
			? Optional.ofNullable(indexToName.get(index)).map(s -> s.name)
			: Optional.empty();
	}

	public Optional<Integer> getIndex(String name) {
		return Optional.ofNullable(nameToIndex.get(name)).map(s -> s.index);
	}

	public Optional<Variable<?>> getVariable(int index) {
		return isValidIndex(index)
			? Optional.ofNullable(indexToName.get(index)).map(VariableSignature::newInstance)
			: Optional.empty();
	}

	public Optional<Variable<?>> getVariable(String name) {
		return Optional.ofNullable(nameToIndex.get(name)).map(VariableSignature::newInstance);
	}

	private boolean isValidIndex(final int index) {
		return (index >= getMinIndex()) && (index <= getMaxIndex());
	}

	public List<String> getNames() {
		return indexToName.stream().skip(1).map(VariableSignature::getName).collect(Collectors.toList());
	}

	public int size() {
		return indexToName.size() - 1;
	}

	public int getMinIndex() {
		return 1;
	}

	public int getMaxIndex() {
		return indexToName.size() - 1;
	}

	public void renameVariable(int index, String newName) {
		Objects.requireNonNull(newName);
		if (isValidIndex(index)) {
			final VariableSignature oldSig = indexToName.get(index);
			if (oldSig != null) {
				final VariableSignature newSig = oldSig.rename(newName);
				indexToName.set(index, newSig);
				nameToIndex.remove(oldSig.name);
				nameToIndex.put(newName, newSig);
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
		final VariableSignature oldSig = nameToIndex.get(oldName);
		if (oldSig != null) {
			final VariableSignature newSig = oldSig.rename(newName);
			indexToName.set(newSig.index, newSig);
			nameToIndex.remove(oldSig.name);
			nameToIndex.put(newName, newSig);
		} else {
			throw new NoSuchElementException(String.valueOf(oldName));
		}
	}

	private VariableSignature addVariable(final String name, final int index, final Class<? extends Variable<?>> type) {
		for (int i = getMaxIndex(); i < index; i++) {
			indexToName.add(null);
		}
		final VariableSignature sig = new VariableSignature(this, name, index, type);
		nameToIndex.put(name, sig);
		indexToName.set(index, sig);
		return sig;
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
		return (name != null) && !nameToIndex.containsKey(name)
			? Optional.ofNullable((BoolVariable) addVariable(name, getMaxIndex() + 1, BoolVariable.class).newInstance())
			: Optional.empty();
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
		return (name != null) && !nameToIndex.containsKey(name)
			? Optional.ofNullable((IntVariable) addVariable(name, getMaxIndex() + 1, IntVariable.class).newInstance())
			: Optional.empty();
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
		return (name != null) && !nameToIndex.containsKey(name)
			? Optional.ofNullable((RealVariable) addVariable(name, getMaxIndex() + 1, RealVariable.class).newInstance())
			: Optional.empty();
	}

	public boolean removeVariable(String name) {
		final VariableSignature oldSig = nameToIndex.get(name);
		if (oldSig != null) {
			if (oldSig.index == getMaxIndex()) {
				indexToName.remove(oldSig.index);
			} else {
				indexToName.set(oldSig.index, null);
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
				indexToName.remove(index);
			} else {
				indexToName.set(index, null);
			}
			getName(index).ifPresent(nameToIndex::remove);
			return true;
		} else {
			return false;
		}
	}

	public boolean hasGaps() {
		return nameToIndex.size() != indexToName.size();
	}

	public VariableMap normalize() {
		return new VariableMap(this, true);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(indexToName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return Objects.equals(indexToName, ((VariableMap) obj).indexToName);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("VariableMap\n");
		for (int i = 1; i < indexToName.size(); i++) {
			sb.append('\t');
			sb.append(indexToName.get(i));
			sb.append('\n');
		}
		return sb.toString();
	}

	public boolean containsAll(VariableMap variables) {
		return nameToIndex.keySet().containsAll(variables.nameToIndex.keySet());
	}

	@Override
	public VariableMap clone() {
		return new VariableMap(this, false);
	}

	public List<VariableSignature> getSignatures() {
		return indexToName;
	}

	@Override
	public Iterator<VariableSignature> iterator() {
		return indexToName.iterator();
	}

}
