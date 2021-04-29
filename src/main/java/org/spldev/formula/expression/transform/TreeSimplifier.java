package org.spldev.formula.expression.transform;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.tree.visitor.*;

public class TreeSimplifier implements TreeVisitor<Void, Expression> {

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

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			if (node instanceof And) {
				for (final Expression child : node.getChildren()) {
					if (child == Literal.False) {
						node.setChildren(Arrays.asList(Literal.False));
					}
				}
				node.flatMapChildren(this::mergeAnd);
			} else if (node instanceof Or) {
				for (final Expression child : node.getChildren()) {
					if (child == Literal.True) {
						node.setChildren(Arrays.asList(Literal.True));
					}
				}
				node.flatMapChildren(this::mergeOr);
			}
//			if (node.getChildren().size() > 1) {
//				node.setChildren(new HashSet<>(node.getChildren()));
//			}
		}
		return VistorResult.Continue;
	}

	private List<? extends Expression> mergeAnd(final Expression child) {
		return (child instanceof And) || (child.getChildren().size() == 1) ? child.getChildren() : null;
	}

	private List<? extends Expression> mergeOr(final Expression child) {
		return (child instanceof Or) || (child.getChildren().size() == 1) ? child.getChildren() : null;
	}

}
