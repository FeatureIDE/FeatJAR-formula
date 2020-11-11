package org.spldev.formulas.manipulators;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.atomic.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.trees.*;
import org.spldev.trees.visitors.*;

public class DeMorganTransformer implements TreeVisitor<Expression> {

	private boolean fail;

	@Override
	public void reset() {
		fail = false;
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
			node.replaceChildren(this::replace);
			return VistorResult.Continue;
		} else if (node instanceof Term) {
			return VistorResult.SkipSiblings;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	private Expression replace(Expression node) {
		Expression newNode = null;
		if (node instanceof Not) {
			final Formula notChild = (Formula) node.getChildren().iterator().next();
			if (notChild instanceof Literal) {
				newNode = Trees.cloneTree(notChild);
				((Literal) newNode).flip();
			} else if (notChild instanceof Not) {
				newNode = notChild.getChildren().get(0);
			} else if (notChild instanceof Or) {
				final List<Formula> children = ((Or) notChild).getChildren();
				final List<Formula> newChildren = new ArrayList<>(children.size());
				for (final Formula child : children) {
					newChildren.add(new Not(child));
				}
				newNode = new And(newChildren);
			} else if (notChild instanceof And) {
				final List<Formula> children = ((And) notChild).getChildren();
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
