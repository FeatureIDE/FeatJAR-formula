package org.spldev.formula.expression.io;
/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.transform.*;
import org.spldev.util.logging.*;
import org.spldev.util.tree.*;
import org.w3c.dom.*;

public class XmlFeatureModelCNFFormat extends XmlFeatureModelFormat {

	public XmlFeatureModelCNFFormat() {
	}

	protected Formula readDocument(Document doc) {
		map = new VariableMap();
		final List<Element> elementList = getElement(doc, FEATURE_MODEL);
		if (elementList.size() == 1) {
			Element e = elementList.get(0);
			parseStruct(getElement(e, STRUCT));
			final int crossTreeConstaintsIndex = constraints.size();
			parseConstraints(getElement(e, CONSTRAINTS));
			final List<Formula> crossTreeConstraints = constraints.subList(crossTreeConstaintsIndex, constraints.size());
			List<Formula> cnfConstraints = crossTreeConstraints.stream().map(XmlFeatureModelCNFFormat::toCNF).collect(Collectors.toList());
			crossTreeConstraints.clear();
			constraints.addAll(cnfConstraints);
		} else if (elementList.isEmpty()) {
			Logger.logError("Not feature model xml element!");
		} else {
			Logger.logError("More than one feature model xml elements!");
		}
		return Trees.cloneTree(new And(constraints));
	}

	@Override
	protected Formula atMost(final List<Formula> parseFeatures) {
		return new And(groupElements(
			parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1, parseFeatures.size()));
	}

	@Override
	protected Formula biimplies(Formula a, final Formula b) {
		return new And(
			new Or(new Not(a), b),
			new Or(new Not(b), a));
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
	protected Formula implies(final LiteralVariable f, final List<Formula> parseFeatures) {
		final ArrayList<Formula> list = new ArrayList<>(parseFeatures);
		list.add(f.flip());
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

	private static Formula toCNF(Formula formula) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
		Trees.traverse(auxiliaryRoot, new DeMorganTransformer());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		final DistributiveLawTransformer visitor = new DistributiveLawTransformer();
		final Formula root = (Formula) auxiliaryRoot.getChild();
		if (!(root instanceof And)) {
			auxiliaryRoot.setChild(new And(root));
		}
		Trees.traverse(auxiliaryRoot, visitor);

		return (Formula) auxiliaryRoot.getChild();
	}

	@Override
	public XmlFeatureModelCNFFormat getInstance() {
		return new XmlFeatureModelCNFFormat();
	}

}
