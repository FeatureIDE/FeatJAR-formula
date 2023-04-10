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
package de.featjar.clauses.solutions.analysis;

import de.featjar.clauses.LiteralList;
import java.util.LinkedHashSet;
import java.util.List;

public class InteractionFinderCombinationForwardBackward extends InteractionFinderCombination {

    public InteractionFinderCombinationForwardBackward() {
        super(new SingleInteractionFinder());
    }

    public List<LiteralList> find(int t) {
        @SuppressWarnings("unchecked")
        List<LiteralList>[] results = new List[t + 1];
        for (int ti = 1; ti <= t; ti++) {
            results[ti] = finder.find(ti);
        }

        LiteralList[] mergedResults = new LiteralList[t + 1];
        for (int ti = 1; ti <= t; ti++) {
            final List<LiteralList> res = results[ti];
            if (finder.isPotentialInteraction(res)) {
                mergedResults[ti] =
                        LiteralList.merge(res, finder.failingConfs.get(0).size());
            } else {
                results[ti] = null;
            }
        }

        int lastI = -1;

        for (int i = t; i >= 1; i--) {
            if (lastI == -1 && mergedResults[i] != null) {
                lastI = i;
            } else {
                if (mergedResults[i] != null) {
                    LiteralList lastMergedResult = mergedResults[lastI];
                    LiteralList curMergedResult = mergedResults[i];
                    if (lastMergedResult.containsAll(curMergedResult)) {
                        if (!curMergedResult.containsAll(lastMergedResult)) {
                            LinkedHashSet<LiteralList> exclude = new LinkedHashSet<>();
                            for (LiteralList r : results[lastI]) {
                                LiteralList removeAll = r.removeAll(curMergedResult);
                                if (removeAll != null) {
                                    exclude.add(removeAll);
                                } else {
                                    exclude = null;
                                    break;
                                }
                            }
                            if (exclude != null) {
                                LiteralList complete = finder.complete(curMergedResult, exclude);
                                if (complete != null && finder.verify(complete)) {
                                    return results[lastI];
                                }
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
        finder.addStatisticEntry(t, result);
        return result;
    }
}
