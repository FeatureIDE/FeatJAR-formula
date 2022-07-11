/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * -----------------------------------------------------------------------------
 */
package de.featjar.clauses;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import de.featjar.formula.io.FormulaFormatManager;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.data.Cache;
import de.featjar.util.data.Provider;
import de.featjar.util.data.Result;
import de.featjar.util.job.Executor;
import de.featjar.util.logging.Logger;
import de.featjar.formula.io.*;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.atomic.literal.*;
import de.featjar.formula.structure.compound.*;
import de.featjar.util.data.*;
import de.featjar.util.job.*;
import de.featjar.util.logging.*;

/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public final class Clauses {

	private Clauses() {
	}

	public static LiteralList getVariables(Collection<LiteralList> clauses) {
		return new LiteralList(clauses.stream().flatMapToInt(c -> Arrays.stream(c.getLiterals())).distinct().toArray());
	}

	public static LiteralList getLiterals(VariableMap variables) {
		return new LiteralList(IntStream.rangeClosed(1, variables.getVariableCount()).flatMap(i -> IntStream.of(-i, i))
			.toArray());
	}

	/**
	 * Negates all clauses in the list (applies De Morgan).
	 *
	 * @param clauses collection of clauses
	 * @return A newly construct {@code ClauseList}.
	 */
	public static Stream<LiteralList> negate(Collection<LiteralList> clauses) {
		return clauses.stream().map(LiteralList::negate);
	}

	public static Result<LiteralList> adapt(LiteralList clause, VariableMap oldVariables,
		VariableMap newVariables) {
		return clause.adapt(oldVariables, newVariables);
	}

	public static int adapt(int literal, VariableMap oldVariables,
		VariableMap newVariables) {
		final String name = oldVariables.getVariableName(Math.abs(literal)).orElse(null);
		final int index = newVariables.getVariableIndex(name).orElse(0);
		return literal < 0 ? -index : index;
	}

	public static CNF convertToCNF(Formula formula) {
		return Executor.run(new FormulaToCNF(), formula).get();
	}

	public static CNF convertToCNF(Formula formula, VariableMap variableMap) {
		final FormulaToCNF function = new FormulaToCNF();
		function.setVariableMapping(variableMap);
		return Executor.run(function, formula).get();
	}

	public static CNF convertToDNF(Formula formula) {
		final CNF cnf = Executor.run(new FormulaToCNF(), formula).get();
		return new CNF(cnf.getVariableMap(), convertNF(cnf.getClauses()));
	}

	public static CNF convertToDNF(Formula formula, VariableMap variableMap) {
		final FormulaToCNF function = new FormulaToCNF();
		function.setVariableMapping(variableMap);
		final CNF cnf = Executor.run(function, formula).get();
		return new CNF(variableMap, convertNF(cnf.getClauses()));
	}

	/**
	 * Converts CNF to DNF and vice-versa.
	 *
	 * @param clauses list of clauses
	 * @return A newly construct {@code ClauseList}.
	 */
	public static List<LiteralList> convertNF(List<LiteralList> clauses) {
		final List<LiteralList> convertedClauseList = new ArrayList<>();
		convertNF(clauses, convertedClauseList, new int[clauses.size()], 0);
		return convertedClauseList;
	}

	private static void convertNF(List<LiteralList> cnf, List<LiteralList> dnf, int[] literals, int index) {
		if (index == cnf.size()) {
			final int[] newClauseLiterals = new int[literals.length];
			int count = 0;
			for (final int literal : literals) {
				if (literal != 0) {
					newClauseLiterals[count++] = literal;
				}
			}
			if (count < newClauseLiterals.length) {
				dnf.add(new LiteralList(Arrays.copyOf(newClauseLiterals, count)));
			} else {
				dnf.add(new LiteralList(newClauseLiterals));
			}
		} else {
			final HashSet<Integer> literalSet = new HashSet<>();
			for (int i = 0; i <= index; i++) {
				literalSet.add(literals[i]);
			}
			int redundantCount = 0;
			final int[] literals2 = cnf.get(index).getLiterals();
			for (final int literal : literals2) {
				if (!literalSet.contains(-literal)) {
					if (!literalSet.contains(literal)) {
						literals[index] = literal;
						convertNF(cnf, dnf, literals, index + 1);
					} else {
						redundantCount++;
					}
				}
			}
			literals[index] = 0;
			if (redundantCount == literals2.length) {
				convertNF(cnf, dnf, literals, index + 1);
			}
		}
	}

	public static CNF open(Path path) {
		return Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF).orElse(
			Logger::logProblems);
	}

	public static Result<CNF> load(Path path) {
		return Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF);
	}

	public static Result<CNF> load(Path path, Cache cache) {
		return cache.get(CNFProvider.loader(path));
	}

	public static Cache createCache(Path path) {
		final Cache cache = new Cache();
		cache.set(CNFProvider.loader(path));
		return cache;
	}

	public static Cache createCache(CNF cnf) {
		final Cache cache = new Cache();
		cache.set(CNFProvider.of(cnf));
		return cache;
	}

	public static Or toOrClause(LiteralList clause, VariableMap variableMap) {
		return new Or(toLiterals(clause, variableMap));
	}

	public static And toAndClause(LiteralList clause, VariableMap variableMap) {
		return new And(toLiterals(clause, variableMap));
	}

	public static List<Literal> toLiterals(LiteralList clause, VariableMap variableMap) {
		return Arrays.stream(clause.getLiterals()).mapToObj(l -> variableMap.createLiteral(Math.abs(l), l > 0)).collect(
			Collectors.toList());
	}

}
