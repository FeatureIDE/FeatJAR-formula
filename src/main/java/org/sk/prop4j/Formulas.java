package org.sk.prop4j;

import org.sk.prop4j.manipulators.*;
import org.sk.prop4j.manipulators.DistributiveLawTransformer.NormalForm;
import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.compound.And;
import org.sk.prop4j.visitors.*;
import org.sk.trees.Trees;
import org.sk.trees.structure.Tree;
import org.sk.trees.visitors.*;
import org.sk.trees.visitors.basic.TreeDepthCounter;

public class Formulas extends Trees {

	public static <R extends NodeVisitor> R traverse(Formula node, R visitor) {
		return TreeTraverser.traverse(node, visitor);
	}

	public static PreOrderVisitor preOrderTraveral(Formula node, PreOrderVisitor visitor) {
		return TreeTraverser.preOrderTraveral(node, visitor);
	}

	public static PostOrderVisitor postOrderTraveral(Formula node, PostOrderVisitor visitor) {
		return TreeTraverser.postOrderTraveral(node, visitor);
	}

	@SuppressWarnings("unchecked")
	public static <R extends Formula> R manipulate(R node, NodeVisitor visitor) {
		final AuxiliaryRootFormula auxiliaryRoot = new AuxiliaryRootFormula(node);
		TreeTraverser.traverse(auxiliaryRoot, visitor);
		return (R) auxiliaryRoot.getNode();
	}

	public static Formula toCNF(Formula node) {
		if (!TreeTraverser.traverse(node, new CNFVisitor()).isCnf()) {
			node = manipulate(node, new EquivalenceTransformer());
			node = manipulate(node, new DeMorganTransformer());
			node = manipulate(node, new TreeSimplifier());
			node = (node instanceof And) ? node : new And(node);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.CNF);
			node = manipulate(node, visitor);
			return node;
		} else {
			return node.clone();
		}
	}

	public static Formula toDNF(Formula node) {
		if (!TreeTraverser.traverse(node, new DNFVisitor()).isDnf()) {
			node = manipulate(node, new EquivalenceTransformer());
			node = manipulate(node, new DeMorganTransformer());
			node = manipulate(node, new TreeSimplifier());
			node = (node instanceof And) ? node : new And(node);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.DNF);
			node = manipulate(node, visitor);
			return node;
		} else {
			return node.clone();
		}
	}

	public static Formula toCNF(Formula node, boolean clausal) {
		return null;
	}

	public static Formula toDNF(Formula node, boolean clausal) {
		return null;
	}

	public static boolean isClausalCNF(Formula node) {
		return TreeTraverser.traverse(node, new CNFVisitor()).isClausalCnf();
	}

	public static boolean isClausalDNF(Formula node) {
		return TreeTraverser.traverse(node, new DNFVisitor()).isClausalDnf();
	}

	public static boolean isCNF(Formula node) {
		return TreeTraverser.traverse(node, new CNFVisitor()).isCnf();
	}

	public static boolean isDNF(Formula node) {
		return TreeTraverser.traverse(node, new DNFVisitor()).isDnf();
	}

	public static int getMaxDepth(Tree node) {
		return TreeTraverser.traverse(node, new TreeDepthCounter()).getMaxDepth();
	}

}
