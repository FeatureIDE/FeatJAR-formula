package org.sk.prop4j.manipulators;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.atomic.Atomic;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.Tree;
import org.sk.trees.visitors.NodeVisitor;

public class TreeSimplifier implements NodeVisitor {

	private final HashSet<Tree> childrenSet = new HashSet<>();

	private boolean fail;

	@Override
	public void init() {
		fail = false;
		childrenSet.clear();
	}

	public boolean isSuccess() {
		return !fail;
	}

	@Override
	public VistorResult firstVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
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

	private List<Formula> removeDuplicates(Formula node) {
		if (childrenSet.add(node)) {
			return null;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public VistorResult lastVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		if (node instanceof Compound) {
			((Compound) node).replaceChildren(this::mergeSingle);
			if (node instanceof And) {
				((And) node).mapChildren(this::mergeAnd);
			} else if (node instanceof Or) {
				((Or) node).mapChildren(this::mergeOr);
			}
			if (node.getChildren().size() > 1) {
				childrenSet.clear();
				((Compound) node).mapChildren(this::removeDuplicates);
			}
		}
		return VistorResult.Continue;
	}

	private Formula mergeSingle(Formula child) {
		if (child.getChildren().size() == 1) {
			if (child instanceof And) {
				return ((And) child).getChildren().get(0);
			} else if (child instanceof Or) {
				return ((Or) child).getChildren().get(0);
			}
		}
		return null;
	}

	private Collection<? extends Formula> mergeAnd(final Formula child) {
		return (child instanceof And) ? ((Compound) child).getChildren() : null;
	}

	private Collection<? extends Formula> mergeOr(final Formula child) {
		return (child instanceof Or) ? ((Compound) child).getChildren() : null;
	}

}
