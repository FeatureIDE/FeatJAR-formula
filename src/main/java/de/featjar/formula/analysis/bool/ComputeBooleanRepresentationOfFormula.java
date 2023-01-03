package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.predicate.Literal;

import java.util.List;
import java.util.Objects;

public class ComputeBooleanRepresentationOfFormula extends ABooleanRepresentationComputation<IFormula, BooleanClauseList> { // todo: assumption: is in CNF
    public ComputeBooleanRepresentationOfFormula(IComputation<IFormula> valueRepresentation) {
        super(valueRepresentation);
    }

    public static Result<BooleanClauseList> toBooleanClauseList(IFormula formula, VariableMap variableMap) {
        final BooleanClauseList clauseList = new BooleanClauseList();
        //final Object formulaValue = formula.evaluate();
//                    if (formulaValue != null) { //TODO
//                        if (formulaValue == Boolean.FALSE) {
//                            clauseList.add(new LiteralList());
//                        }
//                    } else {
        formula.getChildren().stream()
                .map(expression -> getClause((IFormula) expression, variableMap))
                .filter(Objects::nonNull)
                .forEach(clauseList::add);
        //}
        return Result.of(clauseList); //todo: better error handling when index cannot be found
    }

    protected static BooleanClause getClause(IFormula formula, VariableMap variableMap) {
        if (formula instanceof Literal) {
            final Literal literal = (Literal) formula;
            final int index = variableMap.get(literal.getExpression().getName()).orElseThrow();
            return new BooleanClause(literal.isPositive() ? index : -index);
        } else {
            final List<? extends IExpression> children = formula.getChildren();
            if (children.stream().anyMatch(literal -> literal == Expressions.True)) {
                return null;
            } else {
                final int[] literals = children.stream()
                        .filter(literal -> literal != Expressions.False)
                        .filter(literal -> literal instanceof Literal)
                        .mapToInt(literal -> {
                            final int variable = variableMap.get(((Literal) literal).getExpression().getName())
                                    .orElseThrow();
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new BooleanClause(literals);
            }
        }
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeBooleanRepresentationOfFormula(getInput());
    }
}
