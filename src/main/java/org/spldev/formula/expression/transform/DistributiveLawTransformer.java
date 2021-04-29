package org.spldev.formula.expression.transform;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class DistributiveLawTransformer implements TreeVisitor<Void, Expression> {

	private boolean simplify = true;
	private boolean subsume = false;

	public boolean isSimplify() {
		return simplify;
	}

	public void setSimplify(boolean simplify) {
		this.simplify = simplify;
	}

	public boolean isSubsume() {
		return subsume;
	}

	public void setSubsume(boolean subsume) {
		this.subsume = subsume;
	}

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			return VistorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	@Override
	public VistorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.Continue;
		} else if (node instanceof Or) {
			if (Trees.preOrderStream(node).skip(1).anyMatch(n -> n instanceof Or)) {
				node.flatMapChildren(n -> convert(n, And::new));
			}
			return VistorResult.Continue;
		} else if (node instanceof And) {
			if (Trees.preOrderStream(node).skip(1).anyMatch(n -> n instanceof And)) {
				node.flatMapChildren(n -> convert(n, Or::new));
			}
			return VistorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			final AuxiliaryRoot auxiliaryRoot = (AuxiliaryRoot) node;
			auxiliaryRoot.setChild(Trees.cloneTree(auxiliaryRoot.getChild()));
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Formula> convert(Expression child, Function<Collection<Formula>, Formula> clauseConstructor) {
		if (child instanceof Literal) {
			return null;
		} else {
			final ArrayList<List<Formula>> newClauseList = new ArrayList<>();
			convertNF((Formula) child, newClauseList, new LinkedHashSet<>(child.getChildren()
				.size() << 1), 0);

			final ArrayList<Formula> filteredClauseList = new ArrayList<>(newClauseList.size());
			if (simplify) {
				Collections.sort(newClauseList, (c1, c2) -> {
					return c1.size() - c2.size();
				});
				final int count = newClauseList.size();
				for (int i = 0; i < count; i++) {
					final List<? extends Expression> children1 = newClauseList.get(i);
					if (children1 != null) {
						innerLoop: for (int j = 1; j < count; j++) {
							List<? extends Expression> children2 = newClauseList.get(j);
							if (children2 != null) {
								if (children1.size() == children2.size()) {
									Iterator<? extends Expression> it1 = children1.iterator();
									Iterator<? extends Expression> it2 = children2.iterator();
									while (it1.hasNext()) {
										Expression exp1 = it1.next();
										Expression exp2 = it2.next();
										if (!exp1.equalsNode(exp2)) {
											continue innerLoop;
										}
									}
									newClauseList.set(j, null);
								} else {
									Iterator<? extends Expression> it1 = children1.iterator();
									Iterator<? extends Expression> it2 = children2.iterator();
									l3: while (it1.hasNext()) {
										Expression exp1 = it1.next();
										while (it2.hasNext()) {
											Expression exp2 = it2.next();
											if (exp1.equalsNode(exp2)) {
												continue l3;
											}
										}
										continue innerLoop;
									}
									newClauseList.set(j, null);
								}
							}
						}
						filteredClauseList.add(clauseConstructor.apply((Collection<Formula>) children1));
					}
				}
			} else {
				for (List<Formula> children1 : newClauseList) {
					filteredClauseList.add(clauseConstructor.apply(children1));
				}
			}
			return filteredClauseList;
		}
	}

	private static void convertNF(Formula root, List<List<Formula>> clauses,
		LinkedHashSet<Literal> literals, int index) {
		if (index == root.getChildren().size()) {
			final ArrayList<Formula> newClauseLiterals = new ArrayList<>(literals.size());
			literals.forEach(newClauseLiterals::add);
			Collections.sort(newClauseLiterals, (c1, c2) -> {
				return c1.toString().compareTo(c2.toString());
			});
			clauses.add(newClauseLiterals);
		} else {
			final Formula child = (Formula) root.getChildren().get(index);
			if (child instanceof Literal) {
				Literal clauseLiteral = (Literal) child;
				if (literals.contains(clauseLiteral)) {
					convertNF(root, clauses, literals, index + 1);
				} else {
					if (!literals.contains(clauseLiteral.flip())) {
						literals.add(clauseLiteral);
						convertNF(root, clauses, literals, index + 1);
						literals.remove(clauseLiteral);
					}
				}
			} else {
				boolean redundant = false;
				for (Expression grandChild : child.getChildren()) {
					Literal clauseLiteral = (Literal) grandChild;
					if (literals.contains(clauseLiteral)) {
						redundant = true;
						break;
					}
				}
				if (redundant) {
					convertNF(root, clauses, literals, index + 1);
				} else {
					for (Expression grandChild : child.getChildren()) {
						Literal clauseLiteral = (Literal) grandChild;
						if (!literals.contains(clauseLiteral.flip())) {
							literals.add(clauseLiteral);
							convertNF(root, clauses, literals, index + 1);
							literals.remove(clauseLiteral);
						}
					}
				}
			}
		}
	}

}
