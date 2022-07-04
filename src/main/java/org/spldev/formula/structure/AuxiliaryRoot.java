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

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public class AuxiliaryRoot extends NonTerminal {

	public AuxiliaryRoot(Expression node) {
		super(node);
	}

	private AuxiliaryRoot() {
		super();
	}

	@Override
	public String getName() {
		return "";
	}

	public Expression getChild() {
		return getChildren().iterator().next();
	}

	public void setChild(Expression node) {
		Objects.requireNonNull(node);
		setChildren(Arrays.asList(node));
	}

	@Override
	public AuxiliaryRoot cloneNode() {
		return new AuxiliaryRoot();
	}

	@Override
	public Class<?> getType() {
		return getChild().getType();
	}

	@Override
	public Object eval(List<?> values) {
		return getChild().eval(values);
	}

}
