package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.IBooleanRepresentation;

import java.util.LinkedHashSet;

public interface IValueRepresentation {
    /**
     * {@return a Boolean object with the same contents as this object}
     * If needed, translates variable indices using the given variable map.
     * The returned result may contain warnings, as this can be a lossy conversion.
     *
     * @param variableMap the variable map
     */
    Result<? extends IBooleanRepresentation> toBoolean(VariableMap variableMap);

    default IComputation<? extends IBooleanRepresentation> toBoolean(IComputation<VariableMap> variableMap) {
        return IComputation.of(IComputation.of(this), variableMap)
                .mapResult(IValueRepresentation.class, "toBoolean",
                        pair -> pair.getKey().toBoolean(pair.getValue()).get());
    }

    LinkedHashSet<String> getVariableNames();
}
