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
import java.util.List;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public interface InteractionFinder {

    public static class Statistic {
        private final int t;
        private final int interactionCounter;
        private final int verifyCounter;
        private final int iterationCounter;

        public Statistic(int t, int interactionCounter, int verifyCounter, int iterationCounter) {
            this.t = t;
            this.interactionCounter = interactionCounter;
            this.verifyCounter = verifyCounter;
            this.iterationCounter = iterationCounter;
        }

        public int getT() {
            return t;
        }

        public int getInteractionCounter() {
            return interactionCounter;
        }

        public int getVerifyCounter() {
            return verifyCounter;
        }

        public int getIterationCounter() {
            return iterationCounter;
        }
    }

    LiteralList getCore();

    ConfigurationVerifyer getVerifier();

    ConfigurationUpdater getUpdater();

    void setCore(LiteralList core);

    void setUpdater(ConfigurationUpdater updater);

    void setVerifier(ConfigurationVerifyer verifier);

    void setConfigurationVerificationLimit(int configurationVerificationLimit);

    void setConfigurationCreationLimit(int configurationCreationLimit);

    void addConfigurations(List<LiteralList> configurations);

    List<LiteralList> find(int t);

    List<Statistic> getStatistics();

    List<LiteralList> getSample();

    int getLimitFactor();

    void setLimitFactor(int limitFactor);

    void reset();
}
