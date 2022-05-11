/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.clauses.solutions.metrics;

/**
 * Computes the Hamming distance between two literal arrays.
 *
 * @author Sebastian Krieter
 */
public class HammingDistance implements DistanceFunction {

	@Override
	public double computeDistance(final int[] literals1, final int[] literals2) {
		double conflicts = 0;
		for (int k = 0; k < literals1.length; k++) {
			conflicts += (literals1[k] != literals2[k]) ? 1 : 0;
		}
		return conflicts / literals1.length;
	}

	@Override
	public String getName() {
		return "Hamming";
	}

}
