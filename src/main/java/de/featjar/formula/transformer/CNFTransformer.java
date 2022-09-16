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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.transformer;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.NormalFormTester;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.CancelableMonitor;
import de.featjar.base.tree.Trees;
import de.featjar.formula.visitor.NormalForms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Transforms a formula into conjunctive normal form.
 *
 * @author Sebastian Krieter
 */
public class CNFTransformer implements FormulaTransformer {

    public final boolean useMultipleThreads = false;

    protected final List<Formula> distributiveClauses;
    protected final List<TseitinTransformer.Substitute> tseitinClauses;
    protected boolean useDistributive;
    protected int maximumNumberOfLiterals = Integer.MAX_VALUE;

    public CNFTransformer() {
        if (useMultipleThreads) {
            distributiveClauses = Collections.synchronizedList(new ArrayList<>());
            tseitinClauses = Collections.synchronizedList(new ArrayList<>());
        } else {
            distributiveClauses = new ArrayList<>();
            tseitinClauses = new ArrayList<>();
        }
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Result<Formula> execute(Formula formula, Monitor monitor) {
        useDistributive = (maximumNumberOfLiterals > 0);
        final NormalFormTester normalFormTester = NormalForms.getNormalFormTester(formula, Formula.NormalForm.CNF);
        if (normalFormTester.isNormalForm()) {
            if (!normalFormTester.isClausalNormalForm()) {
                return Result.of(NormalForms.toClausalNormalForm((Formula) Trees.clone(formula), Formula.NormalForm.CNF));
            } else {
                return Result.of((Formula) Trees.clone(formula));
            }
        }
        Formula newFormula = new NNFTransformer().apply((Formula) Trees.clone(formula)).get(); // preferred input computation
        if (newFormula instanceof And) {
            final List<? extends Expression> children = newFormula.getChildren();
            if (useMultipleThreads) {
                children.parallelStream().forEach(child -> transform((Formula) child));
            } else {
                children.forEach(child -> transform((Formula) child));
            }
        } else {
            transform(newFormula);
        }

        newFormula = new And(getTransformedClauses());
        newFormula = NormalForms.toClausalNormalForm(newFormula, Formula.NormalForm.CNF);
        return Result.of(newFormula);
    }

    protected List<? extends Formula> getTransformedClauses() {
        final List<Formula> transformedClauses = new ArrayList<>(distributiveClauses);

        if (!tseitinClauses.isEmpty()) {
            final HashMap<TseitinTransformer.Substitute, TseitinTransformer.Substitute> combinedTseitinClauses = new HashMap<>();
            for (final TseitinTransformer.Substitute tseitinClause : tseitinClauses) {
                TseitinTransformer.Substitute substitute = combinedTseitinClauses.get(tseitinClause);
                if (substitute == null) {
                    combinedTseitinClauses.put(tseitinClause, tseitinClause);
                    final Variable variable = tseitinClause.getVariable();
                    if (variable != null) {
                        //todo variable.setName(termMap.addBooleanVariable().getName());
                    }
                } else {
                    final Variable variable = substitute.getVariable();
                    if (variable != null) {
                        //todo tseitinClause.getVariable().rename(variable.getName());
                    }
                }
            }
            for (final TseitinTransformer.Substitute tseitinClause : combinedTseitinClauses.keySet()) {
                for (final Expression expression : tseitinClause.getClauses()) {
                    //todo transformedClauses.add(Formulas.manipulate(expression, new VariableMapSetter(termMap)));
                }
            }
        }
        return transformedClauses;
    }

    private void transform(Formula child) {
        final Formula clonedChild = (Formula) Trees.clone(child);
        if ((clonedChild).isCNF()) {
            if (clonedChild instanceof And) {
                distributiveClauses.addAll((List<? extends Formula>) clonedChild.getChildren());
            } else {
                distributiveClauses.add(clonedChild);
            }
        } else {
            if (useDistributive) {
                try {
                    distributiveClauses.addAll(
                            (List<? extends Formula>) distributive(clonedChild, new CancelableMonitor()).get().getChildren()); // todo .get?
                    return;
                } catch (final DistributiveTransformer.MaximumNumberOfLiteralsExceededException ignored) {
                }
            }
            tseitinClauses.addAll(tseitin(clonedChild, new CancelableMonitor()).get()); // todo: .get?
        }
    }

    protected Result<Formula> distributive(Formula child, Monitor monitor)
            throws DistributiveTransformer.MaximumNumberOfLiteralsExceededException {
        final DistributiveTransformer cnfDistributiveLawTransformer = new DistributiveTransformer(Formula.NormalForm.CNF);
        cnfDistributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return cnfDistributiveLawTransformer.execute(child, monitor);
    }

    protected Result<List<TseitinTransformer.Substitute>> tseitin(Expression child, Monitor monitor) {
        final TseitinTransformer tseitinTransformer = new TseitinTransformer();
        return tseitinTransformer.execute(child, monitor);
    }
}
