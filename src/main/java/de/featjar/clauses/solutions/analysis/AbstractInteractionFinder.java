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
import de.featjar.clauses.solutions.combinations.ParallelLexicographicIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Jens Meinicke
 * @author Sebastian Krieter
 *
 *         TODO how to detect A || B?
 */
public abstract class AbstractInteractionFinder implements InteractionFinder {

    private final SolutionUpdater updater;
    private final Predicate<LiteralList> verifier;

    private final List<LiteralList> succeedingConfs = new ArrayList<>();
    private final List<LiteralList> failingConfs = new ArrayList<>();
    private final ArrayList<Long> interactionCounter = new ArrayList<>();
    private int configCreationCounter = 0;
    private int verifyCounter = 0;

    private LiteralList core;

    public AbstractInteractionFinder(
            Collection<LiteralList> sample,
            SolutionUpdater configurationGenerator,
            Predicate<LiteralList> configurationChecker) {
        this.updater = configurationGenerator;
        this.verifier = configurationChecker;
        // HERE fill sets of valid and failing configs
        for (LiteralList configuration : sample) {
            verify(configuration);
        }
    }

    public void setCore(LiteralList core) {
        this.core = core;
    }

    public ArrayList<Long> getInteractionCounter() {
        return interactionCounter;
    }

    protected void addInteractionCount(long count) {
        interactionCounter.add(count);
    }

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

        final List<LiteralList> interactions = ParallelLexicographicIterator.stream(t, commonLiterals.size())
                .map(comboIndex -> {
                    int[] literals = new int[comboIndex.length];
                    for (int i = 0; i < comboIndex.length; i++) {
                        literals[i] = commonLiterals.get(comboIndex[i]);
                    }
                    return new LiteralList(literals, Order.NATURAL, false);
                })
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
                })
                .collect(Collectors.toList());
        return interactions;
    }

    public LiteralList complete(LiteralList include, LiteralList... exclude) {
        configCreationCounter++;
        return updater.complete(include, exclude).orElse(null);
    }

    @Override
    public boolean verify(LiteralList solution) {
        verifyCounter++;
        if (verifier.test(solution)) {
            succeedingConfs.add(solution);
            return true;
        } else {
            failingConfs.add(solution);
            return false;
        }
    }

    public LiteralList merge(List<LiteralList> result) {
        return LiteralList.merge(result);
    }

    public LiteralList update(LiteralList result) {
        return updater.update(result).map(c -> c.removeAll(core)).orElse(null);
    }

    public int getConfigurationCount() {
        return succeedingConfs.size() + failingConfs.size();
    }

    public LiteralList getCore() {
        return core;
    }

    public int getConfigCreationCount() {
        return configCreationCounter;
    }

    public int getVerifyCount() {
        return verifyCounter;
    }
}
