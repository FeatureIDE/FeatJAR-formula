package org.spldev.structure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.function.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;

public class NonTerminalTest {
	@Test
	public void createSimpleFormulas() {
		final VariableMap map = new VariableMap();
		final Literal p1 = map.createLiteral("p");
		final Literal p2 = map.createLiteral("p");
		assertEquals(p1, p2);
		assertNotSame(p1, p2);
		final Literal q = map.createLiteral("q");
		assertDoesNotThrow(() -> new And(p1, q));
		assertEquals(new And(p1, q), new And(p2, q));
		assertNotEquals(new And(p1, q), new And(q, p2));
		assertDoesNotThrow(() -> Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q"))));
		assertEquals(Formulas.create(m -> m.createLiteral("1")), Formulas.create(m -> m.createLiteral("1")));
		assertEquals(Formulas.create(m -> m.createLiteral("1", false)), Formulas.create(m -> m.createLiteral("1", false)));
		assertNotEquals(Formulas.create(m -> m.createLiteral("1", false)), Formulas.create(m -> m.createLiteral("1")));
	}

	@Test
	public void ensureSharedVariableMap() {
		final VariableMap map1 = new VariableMap();
		final VariableMap map2 = new VariableMap();
		assertThrows(IllegalArgumentException.class, () -> new And(Literal.True, Literal.False));
		assertDoesNotThrow(() -> new And(map1.createLiteral("a"), map1.createLiteral("a", false)));
		assertThrows(IllegalArgumentException.class, () -> new And(map1.createLiteral("a"), map2.createLiteral("a", false)));
		Formula formula1 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
		Formula formula2 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
		assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
	}

	@Test
	public void compose() {
		{
			Formula formula1 = Formulas.create(m -> m.createLiteral("a"));
			Formula formula2 = Formulas.create(m -> m.createLiteral("a"));
			assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
			assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, formula2));
			Formula formula3 = Formulas.compose(And::new, formula1, formula2);
			Literal x = formula3.getFirstChild().get().getVariableMap().createLiteral("x");
			assertDoesNotThrow(() -> Formulas.compose(And::new, formula3, x));
		}
		Consumer<Formula> test = formula1 -> {
			Formula formula2 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
			Formula formula3 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
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
		VariableMap m = new VariableMap();
		List.of(m.createLiteral("p"), m.createLiteral("q"));
		test.accept(Formulas.create(map -> m.createLiteral("a")));
		test.accept(new And());
		test.accept(Literal.True);
	}
}
