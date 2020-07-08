package org.sk.prop4j.manipulators;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.atomic.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.*;
import org.sk.trees.visitors.*;

public class DeMorganTransformer implements NodeVisitor {

	private boolean fail;

	@Override
	public void init() {
		fail = false;
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
			((Compound) node).replaceChildren(this::replace);
			return VistorResult.Continue;
		} else if (node instanceof Term) {
			return VistorResult.SkipSiblings;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	private Formula replace(Formula node) {
		Formula newNode = null;
		if (node instanceof Not) {
			final Tree notChild = node.getChildren().iterator().next();
			if (notChild instanceof Literal) {
				newNode = ((Literal) notChild).clone();
				((Literal) newNode).flip();
			} else if (notChild instanceof Not) {
				newNode = ((Not) notChild).getChildren().get(0);
			} else if (notChild instanceof Or) {
				final List<? extends Formula> children = ((Or) notChild).getChildren();
				final List<Formula> newChildren = new ArrayList<>(children.size());
				for (final Formula child : children) {
					newChildren.add(new Not(child));
				}
				newNode = new And(newChildren);
			} else if (notChild instanceof And) {
				final List<? extends Formula> children = ((And) notChild).getChildren();
				final List<Formula> newChildren = new ArrayList<>(children.size());
				for (final Formula child : children) {
					newChildren.add(new Not(child));
				}
				newNode = new Or(newChildren);
			}
		}
		return newNode;
	}

}
