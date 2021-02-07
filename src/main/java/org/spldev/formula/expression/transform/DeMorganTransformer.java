package org.spldev.formula.expression.transform;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.tree.visitor.*;

public class DeMorganTransformer implements TreeVisitor<Void, Expression> {

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			node.replaceChildren(this::replace);
			return VistorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			node.replaceChildren(this::replace);
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	private Expression replace(Expression node) {
		Expression newNode = null;
		if (node instanceof Not) {
			final Formula notChild = (Formula) node.getChildren().iterator().next();
			if (notChild instanceof Literal) {
				newNode = ((Literal) notChild).flip();
			} else if (notChild instanceof Not) {
				newNode = notChild.getChildren().get(0);
			} else if (notChild instanceof Or) {
				newNode = new And(((Compound) notChild).getChildren().stream().map(Not::new).collect(Collectors
					.toList()));
			} else if (notChild instanceof And) {
				newNode = new Or(((Compound) notChild).getChildren().stream().map(Not::new).collect(Collectors
					.toList()));
			}
		}
		return newNode;
	}

}
