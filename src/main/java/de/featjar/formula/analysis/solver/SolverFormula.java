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
package de.featjar.formula.analysis.solver;

import de.featjar.formula.structure.formula.Formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A solver-specific representation of a {@link de.featjar.formula.structure.formula.Formula}.
 * Is represented in proto-CNF (i.e., assumed to be a large conjunction).
 *
 * @param <T> the type of the solver formulas
 * @author Sebastian Krieter
 */
public abstract class SolverFormula<T> {

    protected final List<T> solverFormulas;

    public SolverFormula() {
        solverFormulas = new ArrayList<>();
    }

    protected SolverFormula(SolverFormula<T> oldFormula) {
        solverFormulas = new ArrayList<>(oldFormula.solverFormulas); // todo clone necessary?
    }

    public List<T> get() {
        return solverFormulas;
    }

    public int size() {
        return solverFormulas.size();
    }

    public T peek() {
        return solverFormulas.get(solverFormulas.size() - 1);
    }
    
    public abstract List<T> push(Formula solverFormula);

    public List<T> push(Collection<Formula> solverFormulas) {
        int addCount = 0;
        final ArrayList<T> solverFormulaList = new ArrayList<>(solverFormulas.size());
        for (final Formula solverFormula : solverFormulas) {
            try {
                push(solverFormula);
                addCount++;
            } catch (final Exception e) {
                pop(addCount);
                throw e;
            }
        }
        return solverFormulaList;
    }

    public T pop() {
        return remove(solverFormulas.size() - 1);
    }

    public void pop(int count) {
        if (count > size()) {
            count = size();
        }
        for (int i = 0; i < count; i++) {
            pop();
        }
    }

    public void remove(T solverFormula) {
        remove(solverFormulas.indexOf(solverFormula));
    }

    protected T remove(final int index) {
        return solverFormulas.remove(index);
    }
    
    public void clear() {
        pop(size());
    }
}
