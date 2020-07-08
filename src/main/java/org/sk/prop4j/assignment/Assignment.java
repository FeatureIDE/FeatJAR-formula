package org.sk.prop4j.assignment;

import java.util.*;

public class Assignment {

	private final List<Object> assignments;
	private final Variables variables;

	public static Assignment emptyAssignment(Variables variables) {
		return new Assignment(variables);
	}

	public Variables getVariables() {
		return variables;
	}

	public Assignment(Variables variables) {
		this.variables = Objects.requireNonNull(variables);
		assignments = new ArrayList<>(variables.getMaxIndex() + 1);
		for (int i = 0; i < variables.size(); i++) {
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

	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(int index) {
		if (variables.hasVariable(index)) {
			return Optional.ofNullable((T) assignments.get(index));
		} else {
			throw new NoSuchElementException(String.valueOf(index));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(String name) {
		final int index = variables.getIndex(name).orElseThrow(() -> new NoSuchElementException(name));
		return Optional.ofNullable((T) assignments.get(index));
	}

}
