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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class SingleInteractionFinder extends AInteractionFinder {

    protected List<int[]> include, exclude;

    public void reset() {
        super.reset();
        include = null;
        exclude = null;
    }

    protected LiteralList findInitialConfig(List<int[]> interactions) {
        final long middle = interactions.size();
        LiteralList bestConfig = null;
        long bestRatio = middle;
        int count = limit;

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
                    count = limit;
                }
            }
        }
        return bestConfig != null ? bestConfig : choose(interactions);
    }

    protected LiteralList findConfig(List<int[]> interactions) {
        include = null;
        exclude = null;
        LiteralList config = findInitialConfig(interactions);
        if (config == null) {
            return null;
        }
        Map<Boolean, List<int[]>> partitions = group(interactions, config);
        include = partitions.get(Boolean.TRUE);
        exclude = partitions.get(Boolean.FALSE);
        int diff = Math.abs(include.size() - exclude.size());
        if (diff <= 1) {
            return config;
        }

        int lastDiff = diff;
        LiteralList bestConfig = config;
        if (include.size() > exclude.size()) {
            while (diff > 1) {
                config = getConfig(null, exclude, include);
                if (config == null) {
                    break;
                }
                partitions = group(include, config);
                diff = Math.abs((exclude.size() + partitions.get(Boolean.FALSE).size())
                        - partitions.get(Boolean.TRUE).size());
                if (diff >= lastDiff) {
                    break;
                }
                lastDiff = diff;
                bestConfig = config;
                exclude.addAll(partitions.get(Boolean.FALSE));
                include = partitions.get(Boolean.TRUE);
            }
        } else {
            while (diff > 1) {
                config = getConfig(include, null, exclude);
                if (config == null) {
                    break;
                }
                partitions = group(exclude, config);
                diff = Math.abs((include.size() + partitions.get(Boolean.TRUE).size())
                        - partitions.get(Boolean.FALSE).size());
                if (diff >= lastDiff) {
                    break;
                }
                lastDiff = diff;
                bestConfig = config;
                include.addAll(partitions.get(Boolean.TRUE));
                exclude = partitions.get(Boolean.FALSE);
            }
        }
        return bestConfig;
    }

    private ConcurrentMap<Boolean, List<int[]>> group(List<int[]> list, final LiteralList newConfig) {
        return list.stream()
                .collect(Collectors.groupingByConcurrent(
                        i -> newConfig.containsAll(i), Collectors.toCollection(ArrayList::new)));
    }

    public List<LiteralList> find(int t) {
        if (t <= 0) {
            return Collections.emptyList();
        }
        this.t = t;

        if (lastMerge != null && lastMerge.size() <= t) {
            lastMerge = null;
        }

        List<int[]> curInteractionList = computePotentialInteractions();
        if (curInteractionList == null) {
            return null;
        }

        while (curInteractionList.size() > 1 //
                && verifyCounter < configurationVerificationLimit) {
            iterationCounter++;

            final LiteralList configuration = findConfig(curInteractionList);

            if (configuration != null) {
                final boolean pass = verify(configuration);
                curInteractionList = pass ? exclude : include;
                if (lastMerge != null && pass == configuration.containsAll(lastMerge.getLiterals())) {
                    lastMerge = null;
                }
            } else {
                break;
            }
        }

        if (curInteractionList.isEmpty()) {
            return null;
        } else {
            lastMerge = LiteralList.mergeParallel(
                    curInteractionList, failingConfs.get(0).size());
            return curInteractionList.stream().map(LiteralList::new).collect(Collectors.toList());
        }
    }
}
