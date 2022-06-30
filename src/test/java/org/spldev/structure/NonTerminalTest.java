package org.spldev.structure;

import org.junit.jupiter.api.Test;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.Formulas;
import org.spldev.formula.structure.NonTerminal;
import org.spldev.formula.structure.atomic.literal.Literal;
import org.spldev.formula.structure.atomic.literal.True;
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.formula.structure.compound.And;
import org.spldev.formula.structure.compound.Implies;
import org.spldev.formula.structure.compound.Or;
import org.spldev.formula.structure.term.Variable;
import org.spldev.util.tree.Trees;

import java.beans.Expression;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NonTerminalTest {
	@Test
	public void createSimpleFormulas() {
		final VariableMap map = VariableMap.emptyMap();
		final Literal p1 = map.booleanLiteral("p");
		final Literal p2 = map.booleanLiteral("p");
		assertEquals(p1, p2);
		assertNotSame(p1, p2);
		final Literal q = map.booleanLiteral("q");
		assertDoesNotThrow(() -> new And(p1, q));
		assertEquals(new And(p1, q), new And(p2, q));
		assertNotEquals(new And(p1, q), new And(q, p2));
		assertDoesNotThrow(() -> Formulas.create(m -> new Implies(m.booleanLiteral("p"), m.booleanLiteral("q"))));
		assertEquals(Formulas.create(VariableMap::falseLiteral), Formulas.create(VariableMap::falseLiteral));
		assertNotEquals(Formulas.create(VariableMap::falseLiteral), Formulas.create(VariableMap::trueLiteral));
	}

	@Test
	public void ensureSharedVariableMap() {
		final VariableMap map1 = VariableMap.emptyMap();
		final VariableMap map2 = VariableMap.emptyMap();
		assertThrows(IllegalArgumentException.class, () -> new And(Literal.True, Literal.False));
		assertDoesNotThrow(() -> new And(map1.trueLiteral(), map1.falseLiteral()));
		assertThrows(IllegalArgumentException.class, () -> new And(map1.trueLiteral(), map2.falseLiteral()));
		Formula formula1 = Formulas.create(m -> new Implies(m.booleanLiteral("p"), m.booleanLiteral("q")));
		Formula formula2 = Formulas.create(m -> new Implies(m.booleanLiteral("q"), m.booleanLiteral("r")));
		assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
	}

	@Test
	public void compose() {
		{
			Formula formula1 = Formulas.create(VariableMap::trueLiteral);
			Formula formula2 = Formulas.create(VariableMap::trueLiteral);
			assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
			assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, formula2));
			Formula formula3 = Formulas.compose(And::new, formula1, formula2);
			Literal x = formula3.getFirstChild().get().getVariableMap().booleanLiteral("x");
			assertDoesNotThrow(() -> Formulas.compose(And::new, formula3, x));
		}
		Consumer<Formula> test = formula1 -> {
			Formula formula2 = Formulas.create(m -> new Implies(m.booleanLiteral("p"), m.booleanLiteral("q")));
			Formula formula3 = Formulas.create(m -> new Implies(m.booleanLiteral("q"), m.booleanLiteral("r")));
			assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2, formula3));
			assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, formula2, formula3));
			Formula formula = Formulas.compose(And::new, formula1, formula2, formula3);
			assertNotEquals(formula1.getVariableMap(), formula);
			assertNotEquals(formula2.getVariableMap(), formula);
			assertNotEquals(formula3.getVariableMap(), formula);
			assertEquals(formula.getVariableMap(), formula.getChildren().get(0).getVariableMap());
			assertEquals(formula.getVariableMap(), formula.getChildren().get(1).getVariableMap());
			assertEquals(formula.getVariableMap(), formula.getChildren().get(2).getVariableMap());
		};
		VariableMap m = VariableMap.emptyMap();
		List<Literal> children = List.of(m.booleanLiteral("p"), m.booleanLiteral("q"));
		test.accept(Formulas.create(VariableMap::trueLiteral));
		test.accept(And.empty());
		test.accept(Literal.True);
	}
}
