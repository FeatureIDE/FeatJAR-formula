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

import org.spldev.clauses.solutions.*;

public abstract class AggregatableMetrics {

	public final class DoubleMetric implements SampleMetric {
		private final String name;
		private final DoubleSupplier aggregate;

		public DoubleMetric(String name, DoubleSupplier aggregate) {
			this.name = name;
			this.aggregate = aggregate;
		}

		@Override
		public double get(SolutionList sample) {
			setSample(sample);
			return aggregate.getAsDouble();
		}

		@Override
		public String getName() {
			return name;
		}
	}

	protected static final double EMPTY = -2;
	protected static final double INVALID = -1;

	protected SolutionList sample;

	private double[] values = null;

	protected double min = EMPTY;
	protected double max = EMPTY;
	protected double mean = EMPTY;
	protected double median = EMPTY;
	protected double variance = EMPTY;
	protected double standardDeviation = EMPTY;

	public List<SampleMetric> getAllAggregates() {
		final List<SampleMetric> aggregates = new ArrayList<>(6);
		aggregates.add(getAggregate("min", this::getMin));
		aggregates.add(getAggregate("max", this::getMax));
		aggregates.add(getAggregate("mean", this::getMean));
		aggregates.add(getAggregate("median", this::getMedian));
		aggregates.add(getAggregate("variance", this::getVariance));
		aggregates.add(getAggregate("standardDeviation", this::getStandardDeviation));
		return aggregates;
	}

	public abstract SampleMetric getAggregate(String name, DoubleSupplier aggregate);

	public double[] getValues() {
		if (values == null) {
			values = computeValues();
		}
		return values;
	}

	protected abstract double[] computeValues();

	public void setSample(SolutionList sample) {
		if ((this.sample == null) || (this.sample != sample)) {
			this.sample = sample;
			reset();
		}
	}

	protected void reset() {
		values = null;
		min = EMPTY;
		max = EMPTY;
		mean = EMPTY;
		median = EMPTY;
		variance = EMPTY;
		standardDeviation = EMPTY;
	}

	protected double getMin() {
		if (min == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				min = INVALID;
			} else {
				min = Double.MAX_VALUE;
				for (final double count : values) {
					if (min > count) {
						min = count;
					}
				}
			}
		}
		return min;
	}

	protected double getMax() {
		if (max == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				max = INVALID;
			} else {
				max = 0;
				for (final double count : values) {
					if (max < count) {
						max = count;
					}
				}
			}
		}
		return max;
	}

	protected double getMean() {
		if (mean == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				mean = INVALID;
			} else {
				double sum = 0;
				for (final double count : values) {
					sum += count;
				}
				mean = sum / values.length;
			}
		}
		return mean;
	}

	protected double getMedian() {
		if (median == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				median = INVALID;
			} else {
				final double[] sortedCounts = Arrays.copyOf(values, values.length);
				Arrays.sort(sortedCounts);

				final int middle = sortedCounts.length / 2;
				median = ((sortedCounts.length % 2) != 0) //
					? sortedCounts[middle] //
					: (sortedCounts[middle - 1] + sortedCounts[middle]) / 2.0;
			}
		}
		return median;
	}

	protected double getVariance() {
		if (variance == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				variance = INVALID;
			} else {
				final double mean = getMean();
				variance = 0;
				for (final double count : values) {
					final double diff = count - mean;
					variance += diff * diff;
				}
				variance /= values.length;
			}
		}
		return variance;
	}

	protected double getStandardDeviation() {
		if (standardDeviation == EMPTY) {
			final double[] values = getValues();
			if (values.length == 0) {
				standardDeviation = INVALID;
			} else {
				standardDeviation = Math.sqrt(getVariance());
			}
		}
		return standardDeviation;
	}

}
