/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * -----------------------------------------------------------------------------
 */
package de.featjar.formula.structure.atomic;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import de.featjar.util.data.Pair;
import de.featjar.util.data.*;

public class IndexAssignment implements Assignment {

	protected final HashMap<Integer, Object> assignments = new HashMap<>();

	@Override
	public void set(int index, Object assignment) {
		if (index > 0) {
			if (assignment == null) {
				assignments.remove(index);
			} else {
				assignments.put(index, assignment);
			}
		}
	}

	@Override
	public Optional<Object> get(int index) {
		return Optional.ofNullable(assignments.get(index));
	}

	@Override
	public List<Pair<Integer, Object>> getAll() {
		return assignments.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue()))
			.collect(Collectors.toList());
	}

	@Override
	public void unsetAll() {
		assignments.clear();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<Integer, Object> entry : assignments.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

}
