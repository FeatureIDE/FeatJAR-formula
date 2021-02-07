package org.spldev.formula;

import java.util.*;

public class Assignment {

	private final List<Object> assignments;
	private final VariableMap variables;

	public static Assignment emptyAssignment(VariableMap variables) {
		return new Assignment(variables);
	}

	public VariableMap getVariables() {
		return variables;
	}

	public Assignment(VariableMap variables) {
		this.variables = Objects.requireNonNull(variables);
		final int assignmentSize = variables.size() + 1;
		assignments = new ArrayList<>(assignmentSize);
		for (int i = 0; i < assignmentSize; i++) {
			assignments.add(null);
		}
	}

	public void set(int index, Object assignment) {
		if (variables.hasVariable(index)) {
			assignments.set(index, assignment);
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	public void set(String name, Object assignment) {
		final int index = variables.getIndex(name).orElseThrow(() -> new NoSuchElementException(name));
		assignments.set(index, assignment);
	}

	public Optional<Object> get(int index) {
		if (variables.hasVariable(index)) {
			return Optional.ofNullable(assignments.get(index));
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	public Optional<Object> get(String name) {
		final int index = variables.getIndex(name).orElseThrow(() -> new NoSuchElementException(name));
		return Optional.ofNullable(assignments.get(index));
	}

}
