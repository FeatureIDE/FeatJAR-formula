/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
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

import java.util.*;
import java.util.function.*;

import org.spldev.clauses.*;

public class CountMetrics extends AggregatableMetrics {

	private final CountFunction function;

	public CountMetrics(CountFunction function) {
		this.function = function;
	}

	public static List<SampleMetric> getAllAggregates(CountFunction function) {
		return new CountMetrics(function).getAllAggregates();
	}

	@Override
	protected double[] computeValues() {
		final List<LiteralList> solutions = sample.getSolutions();
		final int size = solutions.size();
		final double[] values = new double[size];
		for (int i = 0; i < (size - 1); i++) {
			values[i] = function.compute(solutions.get(i));
		}
		return values;
	}

	@Override
	public SampleMetric getAggregate(String name, DoubleSupplier aggregate) {
		return new DoubleMetric(function.getName() + "_count_" + name, aggregate);
	}

}
