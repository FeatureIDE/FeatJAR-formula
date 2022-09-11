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
package de.featjar.formula.structure.transform;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.atomic.literal.VariableMap.Variable;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Compound;
import de.featjar.formula.structure.transform.DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException;
import de.featjar.formula.structure.transform.NormalForms.NormalForm;
import de.featjar.formula.structure.transform.TseytinTransformer.Substitute;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.CancelableMonitor;
import de.featjar.base.tree.Trees;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CNFTransformer implements Transformer {

    public final boolean useMultipleThreads = false;

    protected final List<Formula> distributiveClauses;
    protected final List<Substitute> tseytinClauses;
    protected boolean useDistributive;
    protected int maximumNumberOfLiterals = Integer.MAX_VALUE;

    protected VariableMap variableMap = null;

    public CNFTransformer() {
        if (useMultipleThreads) {
            distributiveClauses = Collections.synchronizedList(new ArrayList<>());
            tseytinClauses = Collections.synchronizedList(new ArrayList<>());
        } else {
            distributiveClauses = new ArrayList<>();
            tseytinClauses = new ArrayList<>();
        }
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Formula execute(Formula orgFormula, Monitor monitor) {
        useDistributive = (maximumNumberOfLiterals > 0);
        final NFTester nfTester = NormalForms.getNFTester(orgFormula, NormalForm.CNF);
        if (nfTester.isNf) {
            if (!nfTester.isClausalNf()) {
                return NormalForms.toClausalNF(Trees.clone(orgFormula), NormalForm.CNF);
            } else {
                return Trees.clone(orgFormula);
            }
        }
        variableMap = orgFormula.getVariableMap().map(VariableMap::clone).orElseGet(VariableMap::new);
        Formula formula = NormalForms.simplifyForNF(Trees.clone(orgFormula));
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
        formula = Formulas.manipulate(formula, new VariableMapSetter(variableMap));
        return formula;
    }

    protected List<? extends Formula> getTransformedClauses() {
        final List<Formula> transformedClauses = new ArrayList<>();

        transformedClauses.addAll(distributiveClauses);

        if (!tseytinClauses.isEmpty()) {
            variableMap = variableMap.clone();
            final HashMap<Substitute, Substitute> combinedTseytinClauses = new HashMap<>();
            for (final Substitute tseytinClause : tseytinClauses) {
                Substitute substitute = combinedTseytinClauses.get(tseytinClause);
                if (substitute == null) {
                    combinedTseytinClauses.put(tseytinClause, tseytinClause);
                    final Variable variable = tseytinClause.getVariable();
                    if (variable != null) {
                        variable.rename(variableMap.addBooleanVariable().getName());
                    }
                } else {
                    final Variable variable = substitute.getVariable();
                    if (variable != null) {
                        tseytinClause.getVariable().rename(variable.getName());
                    }
                }
            }
            for (final Substitute tseytinClause : combinedTseytinClauses.keySet()) {
                for (final Formula formula : tseytinClause.getClauses()) {
                    transformedClauses.add(Formulas.manipulate(formula, new VariableMapSetter(variableMap)));
                }
            }
        }
        return transformedClauses;
    }

    private void transform(Formula child) {
        final Formula clonedChild = Trees.clone(child);
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
                            distributive(clonedChild, new CancelableMonitor()).getChildren());
                    return;
                } catch (final MaximumNumberOfLiteralsExceededException e) {
                }
            }
            tseytinClauses.addAll(tseytin(clonedChild, new CancelableMonitor()));
        }
    }

    protected Compound distributive(Formula child, Monitor monitor)
            throws MaximumNumberOfLiteralsExceededException {
        final CNFDistributiveLawTransformer cnfDistributiveLawTransformer = new CNFDistributiveLawTransformer();
        cnfDistributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return cnfDistributiveLawTransformer.execute(child, monitor);
    }

    protected List<Substitute> tseytin(Formula child, Monitor monitor) {
        final TseytinTransformer tseytinTransformer = new TseytinTransformer();
        tseytinTransformer.setVariableMap(new VariableMap());
        return tseytinTransformer.execute(child, monitor);
    }
}
