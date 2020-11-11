import org.spldev.formulas.*;
import org.spldev.formulas.parse.*;
import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.atomic.*;
import org.spldev.formulas.structure.compound.*;
import org.spldev.trees.*;
import org.spldev.trees.visitors.*;

public class Main {

	public static void main(String[] args) {
		final Literal literalA = new Literal("a");
		final Literal literalB = new Literal("b");
		final Literal literalC = new Literal("c");
		final Literal literalD = new Literal("d");
		final Literal literalE = new Literal("e");
		final Literal literalX = new Literal("x");
		final Literal literalY = new Literal("y");
		final Literal literalZ = new Literal("z");

//		printCNF(literalA);
//		printCNF(new And(literalA, literalB));
//		printCNF(new And(literalA, literalA));
//		printCNF(new And(new Not(literalA), new Not(literalB)));
//		printCNF(new And(literalA, new Not(literalA)));

//		printCNF(new Or(new And(literalA, literalB), new And(literalX, literalY)));
//		printCNF(new Not(
//				new Or(
//						new Not(new And(literalA, literalB)), 
//						new Not(new And(literalX, literalY))
//				)));		
//		printCNF(new Not(literalA));
		printCNF(new And(new Not(literalA), literalA));
		System.out.println("OK!");
	}

	private static void printCNF(Formula node) {
		System.out.println(new NodeWriter().write(node));
		System.out.println("=====");
		Trees.traverse(node, new TreePrinter());
		final Formula cnf = Formulas.toCNF(node);
		System.out.println("-----");
		Trees.traverse(cnf, new TreePrinter());
		System.out.println("=====");
	}

}
