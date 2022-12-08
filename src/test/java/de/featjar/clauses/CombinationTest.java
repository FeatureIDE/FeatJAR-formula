/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.clauses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.clauses.solutions.combinations.LexicographicIterator;
import de.featjar.clauses.solutions.combinations.ParallelLexicographicIterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class CombinationTest {

    @Test
    public void combinationIteration() {
        testForNandT(1, 20);
        testForNandT(2, 20);
        testForNandT(3, 20);
    }

    private void testForNandT(int t, int n) {
        List<String> pSet =
                ParallelLexicographicIterator.stream(t, n).map(Arrays::toString).collect(Collectors.toList());
        List<String> sSet =
                LexicographicIterator.stream(t, n).map(Arrays::toString).collect(Collectors.toList());
        assertEquals(pSet.size(), sSet.size());

        assertTrue(new HashSet<>(pSet).containsAll(sSet));
        assertTrue(new HashSet<>(sSet).containsAll(pSet));
    }
}
