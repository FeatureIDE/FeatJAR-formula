package org.spldev.formula.visitor;

import org.spldev.formula.structure.*;
import org.spldev.tree.visitor.*;

public class NFVisitor implements TreeVisitor<Expression> {

	protected boolean isNf;
	protected boolean isClausalNf;

	@Override
	public void reset() {
		isNf = true;
		isClausalNf = true;
	}

	public boolean isNf() {
		return isNf;
	}

	public boolean isClausalNf() {
		return isClausalNf;
	}

}
