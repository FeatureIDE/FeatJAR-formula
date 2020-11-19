package org.spldev.formula.manipulator;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.tree.visitor.*;

public class EquivalenceTransformer implements TreeVisitor<Expression> {

	private boolean fail;

	@Override
	public void reset() {
		fail = false;
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			if (node instanceof Quantifier) {
				return VistorResult.Fail;
			}
			return VistorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		node.replaceChildren(this::replace);
		if (fail) {
			return VistorResult.Fail;
		}
		return VistorResult.Continue;
	}

	@SuppressWarnings("unchecked")
	private Formula replace(Expression node) {
		if (((node instanceof Atomic) || (node instanceof And) || (node instanceof Or)
			|| (node instanceof Not))) {
			return null;
		}
		final List<Formula> children = (List<Formula>) node.getChildren();
		Formula newNode = null;
		if (node instanceof Implies) {
			newNode = new Or(new Not(children.get(0)), children.get(1));
		} else if (node instanceof Equals) {
			newNode = new And(new Or(new Not(children.get(0)), children.get(1)),
				new Or(new Not(children.get(1)), children.get(0)));
		} else if (node instanceof AtLeast) {
			newNode = convertCardinals(children, ((AtLeast) node).getMin(), false);
		} else if (node instanceof AtMost) {
			newNode = convertCardinals(children, ((AtMost) node).getMax(), true);
		} else if (node instanceof Between) {
			final Between between = (Between) node;
			newNode = new And(
				convertCardinals(children, between.getMax(), true),
				convertCardinals(children, between.getMin(), false));
		} else if (node instanceof Choose) {
			final Choose choose = (Choose) node;
			newNode = new And(
				convertCardinals(children, choose.getK(), true),
				convertCardinals(children, choose.getK(), false));
		} else {
			fail = true;
			return null;
		}
		return newNode;
	}

	private And convertCardinals(final List<? extends Formula> children, int k, boolean negated) {
		return new And(chooseKofN(children, (children.size() - k) + 1, negated));
	}

	private List<Formula> chooseKofN(List<? extends Formula> elements, int k, boolean negated) {
		final int n = elements.size();

		// return tautology
		if ((k == 0) || (k == (n + 1))) {
			return Arrays.asList(Literal.True);
		}

		// return contradiction
		if ((k < 0) || (k > (n + 1))) {
			return Arrays.asList(Literal.False);
		}

		final List<Formula> newNodes = new ArrayList<>();

		// negate all elements
		if (negated) {
			elements = elements.stream().map(Not::new).collect(Collectors.toList());
		}

		final Formula[] clause = new Formula[k];
		final int[] index = new int[k];

		// the position that is currently filled in clause
		int level = 0;
		index[level] = -1;

		while (level >= 0) {
			// fill this level with the next element
			index[level]++;
			// did we reach the maximum for this level
			if (index[level] >= (n - (k - 1 - level))) {
				// go to previous level
				level--;
			} else {
				clause[level] = elements.get(index[level]);
				if (level == (k - 1)) {
					newNodes.add(new Or(Arrays.copyOf(clause, clause.length)));
				} else {
					// go to next level
					level++;
					// allow only ascending orders (to prevent from duplicates)
					index[level] = index[level - 1];
				}
			}
		}
		return newNodes;
	}

}
