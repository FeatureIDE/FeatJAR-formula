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
package de.featjar.clauses.solutions.analysis.old;

import de.featjar.clauses.LiteralList;
import de.featjar.clauses.LiteralList.Order;
import de.featjar.clauses.solutions.analysis.ConfigurationUpdater;
import de.featjar.clauses.solutions.analysis.ConfigurationVerifyer;
import de.featjar.clauses.solutions.analysis.InteractionFinder;
import de.featjar.clauses.solutions.combinations.CombinationIterator;
import de.featjar.clauses.solutions.combinations.ParallelLexicographicIterator;
import de.featjar.util.data.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public abstract class AInteractionFinder2 implements InteractionFinder {

    protected ConfigurationUpdater updater;
    protected ConfigurationVerifyer verifier;
    protected LiteralList core;

    protected int configurationVerificationLimit = Integer.MAX_VALUE;
    protected int configurationCreationLimit = Integer.MAX_VALUE;

    protected List<LiteralList> succeedingConfs;
    protected List<LiteralList> failingConfs;
    protected int verifyCounter;
    protected int iterationCounter;
    protected int t;
    protected LiteralList lastMerge;

    protected ArrayList<Statistic> statistics;
    protected int size;
    private ArrayList<IntList> indexedSuccessSolutions;

    @Override
    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
        statistics = new ArrayList<>();
        verifyCounter = 0;
        iterationCounter = 0;
        t = 0;
        lastMerge = null;
        indexedSuccessSolutions = new ArrayList<>();
    }

    @Override
    public ConfigurationUpdater getUpdater() {
        return updater;
    }

    @Override
    public void setUpdater(ConfigurationUpdater updater) {
        this.updater = updater;
    }

    @Override
    public ConfigurationVerifyer getVerifier() {
        return verifier;
    }

    @Override
    public void setVerifier(ConfigurationVerifyer verifier) {
        this.verifier = verifier;
    }

    @Override
    public LiteralList getCore() {
        return core;
    }

    @Override
    public void setCore(LiteralList core) {
        this.core = core;
    }

    public int getConfigurationVerificationLimit() {
        return configurationVerificationLimit;
    }

    @Override
    public void setConfigurationVerificationLimit(int configurationVerificationLimit) {
        this.configurationVerificationLimit = configurationVerificationLimit;
    }

    public int getConfigurationCreationLimit() {
        return configurationCreationLimit;
    }

    @Override
    public void setConfigurationCreationLimit(int configurationCreationLimit) {
        this.configurationCreationLimit = configurationCreationLimit;
    }

    @Override
    public List<Statistic> getStatistics() {
        return statistics;
    }

    @Override
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
        size = configurations.get(0).size() + 1;
        for (int i = 0; i < 2 * size; i++) {
            indexedSuccessSolutions.add(new IntList());
        }
        configurations.forEach(this::verify);
    }

    public List<LiteralList> find(int t) {
        if (t <= 0) {
            statistics.add(new Statistic(0, 0, 0, 0));
            return Collections.emptyList();
        }
        this.t = t;

        List<int[]> curInteractionList, lastInteractionList;
        if (lastMerge != null && lastMerge.size() > t) {
            LiteralList lastLiterals = new LiteralList(lastMerge, Order.INDEX);

            curInteractionList = computePotentialInteractions();
            if (curInteractionList == null) {
                return null;
            }
            ConcurrentMap<Boolean, List<int[]>> partitions = curInteractionList.parallelStream()
                    .collect(Collectors.groupingByConcurrent(
                            i -> lastLiterals.containsAll(i), Collectors.toCollection(ArrayList::new)));
            lastInteractionList = partitions.get(Boolean.TRUE);
            curInteractionList = partitions.get(Boolean.FALSE);
            if (curInteractionList == null) {
                curInteractionList = new ArrayList<>();
            }
            curInteractionList.add(lastMerge.getLiterals());
        } else {
            lastMerge = null;
            lastInteractionList = null;
            curInteractionList = computePotentialInteractions();
            if (curInteractionList == null) {
                return null;
            }
        }

        while (curInteractionList.size() > 1 //
                && verifyCounter < configurationVerificationLimit) {
            iterationCounter++;

            final LiteralList configuration = findConfig(curInteractionList);

            if (configuration != null) {
                if (lastMerge != null) {
                    curInteractionList = curInteractionList.subList(0, curInteractionList.size() - 1);
                }
                final Predicate<int[]> predicate = verify(configuration)
                        ? literalList -> !configuration.containsAll(literalList)
                        : literalList -> configuration.containsAll(literalList);
                curInteractionList = curInteractionList.parallelStream()
                        .filter(predicate)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (lastMerge != null) {
                    if (predicate.test(lastMerge.getLiterals())) {
                        curInteractionList.add(lastMerge.getLiterals());
                    } else {
                        lastMerge = null;
                    }
                }
            } else {
                break;
            }
        }

        if (lastMerge != null) {
            curInteractionList.remove(curInteractionList.size() - 1);
            if (lastInteractionList != null) {
                curInteractionList.addAll(lastInteractionList);
            }
        }
        addStatisticEntry(curInteractionList);

        if (curInteractionList.isEmpty()) {
            return null;
        } else {
            lastMerge = LiteralList.mergeParallel(
                    curInteractionList, failingConfs.get(0).size());
            return curInteractionList.stream().map(LiteralList::new).collect(Collectors.toList());
        }
    }

    protected void addStatisticEntry(List<?> interactionsAll) {
        statistics.add(new Statistic(
                t, interactionsAll == null ? 0 : interactionsAll.size(), verifyCounter, iterationCounter));
    }

    protected abstract LiteralList findConfig(List<int[]> interactionsAll);

    protected List<int[]> computePotentialInteractions() {
        final Iterator<LiteralList> iterator = failingConfs.iterator();
        LiteralList failingLiterals = iterator.next();
        while (iterator.hasNext()) {
            failingLiterals = iterator.next().retainAll(failingLiterals);
        }
        if (core != null) {
            failingLiterals = failingLiterals.removeAll(core);
        }
        if (lastMerge != null && lastMerge.containsAll(failingLiterals)) {
            return null;
        }

        final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);
        if (commonLiterals.size() < t) {
            return Arrays.asList(commonLiterals.getLiterals());
        }

        return ParallelLexicographicIterator.stream(t, commonLiterals.size()) //
                .map(index -> CombinationIterator.select(commonLiterals, index, new int[index.length])) //
                .filter(combo -> !isCovered(combo)) //
                .collect(Collectors.toList());
    }

    private boolean isCovered(int[] combo) {
        for (LiteralList configuration : succeedingConfs) {
            if (configuration.containsAll(combo)) {
                return true;
            }
        }
        return false;
    }

    protected LiteralList complete(LiteralList include, List<LiteralList> exclude) {
        return updater.complete(include, exclude).orElse(null);
    }

    protected LiteralList choose(List<int[]> clauses) {
        return updater.choose(clauses);
    }

    protected LiteralList complete(LiteralList include, Collection<LiteralList> exclude) {
        return updater.complete(include, exclude).orElse(null);
    }

    protected boolean verify(LiteralList solution) {
        verifyCounter++;
        if (verifier.test(solution) == 0) {
            succeedingConfs.add(solution);
            addIndexSolutions(verifyCounter, solution, indexedSuccessSolutions);
            return true;
        } else {
            failingConfs.add(solution);
            return false;
        }
    }

    protected LiteralList update(LiteralList result) {
        return updater.update(result).map(c -> c.removeAll(core)).orElse(null);
    }

    protected List<LiteralList> getRandomConfigs(int numberOfConfigurations) {
        List<LiteralList> potentialConfs = new ArrayList<>();
        for (int i = 0; i < numberOfConfigurations; i++) {
            LiteralList config = complete(null, null);
            if (config == null) {
                break;
            }
            potentialConfs.add(config);
        }
        return potentialConfs;
    }

    protected LiteralList findBestConfig(List<int[]> interactions, List<LiteralList> potentialConfs) {
        if (potentialConfs.size() == 1) {
            return potentialConfs.get(0);
        }
        LiteralList bestConfig = null;
        long half = interactions.size();
        long bestRatio = half;
        for (LiteralList config : potentialConfs) {
            final long ratio = Math.abs(half
                    - (2
                            * interactions.parallelStream()
                                    .filter(config::containsAll)
                                    .count()));
            if (ratio < bestRatio) {
                bestRatio = ratio;
                bestConfig = config;
            }
        }

        return bestConfig;
    }

    protected boolean isPotentialInteraction(List<LiteralList> interactions) {
        if (interactions == null) {
            return false;
        }
        final LiteralList merge = LiteralList.merge(interactions);
        final LiteralList testConfig = complete(merge, null);
        if (testConfig == null || verify(testConfig)) {
            return false;
        }
        final LiteralList inverseConfig = complete(null, List.of(merge));
        return inverseConfig == null || verify(inverseConfig);
    }

    private void addIndexSolutions(int id, LiteralList config, ArrayList<IntList> indexedSolutions) {
        for (int literal : config.getLiterals()) {
            indexedSolutions.get(index(literal)).add(id);
        }
    }

    private int index(int literal) {
        return (size - literal);
    }

    private boolean isCovered(int[] literals, ArrayList<IntList> indexedSolutions) {
        if (t < 2) {
            return !indexedSolutions.get(index(literals[0])).isEmpty();
        }
        final IntList[] selectedIndexedSolutions = new IntList[t];
        for (int i = 0; i < t; i++) {
            final IntList indexedSolution = indexedSolutions.get(index(literals[i]));
            if (indexedSolution.size() == 0) {
                return false;
            }
            selectedIndexedSolutions[i] = indexedSolution;
        }
        Arrays.sort(selectedIndexedSolutions, Comparator.comparingInt(IntList::size));
        final int[] ix = new int[t - 1];

        final IntList i0 = selectedIndexedSolutions[0];
        final int[] ia0 = i0.toArray();
        loop:
        for (int i = 0; i < i0.size(); i++) {
            int id0 = ia0[i];
            for (int j = 1; j < t; j++) {
                final IntList i1 = selectedIndexedSolutions[j];
                final int binarySearch = Arrays.binarySearch(i1.toArray(), ix[j - 1], i1.size(), id0);
                if (binarySearch < 0) {
                    ix[j - 1] = -binarySearch - 1;
                    continue loop;
                } else {
                    ix[j - 1] = binarySearch;
                }
            }
            return true;
        }
        return false;
    }
}
