package de.featjar.formula.analysis.bool;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueRepresentation;

public interface BooleanRepresentation {
    /**
     * {@return a value object with the same contents as this object}
     * If needed, translates variable indices using the given variable map.
     * The returned result may contain warnings, as this can be a lossy conversion.
     *
     * @param variableMap the variable map
     */
    Result<? extends ValueRepresentation> toValue(VariableMap variableMap);

    Computation<? extends ValueRepresentation> toValue(Computation<VariableMap> variableMap);
}
