package de.featjar.formula.analysis.solver;

import java.util.List;

public interface AssumptionList<T extends Assumable<?>> {
    List<T> getAll();
}
