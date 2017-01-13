package ch.jjoller.mcaixictw.worldmodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Same as ContextTree but uses one tree for each perception bit.
 */
public class FactorialContextTree extends ContextTree {

	protected FactorialContextTree(String name, int depth) {
		super(name, depth);
		// make the root null because it is not needed
		root = null;
	}

	protected List<ContextTree> ctwTrees;
	protected int addedSymbolCount;

	private List<ContextTree> allocateTrees(int numTrees) {
		System.out.println("allocate trees: " + numTrees);
		if (ctwTrees == null) {
			ctwTrees = new ArrayList<ContextTree>(numTrees);
		}
		for (int i = 0; i < numTrees; i++) {
			ctwTrees.add(new ContextTree(name + "_" + i, depth));
		}
		return ctwTrees;
	}

	// @Override
	// public void clear() {
	// history.clear();
	// addedSymbolCount = 0;
	// ctwTrees = null;
	// }

	protected void update(boolean sym) {
		int m_currentlyActiveTree = addedSymbolCount % ctwTrees.size();
		assert (historySize() == 0 ? m_currentlyActiveTree == 0 : true);

		// add to correct tree
		ctwTrees.get(m_currentlyActiveTree).add(sym, history);

		// The symbol is now history...
		history.push(sym);
		addedSymbolCount++;
	}

	@Override
	public void update(List<Boolean> symlist) {
		if (ctwTrees == null || ctwTrees.size() == 0) {
			ctwTrees = allocateTrees(symlist.size());
		}
		// assume the perception has always the same size (this actually makes
		// sense, the advantage of making this assumption is that we can don't
		// have to know the size of a perception when we crate the
		// FactorialContextTree).
		if (symlist.size() != ctwTrees.size()) {
			throw new IllegalArgumentException(
					"perception has wrong size, there are " + ctwTrees.size()
							+ " trees but the perception has size "
							+ symlist.size());
		}
		// Update the symbol list
		for (int i = 0; i < symlist.size(); i++) {
			update(symlist.get(i));
		}
	}

	public void revert() {
		assert (addedSymbolCount > 0);
		assert (historySize() > 0);
		// we need to access the history otherwise we cannot tell which the
		// last inserted symbol was.
		boolean sym = history.pop();
		int m_currentlyActiveTree = (addedSymbolCount - 1) % ctwTrees.size();
		ctwTrees.get(m_currentlyActiveTree).remove(sym, history);
		addedSymbolCount--;
	}

	@Override
	public double logBlockProbability() {
		double logSum;
		logSum = 0.0;
		for (int i = 0; i < ctwTrees.size(); ++i) {
			double logProbTree = ctwTrees.get(i).logBlockProbability();
			assert (Math.exp(logProbTree) <= 1.0 && Math.exp(logProbTree) >= 0.0);
			logSum += logProbTree;
		}
		assert (Math.exp(logSum) <= 1.0 && Math.exp(logSum) >= 0.0);
		return logSum;
	}

	@Override
	public int size() {
		int size = 0;
		for (ContextTree t : ctwTrees) {
			size += t.size();
		}
		return size;
	}

	public String toString() {
		String result = "";
		result += "Factorial Context Tree" + "\n";
		result += "name: " + name + "\n";
		result += "depth: " + depth + "\n";
		if (ctwTrees != null) {
			result += "num context trees: " + ctwTrees.size() + "\n";
		} else {
			result += "no context trees initialized\n";
		}
		result += "history size: " + history.size() + "\n";
		return result;
	}

	public String prettyPrint() {
		String result = new String("");
		result += "History: ";
		for (int i = 0; i < historySize(); ++i) {
			result += history.get(i) + " ";
		}
		result += "\n";
		for (int i = 0; i < ctwTrees.size(); i++) {
			String s = ctwTrees.get(i).toString();
			result += "Tree[" + i + "]\n" + s;
		}
		return result;
	}

	public List<ContextTree> getCtwTrees() {
		return ctwTrees;
	}

	public void setCtwTrees(List<ContextTree> ctwTrees) {
		this.ctwTrees = ctwTrees;
	}

	public int getAddedSymbolCount() {
		return addedSymbolCount;
	}

	public void setAddedSymbolCount(int addedSymbolCount) {
		this.addedSymbolCount = addedSymbolCount;
	}

}
