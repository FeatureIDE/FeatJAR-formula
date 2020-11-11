package org.spldev.formulas.visitors;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.trees.structure.*;
import org.spldev.trees.visitors.*;

public class DNFVisitor implements TreeVisitor<Expression> {

	private boolean isDnf = false;
	private boolean isClausalDnf = false;
	private int depth;

	@Override
	public void reset() {
		isDnf = true;
		isClausalDnf = true;
		depth = 0;
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		depth++;
		if (node instanceof Or) {
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof And)) {
					if (!(child instanceof Atomic)) {
						isDnf = false;
						isClausalDnf = false;
						return VistorResult.SkipAll;
					}
					isClausalDnf = false;
				}
			}
		} else if (node instanceof And) {
			if (depth != 2) {
				isClausalDnf = false;
			}
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof Atomic)) {
					isDnf = false;
					isClausalDnf = false;
					return VistorResult.SkipAll;
				}
			}
		} else if (node instanceof Atomic) {
			if (depth != 3) {
				isClausalDnf = false;
			}
			return VistorResult.SkipChildren;
		} else {
			isDnf = false;
			isClausalDnf = false;
			return VistorResult.SkipAll;
		}
		return VistorResult.Continue;
	}

	public boolean isDnf() {
		return isDnf;
	}

	public boolean isClausalDnf() {
		return isClausalDnf;
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		depth--;
		return VistorResult.Continue;
	}

}
