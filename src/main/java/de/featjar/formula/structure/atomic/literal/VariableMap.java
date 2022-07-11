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
package de.featjar.formula.structure.atomic.literal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.NamedTermMap.ValueTerm;

/**
 * Variables of a formula.
 *
 * @author Sebastian Krieter
 */
public class VariableMap implements Cloneable {

	public final static class Variable extends ValueTerm {
		// todo inv: all subtrees have the same variablemap

		public Variable(String name, int index, Class<?> type, VariableMap variableMap) {
			super(name, index, type, variableMap);
		}

		@Override
		protected Variable copy(VariableMap newMap) {
			return new Variable(name, index, type, newMap);
		}

		@Override
		public Object eval(List<?> values) {
			assert Formula.checkValues(1, values);
			assert Formula.checkValues(getType(), values);
			return values.get(0);
		}

	}

	public final static class Constant extends ValueTerm {

		private final Object value;

		public Constant(String name, int index, Class<?> type, Object value, VariableMap variableMap) {
			super(name, index, type, variableMap);
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public Constant copy(VariableMap newMap) {
			return new Constant(name, index, type, value, newMap);
		}

		@Override
		public String toString() {
			return index + ": " + name + " (" + value + ")";
		}

		@Override
		public Object eval(List<?> values) {
			assert Formula.checkValues(0, values);
			assert Formula.checkValues(getType(), values);
			return value;
		}

	}

	/**
	 * Merges two variable maps in one new map, useful for composing formulas. Joins
	 * common variables and does not necessarily preserve variable numbering. If one
	 * map is empty, creates a clone of the other. If used for composition, formulas
	 * must be manually adapted to the merged variable map.
	 */

	public static VariableMap merge(Collection<VariableMap> maps) {
		// TODO: could be optimized for feature-model interfaces (return bigger
		// VariableMap), but may have unwanted interactions with mutation
		// TODO: could also be optimized to merge many maps at once (not create a copy
		// for each map)

		return maps.stream().reduce(new VariableMap(), VariableMap::new);
	}

	public static VariableMap merge(VariableMap... maps) {
		return merge(Arrays.asList(maps));
	}

	private final NamedTermMap<Variable> variables;
	private final NamedTermMap<Constant> constants;

	public VariableMap(VariableMap map) {
		variables = new NamedTermMap<>(map.variables, this);
		constants = new NamedTermMap<>(map.constants, this);
	}

	private VariableMap(VariableMap map1, VariableMap map2) {
		variables = new NamedTermMap<>(map1.variables, map2.variables, this);
		constants = new NamedTermMap<>(map1.constants, map2.constants, this);
	}

	public VariableMap(String... variableNames) {
		this(Arrays.asList(variableNames));
	}

	public VariableMap(Collection<String> variableNames) {
		this();
		variableNames.forEach(this::addBooleanVariable);
	}

	public VariableMap(int variableCount) {
		this();
		for (int i = 1; i <= variableCount; i++) {
			addBooleanVariable(Integer.toString(i));
		}
	}

	public VariableMap() {
		variables = new NamedTermMap<>();
		constants = new NamedTermMap<>();
	}

	@Override
	public int hashCode() {
		return Objects.hash(variables, constants);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		return Objects.equals(variables, ((VariableMap) obj).variables) && Objects.equals(constants,
			((VariableMap) obj).constants);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("VariableMap\n");
		for (ValueTerm s : variables) {
			sb.append('\t');
			sb.append(s);
			sb.append('\n');
		}
		for (ValueTerm s : constants) {
			sb.append('\t');
			sb.append(s);
			sb.append('\n');
		}
		return sb.toString();
	}

	@Override
	public VariableMap clone() {
		return new VariableMap(this);
	}

	public Formula get(Formula expression) {
		if (expression instanceof Variable) {
			return getVariable(expression.getName()).orElseThrow(() -> new IllegalArgumentException(
				"Map does not contain variable with name " + expression.getName()));
		} else if (expression instanceof Constant) {
			return getConstant(expression.getName()).orElseThrow(() -> new IllegalArgumentException(
				"Map does not contain constant with name " + expression.getName()));
		}
		return expression;
	}

