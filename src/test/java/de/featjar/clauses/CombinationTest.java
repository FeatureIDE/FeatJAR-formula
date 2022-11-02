package de.featjar.clauses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.featjar.clauses.solutions.combinations.LexicographicIterator;
import de.featjar.clauses.solutions.combinations.ParallelLexicographicIterator;

public class CombinationTest {

	@Test
	public void combinationIteration() {
		testForNandT(1, 20);
		testForNandT(2, 20);
		testForNandT(3, 20);
	}

	private void testForNandT(int t, int n) {
		List<String> pSet = ParallelLexicographicIterator.stream(t, n).map(Arrays::toString)
				.collect(Collectors.toList());
		List<String> sSet = LexicographicIterator.stream(t, n).map(Arrays::toString).collect(Collectors.toList());
		assertEquals(pSet.size(), sSet.size());

		assertTrue(new HashSet<>(pSet).containsAll(sSet));
		assertTrue(new HashSet<>(sSet).containsAll(pSet));
	}
}
