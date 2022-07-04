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
package org.spldev.formula.structure;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.atomic.literal.NamedTermMap.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Formula extends Tree<Formula> {
	String getName();

	Class<?> getType();

	void setVariableMap(VariableMap map);

	default Optional<VariableMap> getVariableMap() {
		return Trees.preOrderStream(this)
			.filter(n -> n instanceof ValueTerm)
			.map(n -> ((ValueTerm) n).getMap())
			.findAny();
	}

	@Override
	List<? extends Formula> getChildren();

	@Override
	Formula cloneNode();

	Object eval(List<?> values);

	public static boolean checkValues(int size, List<?> values) {
		return values.size() == size;
	}

	public static boolean checkValues(Class<?> type, List<?> values) {
		return values.stream().allMatch(type::isInstance);
	}

	@SuppressWarnings("unchecked")
	public static <T> T reduce(List<?> values, final BinaryOperator<T> binaryOperator) {
		if (values.stream().anyMatch(value -> value == null)) {
			return null;
		}
		return values.stream().map(l -> (T) l).reduce(binaryOperator).orElse(null);
	}

}
