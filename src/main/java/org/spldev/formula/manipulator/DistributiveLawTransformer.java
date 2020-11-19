package org.spldev.formula.manipulator;

import java.util.*;

import org.spldev.formula.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.tree.visitor.*;

public class DistributiveLawTransformer implements TreeVisitor<Expression> {

	public enum NormalForm {
		CNF, DNF
	}

	private boolean simplify = true;

	private NormalForm normalForm = NormalForm.CNF;

	public boolean isSimplify() {
		return simplify;
	}

	public void setSimplify(boolean simplify) {
		this.simplify = simplify;
	}

	public NormalForm getNormalForm() {
		return normalForm;
	}

	public void setNormalForm(NormalForm normalForm) {
		this.normalForm = normalForm;
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			switch (normalForm) {
			case CNF:
				if (!(node instanceof And)) {
					node.replaceChildren(this::clausifyCNF);
				}
				break;
			case DNF:
				if (!(node instanceof Or)) {
					node.replaceChildren(this::clausifyDNF);
				}
				break;
			default:
				break;
			}

			return VistorResult.Continue;
		} else if (node instanceof Atomic) {
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	protected Formula clausifyCNF(Expression node) {
		if (node instanceof And) {
			final List<Formula> children = ((And) node).getChildren();
			final LinkedList<LinkedHashSet<Formula>> newClauseList = new LinkedList<>();
			for (final Formula child : children) {
				createNF(child, newClauseList);
				if (simplify) {
					if (newClauseList.isEmpty()) {
						return Literal.True;
					} else {
						if (unitPropagation(newClauseList)) {
							return Literal.False;
						}
					}
				}
			}
			final ArrayList<Formula> newChildren = new ArrayList<>(newClauseList.size());
			for (final HashSet<Formula> clause : newClauseList) {
				newChildren.add(new Or(clause));
			}
			return (Formula) Formulas.manipulate(new And(newChildren), new TreeSimplifier());
		}
		return null;
	}

	protected Formula clausifyDNF(Expression node) {
		if (node instanceof Or) {
			final List<Formula> children = ((Or) node).getChildren();
			final LinkedList<LinkedHashSet<Formula>> newClauseList = new LinkedList<>();
			for (final Formula child : children) {
				createNF(child, newClauseList);
				if (simplify) {
					if (newClauseList.isEmpty()) {
						return Literal.False;
					} else {
						if (unitPropagation(newClauseList)) {
							return Literal.True;
						}
					}
				}
			}
			final ArrayList<Formula> newChildren = new ArrayList<>(newClauseList.size());
			for (final HashSet<Formula> clause : newClauseList) {
				newChildren.add(new And(clause));
			}
			return (Formula) Formulas.manipulate(new Or(newChildren), new TreeSimplifier());
		}
		return null;
	}

	protected void createNF(Formula parent, LinkedList<LinkedHashSet<Formula>> newClauseList) {
		final List<Formula> children = (parent instanceof Connective) ? ((Connective) parent).getChildren()
			: Arrays.asList(parent);
		final ArrayList<List<List<Formula>>> oldClauses = collectClauses(children);
		buildClauses(oldClauses, newClauseList, new LinkedHashSet<>(), 0);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<List<List<Formula>>> collectClauses(List<Formula> children) {
		final ArrayList<List<List<Formula>>> oldClauses = new ArrayList<>(children.size());
		for (final Formula child : children) {
			final ArrayList<List<Formula>> oldClause = new ArrayList<>();
			if (child instanceof Literal) {
				oldClause.add(Arrays.asList(child));
			} else {
				for (final Expression grandchild : child.getChildren()) {
					if (grandchild instanceof Literal) {
						oldClause.add(Arrays.asList((Literal) grandchild));
					} else {
						final List<Formula> children2 = (List<Formula>) grandchild.getChildren();
						oldClause.add(new ArrayList<>(children2));
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
					if (simplify && literals.contains(((Literal) l).cloneNode().flip())) {
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
				unitClauses.add(literal.cloneNode().flip());
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
						unitClauses.add(literal.cloneNode().flip());
						newUnitClauses = true;
					}
				}
			}
		}
		return false;
	}

}
