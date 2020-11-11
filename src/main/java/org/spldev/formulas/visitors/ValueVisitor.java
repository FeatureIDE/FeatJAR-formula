package org.spldev.formulas.visitors;

import java.util.*;

import org.spldev.formulas.assignment.*;
import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.atomic.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.formulas.structure.functions.*;
import org.spldev.formulas.structure.terms.*;
import org.spldev.trees.visitors.*;

public class ValueVisitor implements TreeVisitor<Expression> {

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
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Literal) {
			if (node instanceof True) {
				values.push(((Literal) node).isPositive());
			} else if (node instanceof False) {
				values.push(!((Literal) node).isPositive());
			} else if (node instanceof ErrorLiteral) {
				values.push(((Literal) node).isPositive());
			} else {
				final Object value = assignment.get(((Literal) node).getName()).orElseGet(() -> {
					switch (unkownVariableHandling) {
					case ERROR:
						throw new NullPointerException();
					case FALSE:
						return Boolean.FALSE;
					case TRUE:
						return Boolean.TRUE;
					default:
						throw new IllegalStateException(String.valueOf(unkownVariableHandling));
					}
				});
				if (!(value instanceof Boolean)) {
					throw new IllegalArgumentException(String.valueOf(value));
				}
				values.push(value);
			}
		} else if (node instanceof Connective) {
			final int size = node.getChildren().size();
			if (node instanceof And) {
				Boolean result = Boolean.TRUE;
				for (int i = 0; i < size; i++) {
					final boolean value = (boolean) values.pop();
					if (!value) {
						result = Boolean.FALSE;
					}
				}
				values.push(result);
			} else if (node instanceof Or) {
				Boolean result = Boolean.FALSE;
				for (int i = 0; i < size; i++) {
					final boolean value = (boolean) values.pop();
					if (value) {
						result = Boolean.TRUE;
					}
				}
				values.push(result);
			} else if (node instanceof Not) {
				values.push(!(boolean) values.pop());
			} else if (node instanceof Implies) {
				if (size != 2) {
					for (int i = 0; i < size; i++) {
						values.pop();
					}
					values.push(Boolean.FALSE);
				} else {
					final Boolean leftChild = (Boolean) values.pop();
					final Boolean rightChild = (Boolean) values.pop();
					if (leftChild) {
						values.push(rightChild);
					} else {
						values.push(Boolean.TRUE);
					}
				}
			} else if (node instanceof Equals) {
				if (size != 2) {
					for (int i = 0; i < size; i++) {
						values.pop();
					}
					values.push(Boolean.FALSE);
				} else {
					final Boolean leftChild = (Boolean) values.pop();
					final Boolean rightChild = (Boolean) values.pop();
					values.push(leftChild == rightChild);
				}
			} else if (node instanceof Cardinal) {
				int count = 0;
				for (int i = 0; i < size; i++) {
					final boolean value = (boolean) values.pop();
					if (value) {
						count++;
					}
				}
				values.push((count >= ((Cardinal) node).getMin()) && (count <= ((Cardinal) node).getMax()));
			} else {
				throw new IllegalStateException(String.valueOf(node));
			}
		} else if (node instanceof Predicate) {
			@SuppressWarnings("unchecked")
			final Predicate<Object> predicate = (Predicate<Object>) node;
			final int size = predicate.getChildren().size();
			final ArrayList<Object> predicateValues = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				predicateValues.add(values.pop());
			}
			values.push(predicate.eval(predicateValues));
		} else if (node instanceof Constant) {
			final Constant<?> constant = (Constant<?>) node;
			values.push(constant.getValue());
		} else if (node instanceof Variable) {
			final Variable<?> variable = (Variable<?>) node;
			final Object value = assignment.get(variable.getName()).orElseGet(() -> {
				switch (unkownVariableHandling) {
				case ERROR:
					throw new NullPointerException();
				case FALSE:
				case TRUE:
					return Objects.requireNonNull(variable.getDefaultValue());
				default:
					throw new IllegalStateException(String.valueOf(unkownVariableHandling));
				}
			});
			if (!variable.getType().isInstance(value)) {
				throw new IllegalArgumentException(String.valueOf(value));
			}
			values.push(value);
		} else if (node instanceof Function) {
			@SuppressWarnings("unchecked")
			final Function<Object, Object> predicate = (Function<Object, Object>) node;
			final int size = predicate.getChildren().size();
			final ArrayList<Object> predicateValues = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				predicateValues.add(values.pop());
			}
			values.push(predicate.eval(predicateValues));
		} else {
			throw new IllegalStateException(String.valueOf(node));

		}
		return VistorResult.Continue;
	}

}
