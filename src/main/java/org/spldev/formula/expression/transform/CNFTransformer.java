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
package org.spldev.formula.expression.transform;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.bool.*;
import org.spldev.formula.expression.transform.DistributiveLawTransformer.*;
import org.spldev.formula.expression.transform.NormalForms.*;
import org.spldev.formula.expression.transform.TseytinTransformer.*;
import org.spldev.util.job.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.Trees.*;

public class CNFTransformer implements Transformer {

	public static final boolean useMultipleThreads = false;

	protected final List<Formula> distributiveClauses;
	protected final List<Substitute> tseytinClauses;
	protected final boolean useDistributive;
	protected final int maximumNumberOfClauses, maximumLengthOfClauses;

	protected VariableMap variableMap = null;

	public CNFTransformer() {
		this(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public CNFTransformer(int maximumNumberOfClauses, int maximumLengthOfClauses) {
		this.maximumNumberOfClauses = maximumNumberOfClauses;
		this.maximumLengthOfClauses = maximumLengthOfClauses;
		useDistributive = (maximumNumberOfClauses > 0) || (maximumLengthOfClauses > 0);
		if (useMultipleThreads) {
			distributiveClauses = Collections.synchronizedList(new ArrayList<>());
			tseytinClauses = Collections.synchronizedList(new ArrayList<>());
		} else {
			distributiveClauses = new ArrayList<>();
			tseytinClauses = new ArrayList<>();
		}
	}

	@Override
	public Formula execute(Formula orgFormula, InternalMonitor monitor) {
		final NFTester nfTester = NormalForms.getNFTester(orgFormula, NormalForm.CNF);
		if (nfTester.isNf) {
			if (!nfTester.isClausalNf()) {
				return NormalForms.toClausalNF(Trees.cloneTree(orgFormula), NormalForm.CNF);
			} else {
				return Trees.cloneTree(orgFormula);
			}
		}
		variableMap = VariableMap.fromExpression(orgFormula).clone();
		Formula formula = NormalForms.simplifyForNF(Trees.cloneTree(orgFormula));
		if (formula instanceof And) {
			final List<Formula> children = ((And) formula).getChildren();
			if (useMultipleThreads) {
				children.parallelStream().forEach(this::transform);
			} else {
				children.forEach(this::transform);
			}
		} else {
			transform(formula);
		}

		formula = new And(getTransformedClauses());
		formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
		formula.setVariableMap(variableMap);
		return formula;
	}

	protected Collection<? extends Formula> getTransformedClauses() {
		final List<Formula> transformedClauses = new ArrayList<>();

		transformedClauses.addAll(distributiveClauses);

		if (!tseytinClauses.isEmpty()) {
			variableMap = variableMap.clone();
			final HashMap<Substitute, Substitute> combinedTseytinClauses = new HashMap<>();
			int count = 0;
			for (final Substitute tseytinClause : tseytinClauses) {
				Substitute substitute = combinedTseytinClauses.get(tseytinClause);
				if (substitute == null) {
					substitute = tseytinClause;
					combinedTseytinClauses.put(substitute, substitute);
					final BoolVariable variable = substitute.getVariable();
					if (variable != null) {
						Optional<BoolVariable> addBooleanVariable;
						do {
							addBooleanVariable = variableMap.addBooleanVariable("__temp__" + count++);
						} while (addBooleanVariable.isEmpty());
						variable.getVariableMap().renameVariable(variable.getIndex(), addBooleanVariable.get()
							.getName());
					}
				} else {
					final BoolVariable variable = substitute.getVariable();
					if (variable != null) {
						final BoolVariable otherVariable = tseytinClause.getVariable();
						otherVariable.getVariableMap().renameVariable(otherVariable.getIndex(), variable.getName());
					}
				}
			}
			for (final Substitute tseytinClause : combinedTseytinClauses.keySet()) {
				for (final Formula formula : tseytinClause.getClauses()) {
					formula.adaptVariableMap(variableMap);
					transformedClauses.add(formula);
				}
			}
		}
		return transformedClauses;
	}

	private void transform(Formula child) {
		if (isCNF(child)) {
			if (child instanceof And) {
				distributiveClauses.addAll(Trees.cloneTree((And) child).getChildren());
			} else {
				distributiveClauses.add(Trees.cloneTree(child));
			}
		} else {
			if (useDistributive) {
				try {
					distributiveClauses.addAll(distributive(Trees.cloneTree(child), new NullMonitor()).getChildren());
				} catch (final TransformException e) {
					tseytinClauses.addAll(tseytin(Trees.cloneTree(child), new NullMonitor()));
				}
			} else {
				tseytinClauses.addAll(tseytin(Trees.cloneTree(child), new NullMonitor()));
			}
		}
	}

	protected Compound distributive(Formula child, InternalMonitor monitor)
		throws MaximumNumberOfClausesExceededException, MaximumLengthOfClausesExceededException {
		return new CNFDistributiveLawTransformer(maximumNumberOfClauses, maximumLengthOfClauses)
			.execute(child, monitor);
	}

	protected List<Substitute> tseytin(Formula child, InternalMonitor monitor) {
		final TseytinTransformer tseytinTransformer = new TseytinTransformer();
		tseytinTransformer.setVariableMap(VariableMap.emptyMap());
		return tseytinTransformer.execute(child, monitor);
	}

	private boolean isCNF(Formula child) {
		final CNFTester visitor = new CNFTester();
		try {
			Trees.dfsPrePost(child, visitor);
		} catch (final VisitorFailException e1) {
		}
		return visitor.isNf;
	}

}
