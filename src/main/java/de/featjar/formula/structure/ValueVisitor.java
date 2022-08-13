/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
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
 */
package de.featjar.formula.structure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.featjar.formula.structure.atomic.Assignment;
import de.featjar.formula.structure.atomic.literal.VariableMap.Variable;
import de.featjar.util.tree.visitor.TreeVisitor;

public class ValueVisitor implements TreeVisitor<Object, Formula> {

	public enum UnknownVariableHandling {
		ERROR, FALSE, TRUE, NULL
	}

	private final LinkedList<Object> values = new LinkedList<>();

	private UnknownVariableHandling unknownVariableHandling = UnknownVariableHandling.FALSE;

	private final Assignment assignment;
	private Boolean defaultBooleanValue;

	public ValueVisitor(Assignment assignment) {
		this.assignment = assignment;
	}

	public UnknownVariableHandling getUnknown() {
		return unknownVariableHandling;
	}

	public void setUnknown(UnknownVariableHandling unknown) {
		unknownVariableHandling = unknown;
	}

	public Boolean getDefaultBooleanValue() {
		return defaultBooleanValue;
	}

	public void setDefaultBooleanValue(Boolean defaultBooleanValue) {
		this.defaultBooleanValue = defaultBooleanValue;
	}

	@Override
	public void reset() {
		values.clear();
	}

	@Override
	public Optional<Object> getResult() {
		return Optional.ofNullable(values.peek());
	}

	@Override
	public VisitorResult lastVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Variable) {
			final Variable variable = (Variable) node;
			final int index = variable.getIndex();
			if (index <= 0) {
				switch (unknownVariableHandling) {
				case ERROR:
					throw new IllegalArgumentException(variable.getName());
				case NULL:
					values.push(null);
					break;
				case FALSE:
					values.push(Boolean.FALSE);
					break;
				case TRUE:
					values.push(Boolean.TRUE);
					break;
				default:
					throw new IllegalStateException(String.valueOf(unknownVariableHandling));
				}
			} else {
				final Object value = assignment.get(index).orElse(null);
				if (value != null) {
					if (variable.getType().isInstance(value)) {
						values.push(value);
					} else {
						throw new IllegalArgumentException(String.valueOf(value));
					}
				} else {
					if (variable.getType() == Boolean.class) {
						values.push(defaultBooleanValue);
					} else {
						values.push(null);
					}
				}
			}
		} else {
			final List<Object> arguments = values.subList(0, node.getChildren().size());
			Collections.reverse(arguments);
			final Object value = node.eval(arguments);
			arguments.clear();
			values.push(value);
		}
		return VisitorResult.Continue;
	}

}
