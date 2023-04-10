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
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    protected int creationCounter = 0;
    protected int verifyCounter = 0;
    protected int iterationCounter = 0;
    protected LiteralList lastMerge;

    protected ArrayList<Statistic> statistics;

    @Override
    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
        statistics = new ArrayList<>();
        creationCounter = 0;
        verifyCounter = 0;
        iterationCounter = 0;
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
    public List<LiteralList> getSample() {
        ArrayList<LiteralList> sample = new ArrayList<>(succeedingConfs.size() + failingConfs.size());
        sample.addAll(succeedingConfs);
        sample.addAll(failingConfs);
        return sample;
    }

    public int getConfigurationCount() {
        return succeedingConfs.size() + failingConfs.size();
    }

    public void addConfigurations(Collection<LiteralList> configurations) {
        configurations.forEach(this::verify);
    }

    public List<LiteralList> find(int t) {
        if (t <= 0) {
            statistics.add(new Statistic(0, 0, 0, 0, 0));
            return Collections.emptyList();
        }
        List<LiteralList> interactionsAll = computePotentialInteractions(t);
        List<LiteralList> curInteractionList;
        List<LiteralList> lastInteractionList;
        if (lastMerge != null) {
            LiteralList lastLiterals = (lastMerge != null) //
                    ? new LiteralList(lastMerge, Order.INDEX) //
                    : null;
            ConcurrentMap<Boolean, List<LiteralList>> partitions = interactionsAll.parallelStream()
                    .collect(Collectors.groupingByConcurrent(
                            i -> lastLiterals.containsAll(i), Collectors.toCollection(ArrayList::new)));
            lastInteractionList = partitions.get(Boolean.TRUE);
            curInteractionList = partitions.get(Boolean.FALSE);
            if (curInteractionList == null) {
                curInteractionList = new ArrayList<>();
            }
            curInteractionList.add(lastMerge);
        } else {
            curInteractionList = interactionsAll;
            lastInteractionList = null;
        }

        while (curInteractionList.size() > 1 //
                && verifyCounter < configurationVerificationLimit //
                && creationCounter < configurationCreationLimit) {
            addStatisticEntry(t, curInteractionList);
            iterationCounter++;

            final LiteralList configuration = findConfig(curInteractionList);

            if (configuration != null) {
                if (lastMerge != null) {
                    curInteractionList = curInteractionList.subList(0, curInteractionList.size() - 1);
                }
                final Predicate<? super LiteralList> predicate = verify(configuration)
                        ? literalList -> !configuration.containsAll(literalList)
                        : literalList -> configuration.containsAll(literalList);
                curInteractionList = curInteractionList.parallelStream()
                        .filter(predicate)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (lastMerge != null) {
                    if (predicate.test(lastMerge)) {
                        curInteractionList.add(lastMerge);
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
        addStatisticEntry(t, curInteractionList);

        lastMerge = LiteralList.merge(curInteractionList, failingConfs.get(0).size());

        return curInteractionList.isEmpty() ? null : curInteractionList;
    }

    protected void addStatisticEntry(int t, List<LiteralList> interactionsAll) {
        statistics.add(new Statistic(
                t,
                interactionsAll == null ? 0 : interactionsAll.size(),
                creationCounter,
                verifyCounter,
                iterationCounter));
    }

    protected abstract LiteralList findConfig(List<LiteralList> interactionsAll);

    protected List<LiteralList> computePotentialInteractions(int t) {
        Iterator<LiteralList> iterator = failingConfs.iterator();
        LiteralList failingLiterals = iterator.next();
        while (iterator.hasNext()) {
            failingLiterals = failingLiterals.retainAll(iterator.next());
        }
        if (core != null) {
            failingLiterals = failingLiterals.removeAll(core);
        }
        final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);

        if (commonLiterals.size() < t) {
            return Arrays.asList(commonLiterals);
        }

        return ParallelLexicographicIterator.stream(t, commonLiterals.size())
                .map(index -> CombinationIterator.select(commonLiterals, index, new int[index.length]))
                .filter(combo -> {
                    for (LiteralList configuration : succeedingConfs) {
                        if (configuration.containsAll(combo)) {
                            return false;
                        }
                    }
                    for (LiteralList configuration : failingConfs) {
                        if (configuration.containsAll(combo)) {
                            return true;
                        }
                    }
                    return false;
                }) //
                .map(literals -> new LiteralList(literals, Order.NATURAL, false)) //
                .collect(Collectors.toList());
    }

    protected LiteralList complete(LiteralList include, List<LiteralList> exclude) {
        creationCounter++;
        return updater.complete(include, exclude).orElse(null);
    }

    protected LiteralList choose(List<LiteralList> clauses) {
        creationCounter++;
        return updater.choose(clauses).orElse(null);
    }

    protected LiteralList complete(LiteralList include, Collection<LiteralList> exclude) {
        creationCounter++;
        return updater.complete(include, exclude).orElse(null);
    }

    protected boolean verify(LiteralList solution) {
        verifyCounter++;
        if (verifier.test(solution) == 0) {
            succeedingConfs.add(solution);
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

    protected LiteralList findBestConfig(List<LiteralList> interactionsAll, List<LiteralList> potentialConfs) {
        LiteralList bestConfig = null;
        double bestRatio = 0.5;
        for (LiteralList config : potentialConfs) {
            final double ratio = Math.abs(0.5
                    - (((double) interactionsAll.parallelStream()
                                    .filter(config::containsAll)
                                    .count())
                            / interactionsAll.size()));
            if (ratio < bestRatio) {
                bestRatio = ratio;
                bestConfig = config;
            }
        }
        return bestConfig;
    }

    protected boolean isPotentialInteraction(List<LiteralList> interaction) {
        if (interaction == null) {
            return false;
        }
        LiteralList merge =
                LiteralList.merge(interaction, this.failingConfs.get(0).size());
        LiteralList testConfig = complete(merge, null);
        if (testConfig == null || verify(testConfig)) {
            return false;
        }
        LiteralList inverseConfig = complete(null, List.of(merge));
        return inverseConfig == null || verify(inverseConfig);
    }
}
