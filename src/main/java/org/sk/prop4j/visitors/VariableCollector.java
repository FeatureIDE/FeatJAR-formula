package org.sk.prop4j.visitors;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.trees.structure.*;
import org.sk.trees.visitors.*;

public class VariableCollector implements NodeVisitor {

	private final List<Terminal> variableList = new ArrayList<>();

	public List<Terminal> getVariableList() {
		return variableList;
	}

	@Override
	public void init() {
		variableList.clear();
	}

	@Override
	public VistorResult firstVisit(List<Tree> path) {
		final Tree currentNode = NodeVisitor.getCurrentNode(path);
		if (currentNode instanceof Terminal) {
			variableList.add((Terminal) currentNode);
		}
		return VistorResult.Continue;
	}

}
