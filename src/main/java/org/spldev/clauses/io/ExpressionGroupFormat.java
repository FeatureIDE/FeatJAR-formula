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
package org.spldev.clauses.io;

import java.util.*;

import org.spldev.clauses.*;
import org.spldev.util.data.*;
import org.spldev.util.io.InputMapper;
import org.spldev.util.io.format.*;

/**
 * Reads and writes grouped propositional expressions in CNF.
 *
 * @author Sebastian Krieter
 */
public class ExpressionGroupFormat implements Format<List<List<ClauseList>>> {

	public static final String ID = ExpressionGroupFormat.class.getSimpleName();

	@Override
	public String serialize(List<List<ClauseList>> expressionGroups) {
		final StringBuilder sb = new StringBuilder();
		for (final List<? extends ClauseList> expressionGroup : expressionGroups) {
			sb.append("g ");
			sb.append(expressionGroup.size());
			sb.append(System.lineSeparator());
			for (final ClauseList expression : expressionGroup) {
				sb.append("e ");
				for (final LiteralList literalSet : expression) {
					for (final int literal : literalSet.getLiterals()) {
						sb.append(literal);
						sb.append(" ");
					}
					sb.append("|");
				}
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	@Override
	public Result<List<List<ClauseList>>> parse(InputMapper inputMapper) {
		final ArrayList<List<ClauseList>> expressionGroups = new ArrayList<>();
		ArrayList<ClauseList> expressionGroup = null;
		final Iterator<String> lineIterator = inputMapper.get().getLineStream().iterator();
		int lineCount = 0;
		try {
			while (lineIterator.hasNext()) {
				final String line = lineIterator.next();
				lineCount++;
				final char firstChar = line.charAt(0);
				switch (firstChar) {
				case 'g':
					final int groupSize = Integer.parseInt(line.substring(2).trim());
					expressionGroup = new ArrayList<>(groupSize);
					expressionGroups.add(expressionGroup);
					break;
				case 'e':
					if (expressionGroup == null) {
						throw new Exception("No group defined.");
					}
					final String expressionString = line.substring(2).trim();
					final String[] clauseStrings = expressionString.split("\\|");
					final ClauseList expression = new ClauseList();
					for (final String clauseString : clauseStrings) {
						final String[] literalStrings = clauseString.split("\\s+");
						final int[] literals = new int[literalStrings.length];
						int index = 0;
						for (final String literalString : literalStrings) {
							if (!literalString.isEmpty()) {
								final int literal = Integer.parseInt(literalString);
								literals[index++] = literal;
							}
						}
						expression.add(new LiteralList(Arrays.copyOfRange(literals, 0, index)));
					}
					expressionGroup.add(expression);
					break;
				default:
					break;
				}
			}
		} catch (final Exception e) {
			return Result.empty(new ParseProblem(e, lineCount));
		}
		return Result.of(expressionGroups);
	}

	@Override
	public String getFileExtension() {
		return "expression";
	}

	@Override
	public ExpressionGroupFormat getInstance() {
		return this;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "Expression Groups";
	}

}
