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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InteractionFinderCombinationForwardBackward extends InteractionFinderCombination {

    public InteractionFinderCombinationForwardBackward() {
        super(new SingleInteractionFinder());
    }

    public List<LiteralList> find(int t) {
        List<List<LiteralList>> results = new ArrayList<>(t);
        List<LiteralList> mergedResults = new ArrayList<>(t);
        for (int ti = 0; ti <= t; ti++) {
            results.add(finder.find(ti));
            mergedResults.add(LiteralList.merge(results.get(ti)));
        }

        boolean[] forwardResults = new boolean[t + 1];
        for (int i = 0; i <= t; i++) {
            LiteralList inverseConfig = finder.complete(null, results.get(i));
            if (inverseConfig != null) {
                if (finder.verify(inverseConfig)) {
                    forwardResults[i] = true;
                }
            } else {
                break;
            }
        }

        int lastI = -1;

        for (int i = t; i >= 1; i--) {
            if (lastI == -1 && forwardResults[i]) {
                lastI = i;
            } else {
                if (forwardResults[i]) {
                    LiteralList lastMergedResult = mergedResults.get(lastI);
                    LiteralList curMergedResult = mergedResults.get(i);
                    if (lastMergedResult.containsAll(curMergedResult)) {
                        if (!curMergedResult.containsAll(lastMergedResult)) {
                            LiteralList complete =
                                    finder.complete(curMergedResult, lastMergedResult.removeAll(curMergedResult));
                            if (complete == null) {
                                if (!finder.update(curMergedResult).containsAll(lastMergedResult)) {
                                    return results.get(lastI);
                                }
                            } else if (finder.verify(complete)) {
                                return results.get(lastI);
                            }
                        }
                        lastI = i;
                    } else {
                        return results.get(lastI);
                    }
                }
            }
        }
        return lastI == -1 ? Collections.emptyList() : results.get(lastI);
    }
}
