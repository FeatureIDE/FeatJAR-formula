/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.clauses.solutions.analysis.finder;

import de.featjar.clauses.LiteralList;
import de.featjar.clauses.solutions.analysis.AInteractionFinder;
import java.util.List;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class SingleInteractionFinderOld extends AInteractionFinder {

    protected LiteralList findConfig(List<int[]> interactions) {
        final int randomLimit = limit;

        final long middle = interactions.size();
        LiteralList bestConfig = null;
        long bestRatio = middle;
        int count = randomLimit;

        while (count > 0) {
            LiteralList config = random();
            final long includeCount =
                    interactions.parallelStream().filter(config::containsAll).count();
            final long ratio = Math.abs(middle - (2 * includeCount));

            if (ratio == 0) {
                return config;
            } else {
                count--;
                if (ratio != middle && (bestConfig == null || ratio < bestRatio)) {
                    bestConfig = config;
                    bestRatio = ratio;
                    count = randomLimit;
                }
            }
        }
        return bestConfig != null ? bestConfig : choose(interactions);
    }
}
