package org.sk.prop4j.visitors;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.compound.*;
import org.sk.trees.structure.*;
import org.sk.trees.visitors.*;

public class DNFVisitor implements NodeVisitor {

	private boolean isDnf = false;
	private boolean isClausalDnf = false;
	private int depth;

	@Override
	public void init() {
		isDnf = true;
		isClausalDnf = true;
		depth = 0;
	}

	@Override
	public VistorResult firstVisit(List<Tree> path) {
		final Tree node = NodeVisitor.getCurrentNode(path);
		depth++;
		if (node instanceof Or) {
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof And)) {
					if (!(child instanceof Atomic)) {
						isDnf = false;
						isClausalDnf = false;
						return VistorResult.SkipAll;
					}
					isClausalDnf = false;
				}
			}
		} else if (node instanceof And) {
			if (depth != 2) {
				isClausalDnf = false;
			}
			for (final Tree child : node.getChildren()) {
				if (!(child instanceof Atomic)) {
					isDnf = false;
					isClausalDnf = false;
					return VistorResult.SkipAll;
				}
			}
		} else if (node instanceof Atomic) {
			if (depth != 3) {
				isClausalDnf = false;
			}
			return VistorResult.SkipChildren;
		} else {
			isDnf = false;
			isClausalDnf = false;
			return VistorResult.SkipAll;
		}
		return VistorResult.Continue;
	}

	public boolean isDnf() {
		return isDnf;
	}

	public boolean isClausalDnf() {
		return isClausalDnf;
	}

	@Override
	public VistorResult lastVisit(List<Tree> path) {
		depth--;
		return VistorResult.Continue;
	}

}
