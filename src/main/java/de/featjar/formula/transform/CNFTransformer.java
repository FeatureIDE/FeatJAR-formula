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
package de.featjar.formula.transform;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.tmp.Formulas;
import de.featjar.formula.tmp.TermMap;
import de.featjar.formula.tmp.TermMap.Variable;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.CancelableMonitor;
import de.featjar.base.tree.Trees;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CNFTransformer implements Transformer {

    public final boolean useMultipleThreads = false;

    protected final List<Expression> distributiveClauses;
    protected final List<TseitinTransformer.Substitute> tseitinClauses;
    protected boolean useDistributive;
    protected int maximumNumberOfLiterals = Integer.MAX_VALUE;

    protected TermMap termMap = null;

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
    public Result<Expression> execute(Expression orgExpression, Monitor monitor) {
        useDistributive = (maximumNumberOfLiterals > 0);
        final NFTester nfTester = NormalForms.getNFTester(orgExpression, NormalForms.NormalForm.CNF);
        if (nfTester.isNf) {
            if (!nfTester.isClausalNf()) {
                return Result.of(NormalForms.toClausalNF(Trees.clone(orgExpression), NormalForms.NormalForm.CNF));
            } else {
                return Result.of(Trees.clone(orgExpression));
            }
        }
        termMap = orgExpression.getTermMap().map(TermMap::clone).orElseGet(TermMap::new);
        Expression expression = NormalForms.simplifyForNF(Trees.clone(orgExpression));
        if (expression instanceof And) {
            final List<Expression> children = ((And) expression).getChildren();
            if (useMultipleThreads) {
                children.parallelStream().forEach(this::transform);
            } else {
                children.forEach(this::transform);
            }
        } else {
            transform(expression);
        }

        expression = new And(getTransformedClauses());
        expression = NormalForms.toClausalNF(expression, NormalForms.NormalForm.CNF);
        expression = Formulas.manipulate(expression, new VariableMapSetter(termMap));
        return Result.of(expression);
    }

    protected List<? extends Expression> getTransformedClauses() {
        final List<Expression> transformedClauses = new ArrayList<>();

        transformedClauses.addAll(distributiveClauses);

        if (!tseitinClauses.isEmpty()) {
            termMap = termMap.clone();
            final HashMap<TseitinTransformer.Substitute, TseitinTransformer.Substitute> combinedTseitinClauses = new HashMap<>();
            for (final TseitinTransformer.Substitute tseitinClause : tseitinClauses) {
                TseitinTransformer.Substitute substitute = combinedTseitinClauses.get(tseitinClause);
                if (substitute == null) {
                    combinedTseitinClauses.put(tseitinClause, tseitinClause);
                    final Variable variable = tseitinClause.getVariable();
                    if (variable != null) {
                        variable.rename(termMap.addBooleanVariable().getName());
                    }
                } else {
                    final Variable variable = substitute.getVariable();
                    if (variable != null) {
                        tseitinClause.getVariable().rename(variable.getName());
                    }
                }
            }
            for (final TseitinTransformer.Substitute tseitinClause : combinedTseitinClauses.keySet()) {
                for (final Expression expression : tseitinClause.getClauses()) {
                    transformedClauses.add(Formulas.manipulate(expression, new VariableMapSetter(termMap)));
                }
            }
        }
        return transformedClauses;
    }

    private void transform(Expression child) {
        final Expression clonedChild = Trees.clone(child);
        if (Formulas.isCNF(clonedChild)) {
            if (clonedChild instanceof And) {
                distributiveClauses.addAll(((And) clonedChild).getChildren());
            } else {
                distributiveClauses.add(clonedChild);
            }
        } else {
            if (useDistributive) {
                try {
                    distributiveClauses.addAll(
                            distributive(clonedChild, new CancelableMonitor()).get().getChildren()); // todo .get?
                    return;
                } catch (final DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException e) {
                }
            }
            tseitinClauses.addAll(tseitin(clonedChild, new CancelableMonitor()).get()); // todo: .get?
        }
    }

    protected Result<Connective> distributive(Expression child, Monitor monitor)
            throws DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException {
        final CNFDistributiveLawTransformer cnfDistributiveLawTransformer = new CNFDistributiveLawTransformer();
        cnfDistributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return cnfDistributiveLawTransformer.execute(child, monitor);
    }

    protected Result<List<TseitinTransformer.Substitute>> tseitin(Expression child, Monitor monitor) {
        final TseitinTransformer tseitinTransformer = new TseitinTransformer();
        tseitinTransformer.setVariableMap(new TermMap());
        return tseitinTransformer.execute(child, monitor);
    }
}
