package org.spldev.formulas.visitors;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.trees.visitors.*;

public class VariableCollector implements TreeVisitor<Expression> {

	private final List<Terminal> variableList = new ArrayList<>();

	public List<Terminal> getVariableList() {
		return variableList;
	}

	@Override
	public void reset() {
		variableList.clear();
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression currentNode = TreeVisitor.getCurrentNode(path);
		if (currentNode instanceof Terminal) {
			variableList.add((Terminal) currentNode);
		}
		return VistorResult.Continue;
	}

}
