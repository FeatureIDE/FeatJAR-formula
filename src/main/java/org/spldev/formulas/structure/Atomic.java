package org.spldev.formulas.structure;

import java.util.*;

/**
 * An atomic formula.
 *
 * @author Sebastian Krieter
 */
public interface Atomic extends Formula {

	@Override
	List<? extends Term<?>> getChildren();

}
