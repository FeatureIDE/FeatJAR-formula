package org.spldev.structure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;

public class AuxiliaryRootTest {

	private Expression expression1, expression2;

	@BeforeEach
	public void setUp() {
		VariableMap map = new VariableMap(Arrays.asList("L1","L2"));
		expression1 = new LiteralVariable("L1", map);
		expression2 = new LiteralVariable("L2", map);
	}

	@Test
	public void createAuxiliaryRoot() {
		final AuxiliaryRoot newRoot = new AuxiliaryRoot(expression1);
		assertEquals(expression1, newRoot.getChild());
		assertEquals("", newRoot.getName());
	}

	@Test
	public void replaceChild() {
		final AuxiliaryRoot newRoot = new AuxiliaryRoot(expression1);
		newRoot.setChild(expression2);
		assertEquals(expression2, newRoot.getChild());
	}

}
