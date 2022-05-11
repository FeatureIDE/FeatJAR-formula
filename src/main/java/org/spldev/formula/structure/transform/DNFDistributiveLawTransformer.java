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
package org.spldev.formula.structure.transform;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.job.*;

/**
 * Transforms propositional formulas into CNF.
 *
 * @author Sebastian Krieter
 */
public class DNFDistributiveLawTransformer extends DistributiveLawTransformer {

	public DNFDistributiveLawTransformer() {
		super(And.class, And::new);
	}

	@Override
	public Compound execute(Formula formula, InternalMonitor monitor) throws MaximumNumberOfLiteralsExceededException {
		final Compound compound = (formula instanceof Or)
			? (Or) formula
			: new Or(formula);
		return super.execute(compound, monitor);
	}

}
