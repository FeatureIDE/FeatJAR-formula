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

public class InteractionFinderCombinationForward extends InteractionFinderCombination {

    public InteractionFinderCombinationForward() {
        super(new SingleInteractionFinder());
    }

    public List<LiteralList> find(int t) {
        List<LiteralList> lastResult = null;
        for (int ti = 0; ti <= t; ti++) {
            final List<LiteralList> result = finder.find(ti);
            if (result.isEmpty()) {
                return lastResult == null ? new ArrayList<>() : lastResult;
            } else {
                if (lastResult == null) {
                    if (!result.isEmpty()) {
                        lastResult = result;
                    }
                } else {
                    LiteralList merge1 = LiteralList.merge(lastResult);
                    LiteralList merge2 = LiteralList.merge(result);
                    if (merge2.containsAll(merge1)) {
                        if (merge1.containsAll(merge2)) {
                            return lastResult;
                        }
                        LiteralList update1 = finder.update(merge1);
                        if (update1.containsAll(merge2)) {
                            return lastResult;
                        }
                        LiteralList update2 = finder.update(merge2);
                        LiteralList removeAll = update2.removeAll(update1);
                        LiteralList complete = finder.complete(update1, removeAll);
                        if (complete == null || !finder.verify(complete)) {
                            return lastResult;
                        }
                    }
                    lastResult = result;
                }
            }
        }
        return lastResult == null ? new ArrayList<>() : lastResult;
    }
}
