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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public abstract class AInteractionFinder implements InteractionFinder {

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
    protected int limit;
    protected int limitFactor = 0;
    protected LiteralList lastMerge;

    protected ArrayList<Statistic> statistics;

    @Override
    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
        statistics = new ArrayList<>();
        verifyCounter = 0;
        iterationCounter = 0;
        t = 0;
        limit = 0;
        lastMerge = null;
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
    public int getLimitFactor() {
        return limitFactor;
    }

    @Override
    public void setLimitFactor(int limitFactor) {
        this.limitFactor = limitFactor;
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
        configurations.forEach(this::verify);
    }

    public List<LiteralList> find(int t) {
        if (t <= 0) {
            statistics.add(new Statistic(0, 0, 0, 0));
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
                final Predicate<int[]> predicate = verify(configuration)
                        ? literalList -> !configuration.containsAll(literalList)
                        : literalList -> configuration.containsAll(literalList);
                curInteractionList = curInteractionList.parallelStream()
                        .filter(predicate)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (lastMerge != null && !predicate.test(lastMerge.getLiterals())) {
                    lastMerge = null;
                }
            } else {
                break;
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

    public void addStatisticEntry(List<?> interactionsAll) {
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
        LiteralList lastLiterals;
        if (lastMerge != null) {
            lastLiterals = new LiteralList(lastMerge, Order.INDEX);
            if (lastLiterals.containsAll(failingLiterals)) {
                return null;
            }
        } else {
            lastLiterals = null;
        }

        final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);
        if (commonLiterals.size() < t) {
            return Arrays.asList(commonLiterals.getLiterals());
        }

        Stream<int[]> stream = ParallelLexicographicIterator.stream(t, commonLiterals.size()) //
                .map(index -> CombinationIterator.select(commonLiterals, index, new int[index.length]));
        List<int[]> interactions;
        if (lastMerge != null) {
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
        if (interactions != null) {
            limit = limitFactor * (int) Math.ceil((Math.log(interactions.size()) / Math.log(2)) + 1);
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

    protected LiteralList complete(LiteralList include, List<LiteralList> exclude) {
        return updater.complete(include, exclude).orElse(null);
    }

    protected LiteralList choose(List<int[]> clauses) {
        return updater.choose(clauses);
    }

    protected LiteralList random() {
        return updater.random();
    }

    protected LiteralList getConfig(List<int[]> include, List<int[]> exclude, List<int[]> choose) {
        return updater.getConfig(include, exclude, choose);
    }

    public LiteralList complete(LiteralList include, Collection<LiteralList> exclude) {
        return updater.complete(include, exclude).orElse(null);
    }

    protected LiteralList update(LiteralList result) {
        return updater.update(result).map(c -> c.removeAll(core)).orElse(null);
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

    public LiteralList findBestConfig(List<int[]> interactions, List<LiteralList> potentialConfs) {
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

    public boolean isPotentialInteraction(List<LiteralList> interactions) {
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
}
