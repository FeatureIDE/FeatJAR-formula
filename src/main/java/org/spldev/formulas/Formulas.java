package org.spldev.formulas;

import java.util.stream.*;

import org.spldev.formulas.assignment.*;
import org.spldev.formulas.manipulators.*;
import org.spldev.formulas.manipulators.DistributiveLawTransformer.*;
import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.atomic.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.formulas.visitors.*;
import org.spldev.trees.*;
import org.spldev.trees.visitors.*;

public class Formulas {

	public static Formula toCNF(Formula formula) {
		if (!Trees.traverse(formula, new CNFVisitor()).isCnf()) {
			formula = manipulate(formula, new EquivalenceTransformer());
			formula = manipulate(formula, new DeMorganTransformer());
			formula = manipulate(formula, new TreeSimplifier());
			formula = (formula instanceof And) ? formula : new And(formula);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.DNF);
			formula = manipulate(formula, visitor);
			return formula;
		} else {
			return (Formula) Trees.cloneTree(formula);
		}
	}

	public static Formula toDNF(Formula formula) {
		if (!Trees.traverse(formula, new DNFVisitor()).isDnf()) {
			formula = manipulate(formula, new EquivalenceTransformer());
			formula = manipulate(formula, new DeMorganTransformer());
			formula = manipulate(formula, new TreeSimplifier());
			formula = (formula instanceof Or) ? formula : new Or(formula);
			final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
			visitor.setNormalForm(NormalForm.DNF);
			formula = manipulate(formula, visitor);
			return formula;
		} else {
			return (Formula) Trees.cloneTree(formula);
		}
	}

	public static Formula toCNF(Formula formula, boolean clausal) {
		return null;
	}

	public static Formula toDNF(Formula formula, boolean clausal) {
		return null;
	}

	public static boolean isClausalCNF(Formula formula) {
		return Trees.traverse(formula, new CNFVisitor()).isClausalCnf();
	}

	public static boolean isClausalDNF(Formula formula) {
		return Trees.traverse(formula, new DNFVisitor()).isClausalDnf();
	}

	public static boolean isCNF(Formula formula) {
		return Trees.traverse(formula, new CNFVisitor()).isCnf();
	}

	public static boolean isDNF(Formula formula) {
		return Trees.traverse(formula, new DNFVisitor()).isDnf();
	}

	@SuppressWarnings("unchecked")
	public static <R extends Formula> R manipulate(R node, TreeVisitor<Expression> visitor) {
		final AuxiliaryRootFormula auxiliaryRoot = new AuxiliaryRootFormula(node);
		Trees.traverse(auxiliaryRoot, visitor);
		return (R) auxiliaryRoot.getChild();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Expression> T simplify(T formula) {
		final AuxiliaryRootFormula auxiliaryRoot = new AuxiliaryRootFormula(Trees.cloneTree(formula));
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		return (T) auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Expression expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).getMaxDepth();
	}

	public static Variables getVariables(Formula formula) {
		return new Variables( //
			Trees.traverse(formula, new VariableCollector()).getVariableList() //
				.stream() //
				.map(Terminal::getName) //
				.distinct() //
				.collect(Collectors.toList()));
	}

	public static Literal getComplement(Literal literal) {
		return literal.cloneNode().flip();
	}

}
