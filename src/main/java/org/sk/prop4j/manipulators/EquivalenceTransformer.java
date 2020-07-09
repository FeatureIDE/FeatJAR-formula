package org.sk.prop4j.manipulators;

import java.util.*;
import java.util.stream.Collectors;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.atomic.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.Tree;
import org.sk.trees.visitors.NodeVisitor;

public class EquivalenceTransformer implements NodeVisitor {

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
			if (node instanceof Quantifier) {
				fail = true;
				return VistorResult.SkipAll;
			}
			return VistorResult.Continue;
		} else if (node instanceof Term) {
			return VistorResult.SkipSiblings;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	@Override
	public VistorResult lastVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		if (node instanceof Compound) {
			((Compound) node).replaceChildren(this::replace);
			if (fail) {
				return VistorResult.SkipAll;
			}
			return VistorResult.Continue;
		} else if (node instanceof Atomic) {
			return VistorResult.Continue;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	private Formula replace(Formula node) {
		final List<Formula> children;
		Formula newNode = null;
		if (node instanceof Implies) {
			children = ((Implies) node).getChildren();
			newNode = new Or(new Not(children.get(0)), children.get(1));
		} else if (node instanceof Equals) {
			children = ((Equals) node).getChildren();
			newNode = new And(new Or(new Not(children.get(0)), children.get(1)),
				new Or(new Not(children.get(1)), children.get(0)));
		} else if (node instanceof AtLeast) {
			children = ((AtLeast) node).getChildren();
			newNode = convertCardinals(children, ((AtLeast) node).getMin(), false);
		} else if (node instanceof AtMost) {
			children = ((AtMost) node).getChildren();
			newNode = convertCardinals(children, ((AtMost) node).getMax(), true);
		} else if (node instanceof Choose) {
			children = ((Choose) node).getChildren();
			final int n = ((Choose) node).getK();
			newNode = new And(convertCardinals(children, n, true), convertCardinals(children, n, false));
		} else if ((!(node instanceof And) && !(node instanceof Or) && !(node instanceof Not))) {
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
			return Arrays.asList(new True());
		}

		// return contradiction
		if ((k < 0) || (k > (n + 1))) {
			return Arrays.asList(new False());
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
