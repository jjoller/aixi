package ch.jjoller.mcaixictw.worldmodels;

import java.util.List;

/**
 * The agents model of the the environment. The goal of the agent is to maximize
 * its reward while it is interacting with an environment. In order to be
 * successful the agent needs to build its own model of the world while it is
 * interacting with it.
 */
public abstract class Worldmodel {

	// @Deprecated
	// public Worldmodel() {
	// // JPA
	// }

	protected Worldmodel(String name) {
		this.name = name;
	}

	protected String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract String toString();

	// public abstract void clear();

	public abstract void update(List<Boolean> symlist);

	public abstract void updateHistory(List<Boolean> symlist);

	public abstract void revert(int numSymbols);

	public abstract void revertHistory(int newsize);

	public abstract List<Boolean> genRandomSymbols(int bits);

	public abstract List<Boolean> genRandomSymbolsAndUpdate(int bits);

	public abstract double predict(List<Boolean> symbols);

	public abstract boolean nthHistorySymbol(int n);

	public abstract int historySize();

	public static Worldmodel getInstance(String name,
			WorldModelSettings settings) {

		if (settings.isFacContextTree()) {
			return new FactorialContextTree(name, settings.getDepth());
		} else {
			return new ContextTree(name, settings.getDepth());
		}
	}
}