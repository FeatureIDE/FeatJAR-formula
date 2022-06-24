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
package org.spldev.formula.structure.compound;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;

/**
 * A logical connector that is {@code true} iff all of its children are
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class And extends Compound {

	private static class EmptyAnd extends And {
		private VariableMap variableMap;

		public EmptyAnd(VariableMap variableMap) {
			this.variableMap = variableMap;
		}

		@Override
		public void setVariableMap(VariableMap variableMap) {
			this.variableMap = variableMap;
		}

		@Override
		public VariableMap getVariableMap() {
			return variableMap;
		}

		@Override
		public And cloneNode() {
			return new EmptyAnd(variableMap);
		}

		@Override
		protected int computeHashCode() {
			return Objects.hash(And.class, 0);
		}
	}

	public static And empty() {
		return new EmptyAnd(VariableMap.emptyMap());
	}

	public static And empty(VariableMap variableMap) {
		return new EmptyAnd(variableMap);
	}

	public And(Compose compose, List<? extends Formula> nodes) {
		super(compose, nodes);
	}

	public And(List<? extends Formula> nodes) {
		super(nodes);
	}

	public And(Compose compose, Formula... nodes) {
		super(compose, nodes);
	}

	public And(Expression... nodes) {
		super(nodes);
	}

	private And() {
		super();
	}

	@Override
	public And cloneNode() {
		return new And();
	}

	@Override
	public String getName() {
		return "and";
	}

}
