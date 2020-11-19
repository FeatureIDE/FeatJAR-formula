package org.spldev.structure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;

public class AuxiliaryRootTest {

	private Expression expression1, expression2;

	@BeforeEach
	public void setUp() {
		expression1 = new LiteralVariable("L1");
		expression2 = new LiteralVariable("L2");
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
