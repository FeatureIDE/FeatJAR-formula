package org.spldev.formula.manipulator;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.tree.visitor.*;

public class DeMorganTransformer implements TreeVisitor<Expression> {

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
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
				newNode = new And(((Or) notChild).getChildren().stream().map(Not::new).collect(Collectors.toList()));
			} else if (notChild instanceof And) {
				newNode = new Or(((And) notChild).getChildren().stream().map(Not::new).collect(Collectors.toList()));
			}
		}
		return newNode;
	}

}
