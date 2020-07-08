package org.sk.prop4j;

import java.util.stream.*;

import org.sk.prop4j.assignment.*;
import org.sk.prop4j.manipulators.*;
import org.sk.prop4j.manipulators.DistributiveLawTransformer.*;
import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.prop4j.visitors.*;
import org.sk.trees.*;
import org.sk.trees.visitors.*;
import org.sk.trees.visitors.basic.*;

public class Formulas extends Trees {

	public static Formula toCNF(Formula formula) {
		if (!TreeTraverser.traverse(formula, new CNFVisitor()).isCnf()) {
			formula = manipulate(formula, new EquivalenceTransformer());
			formula = manipulate(formula, new DeMorganTransformer());
			formula = manipulate(formula, new TreeSimplifier());
			formula = (formula instanceof And) ? formula : new And(formula);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.DNF);
			formula = manipulate(formula, visitor);
			return formula;
		} else {
			return formula.clone();
		}
	}

	public static Formula toDNF(Formula formula) {
		if (!TreeTraverser.traverse(formula, new DNFVisitor()).isDnf()) {
			formula = manipulate(formula, new EquivalenceTransformer());
			formula = manipulate(formula, new DeMorganTransformer());
			formula = manipulate(formula, new TreeSimplifier());
			formula = (formula instanceof Or) ? formula : new Or(formula);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.DNF);
			formula = manipulate(formula, visitor);
			return formula;
		} else {
			return formula.clone();
		}
	}

	public static Formula toCNF(Formula formula, boolean clausal) {
		return null;
	}

	public static Formula toDNF(Formula formula, boolean clausal) {
		return null;
	}

	public static boolean isClausalCNF(Formula formula) {
		return TreeTraverser.traverse(formula, new CNFVisitor()).isClausalCnf();
	}

	public static boolean isClausalDNF(Formula formula) {
		return TreeTraverser.traverse(formula, new DNFVisitor()).isClausalDnf();
	}

	public static boolean isCNF(Formula formula) {
		return TreeTraverser.traverse(formula, new CNFVisitor()).isCnf();
	}

	public static boolean isDNF(Formula formula) {
		return TreeTraverser.traverse(formula, new DNFVisitor()).isDnf();
	}

	@SuppressWarnings("unchecked")
	public static <R extends Formula> R manipulate(R node, NodeVisitor visitor) {
		final AuxiliaryRootFormula auxiliaryRoot = new AuxiliaryRootFormula(node);
		TreeTraverser.traverse(auxiliaryRoot, visitor);
		return (R) auxiliaryRoot.getNode();
	}

	public static int getMaxDepth(Expression expression) {
		return TreeTraverser.traverse(expression, new TreeDepthCounter()).getMaxDepth();
	}

	public static Variables getVariables(Formula formula) {
		return new Variables( //
			TreeTraverser.traverse(formula, new VariableCollector()).getVariableList() //
				.stream() //
				.map(Terminal::getName) //
				.distinct() //
				.collect(Collectors.toList()));
	}

}
