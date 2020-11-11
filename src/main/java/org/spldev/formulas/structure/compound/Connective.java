package org.spldev.formulas.structure.compound;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 * A logical connector that connects other {@link Formula}.
 *
 * @author Sebastian Krieter
 */
public interface Connective extends Formula {

	@Override
	List<Formula> getChildren();
}
