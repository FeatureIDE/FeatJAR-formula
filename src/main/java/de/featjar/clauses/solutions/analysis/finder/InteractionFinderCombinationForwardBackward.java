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
import de.featjar.clauses.solutions.analysis.InteractionFinderCombination;
import java.util.LinkedHashSet;
import java.util.List;

public class InteractionFinderCombinationForwardBackward extends InteractionFinderCombination {

    public InteractionFinderCombinationForwardBackward() {
        super(new SingleInteractionFinder());
    }

    public List<LiteralList> find(int t) {
        @SuppressWarnings("unchecked")
        List<LiteralList>[] results = new List[t];
        LiteralList[] mergedResults = new LiteralList[t];
        for (int ti = 1; ti <= t; ++ti) {
            List<LiteralList> res = finder.find(ti);
            if (res != null) {
                mergedResults[ti - 1] = LiteralList.merge(res);
                results[ti - 1] = res;
            }
        }

        int lastI = -1;

        loop:
        for (int i = t - 1; i >= 0; --i) {
            if (mergedResults[i] != null) {
                if (lastI == -1) {
                    lastI = i;
                } else {
                    final LiteralList lastMergedResult = mergedResults[lastI];
                    final LiteralList curMergedResult = mergedResults[i];
                    if (lastMergedResult.containsAll(curMergedResult)) {
                        if (!curMergedResult.containsAll(lastMergedResult)) {
                            final LinkedHashSet<LiteralList> exclude = new LinkedHashSet<>();
                            for (LiteralList r : results[lastI]) {
                                final LiteralList removeAll = r.removeAll(curMergedResult);
                                if (removeAll.isEmpty()) {
                                    continue loop;
                                }
                                exclude.add(removeAll);
                            }
                            final LiteralList complete = finder.complete(curMergedResult, exclude);
                            if (complete != null && finder.verify(complete)) {
                                return results[lastI];
                            }
                        }
                        lastI = i;
                    } else {
                        return results[lastI];
                    }
                }
            }
        }

        List<LiteralList> result = lastI == -1 ? null : results[lastI];
        if (!finder.isPotentialInteraction(result)) {
            return null;
        }
        finder.addStatisticEntry(result);
        return result;
    }
}
