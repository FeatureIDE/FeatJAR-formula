package org.sk.prop4j.visitors;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.*;
import org.sk.trees.visitors.*;

public class CNFVisitor implements NodeVisitor {

	private boolean isCnf = false;
	private boolean isClausalCnf = false;
	private int depth;

	@Override
	public void init() {
		isCnf = true;
		isClausalCnf = true;
		depth = 0;
	}

	@Override
	public VistorResult firstVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		depth++;
		if (node instanceof And) {
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof Or)) {
					if (!(child instanceof Atomic)) {
						isCnf = false;
						isClausalCnf = false;
						return VistorResult.SkipAll;
					}
					isClausalCnf = false;
				}
			}
		} else if (node instanceof Or) {
			if (depth != 2) {
				isClausalCnf = false;
			}
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof Atomic)) {
					isCnf = false;
					isClausalCnf = false;
					return VistorResult.SkipAll;
				}
			}
		} else if (node instanceof Atomic) {
			if (depth != 3) {
				isClausalCnf = false;
			}
			return VistorResult.SkipChildren;
		} else {
			isCnf = false;
			isClausalCnf = false;
			return VistorResult.SkipAll;
		}
		return VistorResult.Continue;
	}

	public boolean isCnf() {
		return isCnf;
	}

	public boolean isClausalCnf() {
		return isClausalCnf;
	}

	@Override
	public VistorResult lastVisit(List<Tree> path) {
		depth--;
		return VistorResult.Continue;
	}

}
