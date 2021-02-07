package org.spldev.formula.expression.transform;

import org.spldev.formula.expression.*;
import org.spldev.tree.visitor.*;

public class NFVisitor implements TreeVisitor<Boolean, Expression> {

	protected boolean isNf;
	protected boolean isClausalNf;

	@Override
	public void reset() {
		isNf = true;
		isClausalNf = true;
	}

	@Override
	public Boolean getResult() {
		return isNf;
	}

	public boolean isNf() {
		return isNf;
	}

	public boolean isClausalNf() {
		return isClausalNf;
	}

}
