package org.spldev.formulas.manipulators;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.trees.visitors.*;

public class TreeSimplifier implements TreeVisitor<Expression> {

	private final HashSet<Expression> childrenSet = new HashSet<>();

	private boolean fail;

	@Override
	public void reset() {
		fail = false;
		childrenSet.clear();
	}

	public boolean isSuccess() {
		return !fail;
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			return VistorResult.Continue;
		} else if (node instanceof Term) {
			return VistorResult.SkipSiblings;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	private List<Expression> removeDuplicates(Expression node) {
		if (childrenSet.add(node)) {
			return null;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Compound) {
			node.replaceChildren(this::mergeSingle);
			if (node instanceof And) {
				node.replaceChildrenWithList(this::mergeAnd);
			} else if (node instanceof Or) {
				node.replaceChildrenWithList(this::mergeOr);
			}
			if (node.getChildren().size() > 1) {
				childrenSet.clear();
				node.replaceChildrenWithList(this::removeDuplicates);
			}
		}
		return VistorResult.Continue;
	}

	private Expression mergeSingle(Expression child) {
		return (child.getChildren().size() == 1) && ((child instanceof And) || (child instanceof Or))
			? child.getChildren().get(0)
			: null;
	}

	private List<? extends Expression> mergeAnd(final Expression child) {
		return (child instanceof And) ? child.getChildren() : null;
	}

	private List<? extends Expression> mergeOr(final Expression child) {
		return (child instanceof Or) ? child.getChildren() : null;
	}

}
