package org.spldev.formula.expression;

import java.util.*;

import org.spldev.formula.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.atomic.predicate.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.*;
import org.spldev.tree.visitor.*;

public class ValueVisitor implements TreeVisitor<Object, Expression> {

	public enum UnkownVariableHandling {
		ERROR, FALSE, TRUE,
	}

	private final LinkedList<Object> values = new LinkedList<>();

	private UnkownVariableHandling unkownVariableHandling = UnkownVariableHandling.FALSE;

	private final Assignment assignment;

	public ValueVisitor(Assignment assignment) {
		this.assignment = assignment;
	}

	public UnkownVariableHandling getUnkown() {
		return unkownVariableHandling;
	}

	public void setUnkown(UnkownVariableHandling unkown) {
		unkownVariableHandling = unkown;
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
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			if (node instanceof LiteralVariable) {
				final Literal literal = (Literal) node;
				final boolean positive = literal.isPositive();
				final int index;
				if (node instanceof ErrorLiteral) {
					index = 0;
				} else {
					index = assignment.getVariables().getIndex(literal.getName()).orElse(0);
				}
				if (index == 0) {
					switch (unkownVariableHandling) {
					case ERROR:
						throw new NullPointerException();
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
					final Object value = assignment.get(index).orElse(null);
					if (value == null) {
						values.push(null);
					} else if (value == Boolean.FALSE) {
						values.push(positive ? Boolean.FALSE : Boolean.TRUE);
					} else if (value == Boolean.TRUE) {
						values.push(positive ? Boolean.TRUE : Boolean.FALSE);
					} else {
						throw new IllegalArgumentException(String.valueOf(value));
					}
				}
			} else if (node == Literal.True) {
				values.push(Boolean.TRUE);
			} else if (node == Literal.False) {
				values.push(Boolean.FALSE);
			} else if (node instanceof Predicate) {
				@SuppressWarnings("unchecked")
				final Predicate<Object> predicate = (Predicate<Object>) node;
				final List<Object> arguments = values.subList(0, predicate.getChildren().size());
				final Boolean value = predicate.eval(arguments).get();
				arguments.clear();
				values.push(value);
			} else {
				throw new IllegalStateException(String.valueOf(node));
			}
		} else if (node instanceof Connective) {
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
					final Boolean leftChild = (Boolean) values.pop();
					final Boolean rightChild = (Boolean) values.pop();
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
					final Boolean leftChild = (Boolean) values.pop();
					final Boolean rightChild = (Boolean) values.pop();
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
		} else if (node instanceof Constant) {
			final Constant<?> constant = (Constant<?>) node;
			values.push(constant.getValue());
		} else if (node instanceof Variable) {
			final Variable<?> variable = (Variable<?>) node;
			final int index = assignment.getVariables().getIndex(variable.getName()).orElse(0);
			if (index == 0) {
				switch (unkownVariableHandling) {
				case ERROR:
					throw new NullPointerException();
				case FALSE:
				case TRUE:
					values.push(variable.getDefaultValue());
					break;
				default:
					throw new IllegalStateException(String.valueOf(unkownVariableHandling));
				}
			} else {
				final Object value = assignment.get(index).orElse(null);
				if (value == null) {
					values.push(variable.getDefaultValue());
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
			final Object value = function.eval(arguments).get();
			arguments.clear();
			values.push(value);
		} else {
			throw new IllegalStateException(String.valueOf(node));

		}
		return VistorResult.Continue;
	}

}
