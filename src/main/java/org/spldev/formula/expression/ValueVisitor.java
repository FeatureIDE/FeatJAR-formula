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
package org.spldev.formula.expression;

import java.util.*;

import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.atomic.predicate.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.*;
import org.spldev.formula.expression.term.bool.*;
import org.spldev.util.tree.visitor.*;

public class ValueVisitor implements TreeVisitor<Object, Expression> {

	public enum UnkownVariableHandling {
		ERROR, FALSE, TRUE,
	}

	private final LinkedList<Object> values = new LinkedList<>();

	private UnkownVariableHandling unkownVariableHandling = UnkownVariableHandling.FALSE;

	private final Assignment assignment;
	private Boolean defaultBooleanValue;

	public ValueVisitor(Assignment assignment) {
		this.assignment = assignment;
	}

	public UnkownVariableHandling getUnkown() {
		return unkownVariableHandling;
	}

	public void setUnkown(UnkownVariableHandling unkown) {
		unkownVariableHandling = unkown;
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
	public Object getResult() {
		return values.pop();
	}

	@Override
	public VisitorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			if (node == Literal.True) {
				values.push(Boolean.TRUE);
			} else if (node == Literal.False) {
				values.push(Boolean.FALSE);
			} else if (node instanceof Predicate) {
				@SuppressWarnings("unchecked")
				final Predicate<Object> predicate = (Predicate<Object>) node;
				final List<Object> arguments = values.subList(0, predicate.getChildren().size());
				Collections.reverse(arguments);
				final Boolean value = predicate.eval(arguments).orElse(null);
				arguments.clear();
				values.push(value);
			} else if (node instanceof ErrorLiteral) {
				final Literal literal = (ErrorLiteral) node;
				final boolean positive = literal.isPositive();
				switch (unkownVariableHandling) {
				case ERROR:
					throw new NullPointerException(literal.getName());
				case FALSE:
					values.push(positive ? Boolean.FALSE : Boolean.TRUE);
					break;
				case TRUE:
					values.push(positive ? Boolean.TRUE : Boolean.FALSE);
					break;
				default:
					throw new IllegalStateException(String.valueOf(unkownVariableHandling));
				}
			} else {
				throw new IllegalStateException(String.valueOf(node));
			}
		} else if (node instanceof Compound) {
			final int size = node.getChildren().size();
			if (node instanceof And) {
				final List<Object> arguments = values.subList(0, size);
				Object result = null;
				if (arguments.stream().anyMatch(value -> value == Boolean.FALSE)) {
					result = Boolean.FALSE;
				} else if (arguments.stream().allMatch(Objects::nonNull)) {
					result = Boolean.TRUE;
				}
				arguments.clear();
				values.push(result);
			} else if (node instanceof Or) {
				final List<Object> arguments = values.subList(0, size);
				Object result = null;
				if (arguments.stream().anyMatch(value -> value == Boolean.TRUE)) {
					result = Boolean.TRUE;
				} else if (arguments.stream().allMatch(Objects::nonNull)) {
					result = Boolean.FALSE;
				}
				arguments.clear();
				values.push(result);
			} else if (node instanceof Not) {
				final Boolean value = (Boolean) values.pop();
				if (value == null) {
					values.push(null);
				} else {
					values.push(!value);
				}
			} else if (node instanceof Implies) {
				if (size != 2) {
					for (int i = 0; i < size; i++) {
						values.pop();
					}
					values.push(Boolean.FALSE);
				} else {
					final Boolean rightChild = (Boolean) values.pop();
					final Boolean leftChild = (Boolean) values.pop();
					if ((rightChild == Boolean.TRUE) || (leftChild == Boolean.FALSE)) {
						values.push(Boolean.TRUE);
					} else {
						if ((leftChild == null) || (rightChild == null)) {
							values.push(null);
						} else {
							values.push(Boolean.FALSE);
						}
					}
				}
			} else if (node instanceof Biimplies) {
				if (size != 2) {
					for (int i = 0; i < size; i++) {
						values.pop();
					}
					values.push(Boolean.FALSE);
				} else {
					final Boolean rightChild = (Boolean) values.pop();
					final Boolean leftChild = (Boolean) values.pop();
					if ((leftChild == null) || (rightChild == null)) {
						values.push(null);
					} else {
						values.push(leftChild == rightChild);
					}
				}
			} else if (node instanceof Cardinal) {
				final List<Object> arguments = values.subList(0, size);
				Object result = null;
				final long nullCount = arguments.stream().filter(Objects::isNull).count();
				final long trueCount = arguments.stream().filter(value -> value == Boolean.TRUE).count();
				final Cardinal cardinal = (Cardinal) node;
				if ((trueCount >= cardinal.getMin()) && ((trueCount + nullCount) <= cardinal.getMax())) {
					result = Boolean.TRUE;
				} else if (((trueCount + nullCount) < cardinal.getMin()) || (trueCount > cardinal.getMax())) {
					result = Boolean.FALSE;
				}
				arguments.clear();
				values.push(result);
			} else {
				throw new IllegalStateException(String.valueOf(node));
			}
		} else if (node instanceof Variable) {
			final Variable<?> variable = (Variable<?>) node;
			final int index = variable.getIndex();
			if (index == 0) {
				switch (unkownVariableHandling) {
				case ERROR:
					throw new IllegalArgumentException(variable.getName());
				case FALSE:
				case TRUE:
					values.push(defaultBooleanValue);
					break;
				default:
					throw new IllegalStateException(String.valueOf(unkownVariableHandling));
				}
			} else if (node instanceof Constant) {
				final Constant<?> constant = (Constant<?>) node;
				values.push(constant.getValue());
			} else {
				final Object value = assignment.get(index).orElse(null);
				if (value == null) {
					if (variable instanceof BoolVariable) {
						values.push(defaultBooleanValue);
					} else {
						values.push(null);
					}
				} else {
					if (!variable.getType().isInstance(value)) {
						throw new IllegalArgumentException(String.valueOf(value));
					}
					values.push(value);
				}
			}
		} else if (node instanceof Function) {
			@SuppressWarnings("unchecked")
			final Function<Object, Object> function = (Function<Object, Object>) node;
			final List<Object> arguments = values.subList(0, function.getChildren().size());
			Collections.reverse(arguments);
			final Object value = function.eval(arguments).get();
			arguments.clear();
			values.push(value);
		} else {
			throw new IllegalStateException(String.valueOf(node));

		}
		return VisitorResult.Continue;
	}

}
