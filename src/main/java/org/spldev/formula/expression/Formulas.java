package org.spldev.formula.expression;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.*;
import org.spldev.formula.expression.ValueVisitor.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.transform.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class Formulas {

	public enum NormalForm {
		CNF, DNF
	}

	public static Optional<Object> evaluate(Expression expression, Assignment assignment) {
		final ValueVisitor visitor = new ValueVisitor(assignment);
		visitor.setUnkown(UnkownVariableHandling.ERROR);
		return Trees.traverse(expression, visitor);
	}

	public static Formula toCNF(Formula formula) {
		return toNF(formula, NormalForm.CNF);
	}

	public static Formula toDNF(Formula formula) {
		return toNF(formula, NormalForm.DNF);
	}

	private static Formula toNF(Formula formula, NormalForm normalForm) {
		if (checkNormalForm(formula, normalForm)) {
			return (Formula) Trees.cloneTree(formula);
		}
		return distributiveLawTransform(simplifyForNF(formula), normalForm);
	}

	public static boolean checkNormalForm(Formula formula, NormalForm normalForm) {
		switch (normalForm) {
		case CNF:
			if (Trees.traverse(formula, new CNFVisitor()).get()) {
				return true;
			}
			break;
		case DNF:
			if (Trees.traverse(formula, new DNFVisitor()).get()) {
				return true;
			}
			break;
		default:
			throw new IllegalStateException(String.valueOf(normalForm));
		}
		return false;
	}

	public static Formula simplifyForNF(Formula formula) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
		Trees.traverse(auxiliaryRoot, new EquivalenceTransformer());
		Trees.traverse(auxiliaryRoot, new DeMorganTransformer());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		return (Formula) auxiliaryRoot.getChild();
	}

	public static Formula distributiveLawTransform(Formula root, NormalForm normalForm) {
		final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(root);
		switch (normalForm) {
		case CNF:
			if (!(root instanceof And)) {
				auxiliaryRoot.setChild(new And(root));
			}
			break;
		case DNF:
			if (!(root instanceof Or)) {
				auxiliaryRoot.setChild(new Or(root));
			}
			break;
		default:
			throw new IllegalStateException(String.valueOf(normalForm));
		}
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
		final NFVisitor visitor = new CNFVisitor();
		Trees.traverse(formula, visitor);
		return visitor.isClausalNf();
	}

	public static boolean isClausalDNF(Formula formula) {
		final NFVisitor visitor = new DNFVisitor();
		Trees.traverse(formula, visitor);
		return visitor.isClausalNf();
	}

	public static boolean isCNF(Formula formula) {
		return Trees.traverse(formula, new CNFVisitor()).get();
	}

	public static boolean isDNF(Formula formula) {
		return Trees.traverse(formula, new DNFVisitor()).get();
	}

	public static Expression manipulate(Expression node, TreeVisitor<Void, Expression> visitor) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
		Trees.traverse(auxiliaryRoot, visitor);
		return auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Expression expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).get();
	}

	public static Stream<String> getVariableStream(Expression node) {
		return Trees.preOrderStream(node)
			.filter(n -> n instanceof Terminal)
			.map(n -> ((Terminal) n).getName())
			.distinct();
	}

	public static List<String> getVariables(Expression node) {
		return getVariableStream(node).collect(Collectors.toList());
	}

	public static VariableMap createVariableMapping(Expression node) {
		return new VariableMap(getVariables(node));
	}

}
