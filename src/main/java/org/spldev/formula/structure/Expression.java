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

import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.term.*;
import org.spldev.util.tree.Trees;
import org.spldev.util.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @deprecated use {@link Formula} instead
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
@Deprecated
public interface Expression extends Tree<Expression> {

	String getName();

	default void setVariableMap(VariableMap map) {
		for (final Expression child : getChildren()) {
			child.setVariableMap(map);
		}
	}

	default void adaptVariableMap(VariableMap map) {
		for (final Expression child : getChildren()) {
			child.adaptVariableMap(map);
		}
	}

	// todo return Optional<VariableMap>
	default VariableMap getVariableMap() {
		return Trees.preOrderStream(this)
			.skip(1)
			.findAny()
			.map(Expression::getVariableMap)
			.orElseGet(VariableMap::emptyMap);
	}

	@Override
	List<? extends Expression> getChildren();

	@Override
	Expression cloneNode();

}