	public void normalize() {
		variables.normalize();
		constants.normalize();
	}

	public void randomize(Random random) {
		variables.randomize(random);
	}

	public boolean hasVariable(int index) {
		return variables.has(index);
	}

	public boolean hasVariable(String name) {
		return variables.has(name);
	}

	public boolean hasConstant(String name) {
		return constants.has(name);
	}

	public int getVariableCount() {
		return variables.getMaxIndex();
	}

	public int getConstantCount() {
		return constants.getMaxIndex();
	}

	public void renameVariable(int index, String newName) {
		variables.rename(index, newName);
	}

	public void renameVariable(String oldName, String newName) {
		variables.rename(oldName, newName);
	}

	public void add(int size) {
		add(size);
	}

	public void add(Collection<String> names) {
		add(names);
	}

	public Variable addBooleanVariable() {
		return addVariable(null, -1, Boolean.class);
	}

	public Variable addBooleanVariable(String name) {
		return addVariable(name, -1, Boolean.class);
	}

	public Variable addBooleanVariable(int index) {
		return addVariable(null, index, Boolean.class);
	}

	public Variable addBooleanVariable(String name, int index) {
		return addVariable(name, index, Boolean.class);
	}

	public Variable addIntegerVariable(String name) {
		return addVariable(name, -1, Long.class);
	}

	public Variable addIntegerVariable(String name, int index) {
		return addVariable(name, index, Long.class);
	}

	public Variable addRealVariable(String name) {
		return addVariable(name, -1, Double.class);
	}

	public Variable addRealVariable(String name, int index) {
		return addVariable(name, index, Double.class);
	}

	public Variable addVariable(String name, Class<?> type) {
		return addVariable(name, -1, type);
	}

	public Variable addVariable(String name, int index, Class<?> type) {
		return variables.add(new Variable(name, index, type, this));
	}

	public Constant addConstant(String name, Object value, Class<?> type) {
		return addConstant(name, -1, value, type);
	}

	public Constant addConstant(String name, int index, Object value, Class<?> type) {
		return constants.add(new Constant(name, index, type, value, this));
	}

	public Constant addIntegerConstant(String name, long value) {
		return addConstant(name, -1, value, Long.class);
	}

	public Constant addRealConstant(String name, double value) {
		return addConstant(name, -1, value, Double.class);
	}

	public Constant addIntegerConstant(long value) {
		return addConstant(null, -1, value, Long.class);
	}

	public Constant addRealConstant(double value) {
		return addConstant(null, -1, value, Double.class);
	}

	public List<Variable> getVariableSignatures() {
		return Collections.unmodifiableList(variables.getTerms());
	}

	public List<String> getVariableNames() {
		return variables.getTerms().stream().map(ValueTerm::getName).collect(Collectors.toList());
	}

	public List<Constant> getConstantSignatures() {
		return Collections.unmodifiableList(constants.getTerms());
	}

	public List<String> getConstantNames() {
		return constants.getTerms().stream().map(ValueTerm::getName).collect(Collectors.toList());
	}

	public Optional<Constant> getConstantSignature(int index) {
		return constants.get(index);
	}

	public Optional<String> getConstantName(int index) {
		return getConstantSignature(index).map(ValueTerm::getName);
	}

	public Optional<Class<?>> getConstantType(int index) {
		return getConstantSignature(index).map(ValueTerm::getType);
	}

	public Optional<Constant> getConstant(int index) {
		return constants.get(index);
	}

	public Optional<Class<?>> getConstantType(String name) {
		return getConstantSignature(name).map(ValueTerm::getType);
	}

	public Optional<Constant> getConstant(String name) {
		return constants.get(name);
	}

	public Optional<Constant> getConstantSignature(String name) {
		return constants.get(name);
	}

	public Optional<Integer> getConstantIndex(String name) {
		return getConstantSignature(name).map(ValueTerm::getIndex);
	}

	public Optional<Variable> getVariableSignature(int index) {
		return variables.get(index);
	}

