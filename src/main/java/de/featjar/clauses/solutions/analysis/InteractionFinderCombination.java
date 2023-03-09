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
import java.util.Collection;
import java.util.List;

public abstract class InteractionFinderCombination implements InteractionFinder {

    protected final AInteractionFinder finder;

    public InteractionFinderCombination(AInteractionFinder finder) {
        this.finder = finder;
    }

    @Override
    public List<Statistic> getStatistics() {
        return finder.getStatistics();
    }

    @Override
    public List<LiteralList> getSample() {
        return finder.getSample();
    }

    @Override
    public void setConfigurationVerificationLimit(int configurationVerificationLimit) {
        finder.setConfigurationVerificationLimit(configurationVerificationLimit);
    }

    @Override
    public void setConfigurationCreationLimit(int configurationCreationLimit) {
        finder.setConfigurationCreationLimit(configurationCreationLimit);
    }

    @Override
    public ConfigurationUpdater getUpdater() {
        return finder.getUpdater();
    }

    @Override
    public ConfigurationVerifyer getVerifier() {
        return finder.getVerifier();
    }

    @Override
    public void setUpdater(ConfigurationUpdater updater) {
        finder.setUpdater(updater);
    }

    @Override
    public void setVerifier(ConfigurationVerifyer verifier) {
        finder.setVerifier(verifier);
    }

    @Override
    public LiteralList getCore() {
        return finder.getCore();
    }

    @Override
    public void setCore(LiteralList core) {
        finder.setCore(core);
    }

    @Override
    public void reset() {
        finder.reset();
    }

    @Override
    public void addConfigurations(Collection<LiteralList> configurations) {
        finder.addConfigurations(configurations);
    }
}
