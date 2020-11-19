package org.spldev.formula;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.assignment.*;
import org.spldev.formula.manipulator.*;
import org.spldev.formula.manipulator.DistributiveLawTransformer.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.visitor.*;
import org.spldev.formula.visitor.ValueVisitor.*;
import org.spldev.tree.*;
import org.spldev.tree.visitor.*;

public class Formulas {

	public static Optional<Object> evaluate(Expression expression, Assignment assignment) {
		final ValueVisitor visitor = new ValueVisitor(assignment);
		visitor.setUnkown(UnkownVariableHandling.ERROR);
		Trees.traverse(expression, visitor);
		return visitor.getValue();
	}

	public static Formula toCNF(Formula formula) {
		return toNF(formula, NormalForm.CNF);
	}

	public static Formula toDNF(Formula formula) {
		return toNF(formula, NormalForm.DNF);
	}

	private static Formula toNF(Formula formula, NormalForm normalForm) {
		switch (normalForm) {
		case CNF:
			if (Trees.traverse(formula, new CNFVisitor()).get().isNf()) {
				return (Formula) Trees.cloneTree(formula);
			}
			break;
		case DNF:
			if (Trees.traverse(formula, new DNFVisitor()).get().isNf()) {
				return (Formula) Trees.cloneTree(formula);
			}
			break;
		default:
			throw new IllegalStateException(String.valueOf(normalForm));
		}
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(formula));
		Trees.traverse(auxiliaryRoot, new EquivalenceTransformer());
		Trees.traverse(auxiliaryRoot, new DeMorganTransformer());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());

		final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
		if (!(auxiliaryRoot.getChild() instanceof And)) {
			auxiliaryRoot.setChild(new And((Formula) auxiliaryRoot.getChild()));
		}
		visitor.setNormalForm(normalForm);
		Trees.traverse(auxiliaryRoot, visitor);

		return (Formula) auxiliaryRoot.getChild();
	}

	public static Formula toCNF(Formula formula, boolean clausal) {
		return null;
	}

	public static Formula toDNF(Formula formula, boolean clausal) {
		return null;
	}

	public static boolean isClausalCNF(Formula formula) {
		return Trees.traverse(formula, new CNFVisitor()).get().isClausalNf();
	}

	public static boolean isClausalDNF(Formula formula) {
		return Trees.traverse(formula, new DNFVisitor()).get().isClausalNf();
	}

	public static boolean isCNF(Formula formula) {
		return Trees.traverse(formula, new CNFVisitor()).get().isNf();
	}

	public static boolean isDNF(Formula formula) {
		return Trees.traverse(formula, new DNFVisitor()).get().isNf();
	}

	public static Expression manipulate(Expression node, TreeVisitor<Expression> visitor) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
		Trees.traverse(auxiliaryRoot, visitor);
		return auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Expression expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).get().getMaxDepth();
	}

	public static VariableMap createVariableMapping(Expression node) {
		final List<String> variableList = Trees.preOrderStream(node)
			.filter(n -> n instanceof Terminal)
			.map(n -> ((Terminal) n).getName())
			.distinct()
			.collect(Collectors.toList());
		return new VariableMap(variableList);
	}

}