	public Optional<String> getVariableName(int index) {
		return getVariableSignature(index).map(ValueTerm::getName);
	}

	public Optional<Class<?>> getVariableType(int index) {
		return getVariableSignature(index).map(ValueTerm::getType);
	}

	public Optional<Variable> getVariable(int index) {
		return variables.get(index);
	}

	public Optional<Variable> getVariableSignature(String name) {
		return variables.get(name);
	}

	public Optional<Integer> getVariableIndex(String name) {
		return getVariableSignature(name).map(ValueTerm::getIndex);
	}

	public Optional<Class<?>> getVariableType(String name) {
		return getVariableSignature(name).map(ValueTerm::getType);
	}

	public Optional<Variable> getVariable(String name) {
		return variables.get(name);
	}

	public Optional<Variable> getVariable(String name, Class<?> type) {
		return variables.get(name) //
			.filter(s -> s.type == type);
	}

	public Optional<Variable> getVariable(int index, Class<?> type) {
		return variables.get(index) //
			.filter(s -> s.type == type);
	}

	public Optional<Constant> getConstant(String name, Class<?> type) {
		return constants.get(name) //
			.filter(s -> s.type == type);
	}

	public Optional<Constant> getConstant(int index, Class<?> type) {
		return constants.get(index) //
			.filter(s -> s.type == type);
	}

	public Optional<Variable> getBooleanVariable(String name) {
		return getVariable(name, Boolean.class);
	}

	public Optional<Variable> getIntegerVariable(String name) {
		return getVariable(name, Long.class);
	}

	public Optional<Variable> getRealVariable(String name) {
		return getVariable(name, Double.class);
	}

	public Optional<Variable> getBooleanVariable(int index) {
		return getVariable(index, Boolean.class);
	}

	public Optional<Variable> getIntegerVariable(int index) {
		return getVariable(index, Long.class);
	}

	public Optional<Variable> getRealVariable(int index) {
		return getVariable(index, Double.class);
	}

	public Optional<Constant> getBooleanConstant(String name) {
		return getConstant(name, Boolean.class);
	}

	public Optional<Constant> getIntegerConstant(String name) {
		return getConstant(name, Long.class);
	}

	public Optional<Constant> getRealConstant(String name) {
		return getConstant(name, Double.class);
	}

	public Optional<Constant> getConstantVariable(int index) {
		return getConstant(index, Boolean.class);
	}

	public Optional<Constant> getIntegerConstant(int index) {
		return getConstant(index, Long.class);
	}

	public Optional<Constant> getRealConstant(int index) {
		return getConstant(index, Double.class);
	}

	public Optional<BooleanLiteral> getLiteral(String name, boolean positive) {
		return getBooleanVariable(name).map(v -> new BooleanLiteral(v, positive));
	}

	public Optional<BooleanLiteral> getLiteral(int index, boolean positive) {
		return getBooleanVariable(index).map(v -> new BooleanLiteral(v, positive));
	}

	public Optional<BooleanLiteral> getLiteral(String name) {
		return getBooleanVariable(name).map(BooleanLiteral::new);
	}

	public Optional<BooleanLiteral> getLiteral(int index) {
		return getBooleanVariable(index).map(BooleanLiteral::new);
	}

	public BooleanLiteral createLiteral(String name) {
		return createLiteral(name, true);
	}

	public BooleanLiteral createLiteral(String name, boolean positive) {
		return new BooleanLiteral(getBooleanVariable(name).orElseGet(() -> addBooleanVariable(name)), positive);
	}

	public BooleanLiteral createLiteral(int index) {
		return createLiteral(index, true);
	}

	public BooleanLiteral createLiteral(int index, boolean positive) {
		return new BooleanLiteral(getBooleanVariable(index).orElseGet(() -> addBooleanVariable(index)), positive);
	}

	public boolean removeVariable(int index) {
		return variables.remove(index);
	}

	public boolean removeConstant(int index) {
		return constants.remove(index);
	}

	public boolean removeVariable(String name) {
		return variables.remove(name);
	}

	public boolean removeConstant(String name) {
		return constants.remove(name);
	}

}
