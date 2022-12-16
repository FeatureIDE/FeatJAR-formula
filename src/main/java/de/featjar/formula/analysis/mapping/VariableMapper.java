package de.featjar.formula.analysis.mapping;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.value.ValueClauseList;

public class VariableMapper<T extends VariableMapper.VariableMappee<?, ?>> {


    protected final T variableMappee;
    protected final VariableMap variableMap;

    public VariableMapper(T variableMappee, VariableMap variableMap) {
        this.variableMappee = variableMappee;
        this.variableMap = variableMap;
    }

    Result<?> toValue() {
        return variableMappee.toValue(variableMap);
    }

    Result<?> toBoolean() {
        return variableMappee.toBoolean(variableMap);
    }

    public static class BooleanClauseList extends VariableMapper<de.featjar.formula.analysis.bool.BooleanClauseList> {

        public BooleanClauseList(de.featjar.formula.analysis.bool.BooleanClauseList booleanClauseList, VariableMap variableMap) {
            super(booleanClauseList, variableMap);
        }

        @SuppressWarnings("unchecked")
        @Override
        Result<ValueClauseList> toValue() {
            return (Result<ValueClauseList>) super.toValue();
        }

        @SuppressWarnings("unchecked")
        @Override
        Result<BooleanClauseList> toBoolean() {
            return (Result<BooleanClauseList>) super.toValue();
        }
    }
}
