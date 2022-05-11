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
 * A logical connector that is {@code true} iff at least one of its children is
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Or extends Compound {

	private static class EmptyOr extends Or {
		private VariableMap variableMap;

		public EmptyOr(VariableMap variableMap) {
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
		public Or cloneNode() {
			return new EmptyOr(variableMap);
		}

		@Override
		protected int computeHashCode() {
			return Objects.hash(Or.class, 0);
		}
	}

	public static Or empty() {
		return new EmptyOr(VariableMap.emptyMap());
	}

	public static Or empty(VariableMap variableMap) {
		return new EmptyOr(variableMap);
	}

	public Or(Collection<? extends Formula> nodes) {
		super(nodes);
	}

	public Or(Formula... nodes) {
		super(nodes);
	}

	private Or() {
		super();
	}

	@Override
	public Or cloneNode() {
		return new Or();
	}

	@Override
	public String getName() {
		return "or";
	}

}
