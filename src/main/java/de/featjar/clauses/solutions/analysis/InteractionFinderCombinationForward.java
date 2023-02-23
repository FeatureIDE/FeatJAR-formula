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

public class InteractionFinderCombinationForward implements InteractionFinder {

    private final InteractionFinder finder;

    public InteractionFinderCombinationForward(InteractionFinder finder) {
        this.finder = finder;
    }

    public List<LiteralList> find(int maxT, int n) {
        List<LiteralList> lastResult = null;
        for (int t = 1; t <= maxT; t++) {
            final List<LiteralList> result = finder.find(t, n);
            if (result.isEmpty()) {
                return lastResult == null ? new ArrayList<>() : lastResult;
            } else {
                if (lastResult == null) {
                    lastResult = result;
                } else {
                    LiteralList merge1 = merge(lastResult);
                    LiteralList merge2 = merge(result);
                    if (merge2.containsAll(merge1)) {
                        if (merge1.containsAll(merge2)) {
                            return lastResult;
                        }
                        LiteralList update1 = update(merge1);
                        if (update1.containsAll(merge2)) {
                            return lastResult;
                        }
                        LiteralList update2 = update(merge2);
                        LiteralList removeAll = update2.removeAll(update1);
                        LiteralList complete = complete(update1, removeAll);
                        if (complete == null || !verify(complete)) {
                            return lastResult;
                        }
                    }
                    lastResult = result;
                }
            }
        }
        return lastResult == null ? new ArrayList<>() : lastResult;
    }

    @Override
    public boolean verify(LiteralList solution) {
        return finder.verify(solution);
    }

    @Override
    public void setCore(LiteralList coreDead) {
        finder.setCore(coreDead);
    }

    @Override
    public int getConfigurationCount() {
        return finder.getConfigurationCount();
    }

    @Override
    public List<?> getInteractionCounter() {
        return finder.getInteractionCounter();
    }

    @Override
    public LiteralList getCore() {
        return finder.getCore();
    }

    @Override
    public LiteralList merge(List<LiteralList> result) {
        return finder.merge(result);
    }

    @Override
    public LiteralList complete(LiteralList include, LiteralList... exclude) {
        return finder.complete(include, exclude);
    }

    @Override
    public LiteralList update(LiteralList result) {
        return finder.update(result);
    }

    @Override
    public int getConfigCreationCount() {
        return finder.getConfigCreationCount();
    }

    @Override
    public int getVerifyCount() {
        return finder.getVerifyCount();
    }
}
