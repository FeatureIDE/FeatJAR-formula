/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.analysis;

import java.util.List;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.Assignment;
import de.featjar.util.data.Cache;
import de.featjar.util.data.Provider;
import de.featjar.util.job.MonitorableFunction;

/**
 * Basic analysis interface.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public interface Analysis<T> extends MonitorableFunction<Cache, T>, Provider<T> {

	Assignment getAssumptions();

	List<Formula> getAssumedConstraints();

	void updateAssumptions();

}
