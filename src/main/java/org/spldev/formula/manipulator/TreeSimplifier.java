package org.spldev.formula.manipulator;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.tree.visitor.*;

public class TreeSimplifier implements TreeVisitor<Expression> {

	private final HashSet<String> childrenSet = new HashSet<>();

	@Override
	public void reset() {
		childrenSet.clear();
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	private List<Expression> removeDuplicates(Expression node) {
		if (node instanceof Literal) {
			if (childrenSet.add(node.getName())) {
				return null;
			} else {
				return Collections.emptyList();
			}
		} else {
			return null;
		}
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			node.replaceChildren(this::mergeSingle);
			if (node instanceof And) {
				if (node.getChildren().contains(Literal.False)) {
					node.setChildren(Arrays.asList(Literal.False));
				} else {
					node.replaceChildrenWithList(this::mergeAnd);
				}
			} else if (node instanceof Or) {
				if (node.getChildren().contains(Literal.True)) {
					node.setChildren(Arrays.asList(Literal.True));
				} else {
					node.replaceChildrenWithList(this::mergeOr);
				}
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
