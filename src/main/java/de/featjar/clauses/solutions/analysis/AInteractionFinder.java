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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public abstract class AInteractionFinder implements InteractionFinder {

    private ConfigurationUpdater updater;
    private ConfigurationVerifyer verifier;
    private LiteralList core;

    protected int configurationVerificationLimit = Integer.MAX_VALUE;
    protected int configurationCreationLimit = Integer.MAX_VALUE;

    private List<LiteralList> succeedingConfs;
    private List<LiteralList> failingConfs;
    protected int creationCounter = 0;
    protected int verifyCounter = 0;
    protected int iterationCounter = 0;

    protected ArrayList<Statistic> statistics;

    @Override
    public void reset() {
        succeedingConfs = new ArrayList<>();
        failingConfs = new ArrayList<>();
        statistics = new ArrayList<>();
        creationCounter = 0;
        verifyCounter = 0;
        iterationCounter = 0;
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

        while (interactionsAll.size() > 1 //
                && verifyCounter < configurationVerificationLimit //
                && creationCounter < configurationCreationLimit) {
            statistics.add(
                    new Statistic(t, interactionsAll.size(), creationCounter, verifyCounter, iterationCounter++));

            final LiteralList configuration = findConfig(interactionsAll);

            if (configuration != null) {
                Stream<LiteralList> interactionStream = interactionsAll.parallelStream();
                interactionStream = verify(configuration) //
                        ? interactionStream.filter(combo -> !configuration.containsAll(combo)) //
                        : interactionStream.filter(combo -> configuration.containsAll(combo));
                interactionsAll = interactionStream.collect(Collectors.toList());
            } else {
                break;
            }
        }
        statistics.add(new Statistic(t, interactionsAll.size(), creationCounter, verifyCounter, iterationCounter));

        if (updater.complete(null, interactionsAll).filter(this::verify).isPresent()) {
            return interactionsAll;
        } else {
            return Collections.emptyList();
        }
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

    protected LiteralList complete(LiteralList include, LiteralList... exclude) {
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
            LiteralList config = complete(null);
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
}
