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
import de.featjar.clauses.LiteralList.Order;
import de.featjar.clauses.solutions.combinations.CombinationIterator;
import de.featjar.clauses.solutions.combinations.ParallelLexicographicIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class IncInteractionFinder implements InteractionFinder {

    private ConfigurationUpdater updater;
    private ConfigurationVerifyer verifier;
    private LiteralList core;

    private int configurationVerificationLimit = Integer.MAX_VALUE;

    private List<LiteralList> succeedingConfs;
    private List<LiteralList> failingConfs;

    private int verifyCounter;
    private LiteralList lastMerge;

    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
    }

    public ConfigurationUpdater getUpdater() {
        return updater;
    }

    public void setUpdater(ConfigurationUpdater updater) {
        this.updater = updater;
    }

    public ConfigurationVerifyer getVerifier() {
        return verifier;
    }

    public void setVerifier(ConfigurationVerifyer verifier) {
        this.verifier = verifier;
    }

    public LiteralList getCore() {
        return core;
    }

    public void setCore(LiteralList core) {
        this.core = core;
    }

    public int getConfigurationVerificationLimit() {
        return configurationVerificationLimit;
    }

    public void setConfigurationVerificationLimit(int configurationVerificationLimit) {
        this.configurationVerificationLimit = configurationVerificationLimit;
    }

    public List<LiteralList> getSample() {
        ArrayList<LiteralList> sample = new ArrayList<>(succeedingConfs.size() + failingConfs.size());
        sample.addAll(succeedingConfs);
        sample.addAll(failingConfs);
        return sample;
    }

    public int getConfigurationCount() {
        return succeedingConfs.size() + failingConfs.size();
    }

    public void addConfigurations(List<LiteralList> configurations) {
        configurations.forEach(this::verify);
    }

    protected List<int[]> computePotentialInteractions(int t) {
        final Iterator<LiteralList> iterator = failingConfs.iterator();
        LiteralList failingLiterals = iterator.next();
        while (iterator.hasNext()) {
            failingLiterals = iterator.next().retainAll(failingLiterals);
        }
        if (core != null) {
            failingLiterals = failingLiterals.removeAll(core);
        }

        final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);
        if (commonLiterals.size() < t) {
            return Arrays.asList(commonLiterals.getLiterals());
        }

        Stream<int[]> stream = ParallelLexicographicIterator.stream(t, commonLiterals.size()) //
                .map(index -> CombinationIterator.select(commonLiterals, index, new int[index.length]));
        List<int[]> interactions;
        if (lastMerge != null) {
            LiteralList lastLiterals = new LiteralList(lastMerge, Order.INDEX);
            if (lastLiterals.containsAll(failingLiterals)) {
                return null;
            }
            interactions = stream //
                    .filter(combo -> !lastLiterals.containsAll(combo)) //
                    .filter(combo -> !isCovered(combo)) //
                    .collect(Collectors.toList());
            interactions.add(lastMerge.getLiterals());
        } else {
            interactions = stream //
                    .filter(combo -> !isCovered(combo)) //
                    .collect(Collectors.toList());
        }
        return interactions;
    }

    private boolean isCovered(int[] combo) {
        for (LiteralList configuration : succeedingConfs) {
            if (configuration.containsAll(combo)) {
                return true;
            }
        }
        return false;
    }

    public boolean verify(LiteralList solution) {
        verifyCounter++;
        if (verifier.test(solution) == 0) {
            succeedingConfs.add(solution);
            return true;
        } else {
            failingConfs.add(solution);
            return false;
        }
    }

    public boolean isPotentialInteraction(List<LiteralList> interactions) {
        if (interactions == null) {
            return false;
        }
        final LiteralList merge = LiteralList.merge(interactions);
        final LiteralList testConfig = updater.complete(merge, null).orElse(null);
        if (testConfig == null || verify(testConfig)) {
            return false;
        }
        final LiteralList inverseConfig = updater.complete(null, List.of(merge)).orElse(null);
        return inverseConfig == null || verify(inverseConfig);
    }

    private Map<Boolean, List<int[]>> group(List<int[]> list, final LiteralList newConfig) {
        return list.stream()
                .collect(Collectors.groupingBy(i -> newConfig.containsAll(i), Collectors.toCollection(ArrayList::new)));
    }

    public List<LiteralList> find(int tmax) {
        verifyCounter = 0;
        lastMerge = null;

        @SuppressWarnings("unchecked")
        List<LiteralList>[] results = new List[tmax];
        LiteralList[] mergedResults = new LiteralList[tmax];
        for (int ti = 1; ti <= tmax; ++ti) {
            List<LiteralList> res = findT(ti);
            if (res != null) {
                mergedResults[ti - 1] = new LiteralList(lastMerge);
                results[ti - 1] = res;
            }
        }

        int lastI = -1;

        loop:
        for (int i = tmax - 1; i >= 0; --i) {
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
                            final LiteralList complete =
                                    updater.complete(curMergedResult, exclude).orElse(null);
                            if (complete != null && verify(complete)) {
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
        if (!isPotentialInteraction(result)) {
            return null;
        }
        return result;
    }

    public List<LiteralList> findT(int t) {
        if (lastMerge != null && lastMerge.size() <= t) {
            lastMerge = null;
        }

        List<int[]> curInteractionList = computePotentialInteractions(t);
        if (curInteractionList == null) {
            return null;
        }

        while (curInteractionList.size() > 1 //
                && verifyCounter < configurationVerificationLimit) {
            LiteralList bestConfig = updater.choose(curInteractionList);
            if (bestConfig == null) {
                break;
            }

            Map<Boolean, List<int[]>> partitions = group(curInteractionList, bestConfig);
            List<int[]> include = partitions.get(Boolean.TRUE);
            List<int[]> exclude = partitions.get(Boolean.FALSE);
            int diff = Math.abs(include.size() - exclude.size());
            int lastDiff = diff;

            while (diff > 1) {
                LiteralList config;
                if (include.size() > exclude.size()) {
                    config = updater.getConfig(null, exclude, include);
                    if (config == null) {
                        break;
                    }
                    partitions = group(include, config);
                    diff = Math.abs(
                            (exclude.size() + partitions.get(Boolean.FALSE).size())
                                    - partitions.get(Boolean.TRUE).size());
                    if (diff >= lastDiff) {
                        break;
                    }
                    exclude.addAll(partitions.get(Boolean.FALSE));
                    include = partitions.get(Boolean.TRUE);
                } else {
                    config = updater.getConfig(include, null, exclude);
                    if (config == null) {
                        break;
                    }
                    partitions = group(exclude, config);
                    diff = Math.abs(
                            (include.size() + partitions.get(Boolean.TRUE).size())
                                    - partitions.get(Boolean.FALSE).size());
                    if (diff >= lastDiff) {
                        break;
                    }
                    include.addAll(partitions.get(Boolean.TRUE));
                    exclude = partitions.get(Boolean.FALSE);
                }
                lastDiff = diff;
                bestConfig = config;
            }

            final boolean pass = verify(bestConfig);
            curInteractionList = pass ? exclude : include;
            if (lastMerge != null && pass == bestConfig.containsAll(lastMerge.getLiterals())) {
                lastMerge = null;
            }
        }

        if (curInteractionList.isEmpty()) {
            return null;
        } else {
            lastMerge =
                    LiteralList.merge(curInteractionList, failingConfs.get(0).size());
            return curInteractionList.stream().map(LiteralList::new).collect(Collectors.toList());
        }
    }

    public int getVerifyCounter() {
        return verifyCounter;
    }
}
