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
package org.spldev.formula.io.xml;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.transform.*;
import org.spldev.util.io.format.*;
import org.spldev.util.tree.*;
import org.w3c.dom.*;

public class XmlFeatureModelCNFFormat extends XmlFeatureModelFormat {

	public static final String ID = XmlFeatureModelCNFFormat.class.getCanonicalName();

	public XmlFeatureModelCNFFormat() {
	}

	@Override
	protected Formula readDocument(Document doc) throws ParseException {
		map = VariableMap.emptyMap();
		final List<Element> elementList = getElement(doc, FEATURE_MODEL);
		if (elementList.size() == 1) {
			final Element e = elementList.get(0);
			parseStruct(getElement(e, STRUCT));
			final int crossTreeConstraintsIndex = constraints.size();
			parseConstraints(getElement(e, CONSTRAINTS));
			final List<Formula> crossTreeConstraints = constraints.subList(crossTreeConstraintsIndex,
				constraints.size());
			final List<Formula> cnfConstraints = new ArrayList<>(crossTreeConstraints);
			crossTreeConstraints.clear();
			constraints.addAll(cnfConstraints);
		} else if (elementList.isEmpty()) {
			throw new ParseException("Not a feature model xml element!");
		} else {
			throw new ParseException("More than one feature model xml elements!");
		}
		return Trees.cloneTree(simplify(new And(constraints)));
	}

	@Override
	protected Formula atMost(final List<Formula> parseFeatures) {
		return new And(groupElements(parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1,
			parseFeatures.size()));
	}

	@Override
	protected Formula biimplies(Formula a, final Formula b) {
		return new And(new Or(new Not(a), b), new Or(new Not(b), a));
	}

	@Override
	protected Formula implies(Literal a, final Formula b) {
		return new Or(a.flip(), b);
	}

	@Override
	protected Formula implies(Formula a, final Formula b) {
		return new Or(new Not(a), b);
	}

	@Override
	protected Formula implies(final LiteralPredicate f, final List<Formula> parseFeatures) {
		final ArrayList<Formula> list = new ArrayList<>(parseFeatures.size() + 1);
		list.add(f.flip());
		list.addAll(parseFeatures);
		return new Or(list);
	}

	private List<Formula> groupElements(List<? extends Formula> elements, int k, final int n) {
		final List<Formula> groupedElements = new ArrayList<>();
		final Formula[] clause = new Formula[k + 1];
		final int[] index = new int[k + 1];

		// the position that is currently filled in clause
		int level = 0;
		index[level] = -1;

		while (level >= 0) {
			// fill this level with the next element
			index[level]++;
			// did we reach the maximum for this level
			if (index[level] >= (n - (k - level))) {
				// go to previous level
				level--;
			} else {
				clause[level] = elements.get(index[level]);
				if (level == k) {
					final Formula[] clonedClause = new Formula[clause.length];
					Arrays.copyOf(clause, clause.length);
					for (int i = 0; i < clause.length; i++) {
						clonedClause[i] = clause[i];
					}
					groupedElements.add(new Or(clonedClause));
				} else {
					// go to next level
					level++;
					// allow only ascending orders (to prevent from duplicates)
					index[level] = index[level - 1];
				}
			}
		}
		return groupedElements;
	}

	private static Formula simplify(Formula formula) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
		Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		return (Formula) auxiliaryRoot.getChild();
	}

	@Override
	public XmlFeatureModelCNFFormat getInstance() {
		return new XmlFeatureModelCNFFormat();
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public String getName() {
		return "FeatureIDECNF";
	}

}
