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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.analysis.solver;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Arbitrary assumptions organized in a stack.
 *
 * @param <T> the type of the assumptions
 *
 * @author Sebastian Krieter
 */
public class AssumptionStack<T> implements Assumptions<T> {

    protected final ArrayDeque<T> assumptions;

    public Deque<T> getAssumptions() {
        return assumptions;
    }

    public AssumptionStack() {
        assumptions = new ArrayDeque<>();
    }

    public AssumptionStack(int size) {
        assumptions = new ArrayDeque<>(size);
    }

    protected AssumptionStack(AssumptionStack<T> oldAssumptions) {
        assumptions = new ArrayDeque<>(oldAssumptions.assumptions);
    }

    @Override
    public void clear() {
        assumptions.clear();
    }

    @Override
    public T pop() {
        return assumptions.pop();
    }

    @Override
    public void push(T var) {
        assumptions.push(var);
    }

    @Override
    public int size() {
        return assumptions.size();
    }

    @Override
    public T peek() {
        return assumptions.peek();
    }
}
