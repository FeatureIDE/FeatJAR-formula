/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
package de.featjar.formula.assignment.metrics;

/**
 * Computes the Euclidean distance between two literal arrays. Negative literals
 * are treated as zero, positive literals as one.
 *
 * @author Sebastian Krieter
 */
public class EuclideanDistance implements IDistanceFunction {

    @Override
    public double computeDistance(final int[] literals1, final int[] literals2) {
        double conflicts = 0;
        for (int k = 0; k < literals1.length; k++) {
            conflicts += (literals1[k] != literals2[k]) ? 1 : 0;
        }
        return Math.sqrt(conflicts);
    }

    @Override
    public String getName() {
        return "Euclidean";
    }
}
