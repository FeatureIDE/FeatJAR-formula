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
import java.util.List;

public class InteractionFinderCombinationForwardBackward extends InteractionFinderCombination {

    public InteractionFinderCombinationForwardBackward() {
        super(new SingleInteractionFinder());
    }

    public List<LiteralList> find(int t) {
        List<List<LiteralList>> results = new ArrayList<>(t);
        for (int ti = 0; ti <= t; ti++) {
            results.add(finder.find(ti));
        }
        List<LiteralList> lastResult = null;
        for (int i = results.size() - 1; i >= 0; i--) {
            final List<LiteralList> result = results.get(i);
            if (result.isEmpty()) {
                return lastResult == null ? new ArrayList<>() : lastResult;
            } else {
                if (lastResult == null) {
                    lastResult = result;
                } else {
                    LiteralList merge1 = finder.update(LiteralList.merge(result));
                    LiteralList merge2 = finder.update(LiteralList.merge(lastResult));
                    if (merge2.containsAll(merge1)) {
                        if (!merge1.containsAll(merge2)) {
                            LiteralList complete = finder.complete(merge1, merge2.removeAll(merge1));
                            if (complete != null && finder.verify(complete)) {
                                return lastResult;
                            }
                        }
                        lastResult = result;
                    } else {
                        return lastResult;
                    }
                }
            }
        }
        return lastResult == null ? new ArrayList<>() : lastResult;
    }
}
