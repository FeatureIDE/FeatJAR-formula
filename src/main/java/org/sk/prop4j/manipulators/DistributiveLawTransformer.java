package org.sk.prop4j.manipulators;

import java.util.*;

import org.sk.prop4j.*;
import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.atomic.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.*;
import org.sk.trees.visitors.*;

public class DistributiveLawTransformer implements NodeVisitor {

	public enum NormalForm {
		CNF, DNF
	}

	private boolean fail;

	private boolean simplify = true;

	private NormalForm normalForm = NormalForm.CNF;

	public boolean isSimplify() {
		return simplify;
	}

	public void setSimplify(boolean simplify) {
		this.simplify = simplify;
	}

	@Override
	public void init() {
		fail = false;
	}

	public boolean isSuccess() {
		return !fail;
	}

	public NormalForm getNormalForm() {
		return normalForm;
	}

	public void setNormalForm(NormalForm normalForm) {
		this.normalForm = normalForm;
	}

	@Override
	public VistorResult firstVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			return VistorResult.Continue;
		} else if (node instanceof Term) {
			return VistorResult.SkipSiblings;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	@Override
	public VistorResult lastVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		if (fail) {
			return VistorResult.SkipAll;
		}
		if (node instanceof Compound) {
			switch (normalForm) {
			case CNF:
				if (!(node instanceof And)) {
					((Compound) node).replaceChildren(this::clausifyCNF);
				}
				break;
			case DNF:
				if (!(node instanceof Or)) {
					((Compound) node).replaceChildren(this::clausifyCNF);
				}
				break;
			default:
				break;
			}

			return VistorResult.Continue;
		} else if (node instanceof Atomic) {
			return VistorResult.Continue;
		} else {
			fail = true;
			return VistorResult.SkipAll;
		}
	}

	protected Formula clausifyCNF(Formula node) {
		if (node instanceof And) {
			final List<Formula> children = ((And) node).getChildren();
			final LinkedList<LinkedHashSet<Formula>> newClauseList = new LinkedList<>();
			for (final Formula child : children) {
				createNF(child, newClauseList);
				if (simplify) {
					if (newClauseList.isEmpty()) {
						return new True();
					} else {
						if (unitPropagation(newClauseList)) {
							return new False();
						}
					}
				}
			}
			final ArrayList<Formula> newChildren = new ArrayList<>(newClauseList.size());
			for (final HashSet<Formula> clause : newClauseList) {
				newChildren.add(new Or(clause));
			}
			return Formulas.manipulate(new And(newChildren), new TreeSimplifier());
		}
		return null;
	}

	protected Formula clausifyDNF(Formula node) {
		if (node instanceof Or) {
			final List<Formula> children = ((Or) node).getChildren();
			final LinkedList<LinkedHashSet<Formula>> newClauseList = new LinkedList<>();
			for (final Formula child : children) {
				createNF(child, newClauseList);
				if (simplify) {
					if (newClauseList.isEmpty()) {
						return new False();
					} else {
						if (unitPropagation(newClauseList)) {
							return new True();
						}
					}
				}
			}
			final ArrayList<Formula> newChildren = new ArrayList<>(newClauseList.size());
			for (final HashSet<Formula> clause : newClauseList) {
				newChildren.add(new And(clause));
			}
			return Formulas.manipulate(new Or(newChildren), new TreeSimplifier());
		}
		return null;
	}

	protected void createNF(Formula parent, LinkedList<LinkedHashSet<Formula>> newClauseList) {
		final List<Formula> children = (parent instanceof Connective) ? ((Connective) parent).getChildren()
			: Arrays.asList(parent);
		final ArrayList<List<List<Formula>>> oldClauses = collectClauses(children);
		buildClauses(oldClauses, newClauseList, new LinkedHashSet<>(), 0);
	}

	private ArrayList<List<List<Formula>>> collectClauses(List<Formula> children) {
		final ArrayList<List<List<Formula>>> oldClauses = new ArrayList<>(children.size());
		for (final Formula child : children) {
			final ArrayList<List<Formula>> oldClause = new ArrayList<>();
			if (child instanceof Literal) {
				oldClause.add(Arrays.asList(child));
			} else {
				for (final Formula grandchild : ((Connective) child).getChildren()) {
					if (grandchild instanceof Literal) {
						oldClause.add(Arrays.asList(grandchild));
					} else {
						oldClause.add(new ArrayList<>(((Connective) grandchild).getChildren()));
					}
				}
			}
			oldClauses.add(oldClause);
		}
		for (final List<List<Formula>> clause : oldClauses) {
			Collections.sort(clause, (a, b) -> b.size() - a.size());
		}
		Collections.sort(oldClauses, (a, b) -> a.size() - b.size());
		return oldClauses;
	}

	@SuppressWarnings("unchecked")
	private void buildClauses(ArrayList<List<List<Formula>>> clauseList,
		LinkedList<LinkedHashSet<Formula>> newClauseList, LinkedHashSet<Formula> literals, int depth) {
		for (final LinkedHashSet<Formula> hashSet : newClauseList) {
			if (literals.containsAll(hashSet)) {
				// is subsumed
				return;
			}
		}
		if (depth == clauseList.size()) {
			for (final Iterator<LinkedHashSet<Formula>> iterator = newClauseList.iterator(); iterator.hasNext();) {
				final LinkedHashSet<Formula> otherLiterals = iterator.next();
				if (otherLiterals.size() > literals.size()) {
					if (otherLiterals.containsAll(literals)) {
						// subsumes prior clause
						iterator.remove();
					}
				}
			}
			newClauseList.add((LinkedHashSet<Formula>) literals.clone());
		} else {
			final List<List<Formula>> clause = clauseList.get(depth);
			final ArrayList<Formula> addedLiterals = new ArrayList<>();
			clauseLoop: for (int j = 0; j < clause.size(); j++) {
				for (final Formula l : clause.get(j)) {
					if (simplify && literals.contains(((Literal) l).clone().flip())) {
						literals.removeAll(addedLiterals);
						addedLiterals.clear();
						continue clauseLoop;
					} else {
						if (literals.add(l)) {
							addedLiterals.add(l);
						}
					}
				}
				buildClauses(clauseList, newClauseList, literals, depth + 1);
				literals.removeAll(addedLiterals);
				addedLiterals.clear();
			}
		}
	}

	protected boolean unitPropagation(LinkedList<LinkedHashSet<Formula>> newClauseList) {
		boolean newUnitClauses = false;
		final HashSet<Literal> unitClauses = new HashSet<>();
		for (final LinkedHashSet<Formula> clause : newClauseList) {
			if (clause.isEmpty()) {
				return true;
			} else if (clause.size() == 1) {
				final Literal literal = (Literal) clause.iterator().next();
				unitClauses.add(literal.clone().flip());
				newUnitClauses = true;
			}
		}
		while (newUnitClauses) {
			newUnitClauses = false;
			for (final LinkedHashSet<Formula> clause : newClauseList) {
				if (clause.removeAll(unitClauses)) {
					if (clause.isEmpty()) {
						return true;
					} else if (clause.size() == 1) {
						final Literal literal = (Literal) clause.iterator().next();
						unitClauses.add(literal.clone().flip());
						newUnitClauses = true;
					}
				}
			}
		}
		return false;
	}

}
