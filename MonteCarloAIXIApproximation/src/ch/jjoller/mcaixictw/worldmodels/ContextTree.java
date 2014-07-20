package ch.jjoller.mcaixictw.worldmodels;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import ch.jjoller.mcaixictw.Bits;
import ch.jjoller.mcaixictw.Util;

/**
 * 
 * A bit context tree. Learns the next bit based on context which is of the same
 * length as the tree.
 * 
 */
public class ContextTree extends Worldmodel {

	// create a context tree of specified maximum depth
	protected ContextTree(String name, int depth) {
		super(name);
		history = new Stack<Boolean>();
		root = new CTNode();
		this.depth = depth;
	}

	protected Stack<Boolean> history; // the agents history
	protected CTNode root; // the root node of the context tree
	protected int depth; // the maximum depth of the context tree

	/**
	 * recursively traverses the tree and returns a string in human readable
	 * format (for debug purposes)
	 */
	public String toString() {
		String result = "";
		result += "ContextTree" + "\n";
		result += "name: " + name + "\n";
		result += "depth: " + depth + "\n";
		result += "history size: " + history.size() + "\n";
		return result;
	}

	public String prettyPrint() {
		String result = "";
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				boolean b1 = i == 0 ? false : true;
				boolean b2 = j == 0 ? false : true;

				List<Boolean> symbols = new ArrayList<Boolean>();
				symbols.add(b1);
				symbols.add(b2);

				result += "P(" + b2 + b1 + "|h) = " + predict(symbols) + "\n";
			}
		}
		if (root != null) {
			int nodesPerLevel = 1;
			int nodesPerNextLevel = 0;
			int nodesProcessed = 0;
			LinkedList<CTNode> bfsQueue = new LinkedList<CTNode>();
			bfsQueue.add(root);
			while (!bfsQueue.isEmpty()) {
				CTNode currNode = bfsQueue.removeFirst();
				result += currNode + " ";
				nodesProcessed++;
				if (currNode.child(false) != null) {
					bfsQueue.addLast(currNode.child(false));
					nodesPerNextLevel++;
				}
				if (currNode.child(true) != null) {
					bfsQueue.addLast(currNode.child(true));
					nodesPerNextLevel++;
				}
				if (nodesProcessed == nodesPerLevel) {
					result += "\n\n";
					nodesProcessed = 0;
					nodesPerLevel = nodesPerNextLevel;
					nodesPerNextLevel = 0;
				}
			}
		}
		return result;
	}

	protected void add(boolean sym, Stack<Boolean> underlyingHistory) {
		CTNode currNode = root;

		// Update the root
		double logKTMul = currNode.logKTMul(sym);
		currNode.logPest += logKTMul;
		assert (!Double.isNaN(currNode.logPest));

		currNode.incrCount(sym);

		// Update all nodes with that context
		int d = Math.max(underlyingHistory.size() - depth, 0);

		for (int i = underlyingHistory.size() - 1; i >= d; i--) {

			boolean currSymbol = underlyingHistory.get(i);
			// may have to create a new node.
			if (currNode.child(currSymbol) == null) {
				currNode.setChild(currSymbol, new CTNode());
			}

			currNode = currNode.child(currSymbol);

			// Update the node
			currNode.logPest += currNode.logKTMul(sym);
			assert (!Double.isNaN(currNode.logPest));
			currNode.incrCount(sym);
		}
		updateProbabilities(currNode);
	}

	protected void remove(boolean sym, Stack<Boolean> underlyingHistory) {

		CTNode currNode = root;

		assert (currNode.count(sym) > 0);
		currNode.decrCount(sym);
		// improve numerical stability
		currNode.logPest = currNode.count(false) + currNode.count(true) == 0 ? 0.0
				: currNode.logPest - currNode.logKTMul(sym);
		assert (!Double.isNaN(currNode.logPest));

		// Update all nodes with that context

		int d = Math.max(underlyingHistory.size() - depth, 0);

		for (int i = underlyingHistory.size() - 1; i >= d; i--) {

			boolean currSymbol = underlyingHistory.get(i);

			currNode = currNode.child(currSymbol);
			assert (currNode != null);

			// Update the node
			assert (currNode.count(sym) > 0);
			currNode.decrCount(sym);
			if ((currNode.count(false) + currNode.count(true)) == 0) {
				// Delete the current node, can't be the root node which was
				// processed above.
				currNode = currNode.parent;
				currNode.setChild(currSymbol, null);
				break;
			} else {
				currNode.logPest -= currNode.logKTMul(sym);
				assert (!Double.isNaN(currNode.logPest));
			}
		}
		updateProbabilities(currNode);
	}

	protected void updateProbabilities(CTNode currNode) {

		// Update the probabilities
		// using the weighted probabilities from section 5.7

		// TODO make sure this is correct.
		if (currNode.child(false) == null && currNode.child(true) == null) {
			currNode.logPweight = currNode.logPest;
			currNode = currNode.parent;
		}

		double[] pChildW = new double[2];

		while (currNode != null) {

			pChildW[0] = currNode.child(false) != null ? currNode.child(false).logPweight
					: 0.0;
			pChildW[1] = currNode.child(true) != null ? currNode.child(true).logPweight
					: 0.0;

			// We want to calculate
			// log(Pw) = log(0.5(P_est + P0*P1))
			// Doing a bit of calculus (and using Google "sum of log
			// probabilities) we can rewrite this:
			// log(Pw) = log(0.5(P_est + P0*P1)) = log(0.5) + log(P_est + P0*P1)
			// = log(0.5) + log(1+ (P0*P1)/P_est) + logP_est
			// = log(0.5) + log(1+ exp(log((P0*P1)/P_est))) + logP_est =
			// log(0.5) + log(1+ exp( logP0+logP1-logP_est ) + logP_est
			// and so we only need one exp

			// double p_x = pChildW[0] + pChildW[1] - currNode.logPest;

			// if (p_x < 80) {
			// p_x = Math.log(1.0 + Math.exp(p_x));
			// }
			// if (p_x < -80) {
			// p_x = 1;
			// }

			double log_one_plus_exp = pChildW[0] + pChildW[1]
					- currNode.logPest;

			if (log_one_plus_exp < 80.0) {
				log_one_plus_exp = Math.log(1.0 + Math.exp(log_one_plus_exp));
			}

			currNode.logPweight = Math.log(0.5) + currNode.logPest
					+ log_one_plus_exp;

			// inefficient
			// double p_est = Math.exp(currNode.logPest);
			// double child0 = Math.exp(pChildW[0]);
			// double child1 = Math.exp(pChildW[1]);
			// double p_weight = 0.5 * (p_est + child0 * child1);
			//
			// currNode.logPweight = Math.log(p_weight);
			//
			// if (Double.isInfinite(currNode.logPweight)) {
			// currNode.logPweight = Double.MIN_VALUE;
			// }

			// if (Double.isInfinite(currNode.logPweight)
			// || Double.isNaN(currNode.logPweight)) {
			// System.out.println(p_est + " " + child0 + " " + child1);
			// System.out.println("is invalid: " + p_weight + " "
			// + currNode.logPweight);
			// }

			assert (!(Double.isNaN(currNode.logPweight) || Double
					.isInfinite(currNode.logPweight)));

			// continue with parent node
			currNode = currNode.parent;
		}
	}

	/**
	 * either add or remove a symbol from the tree.
	 * 
	 * 
	 * @param sym
	 * @param addSymbol
	 */
	@Deprecated
	protected void change(boolean sym, boolean addSymbol,
			Stack<Boolean> underlyingHistory) {

		CTNode currNode = root;

		// Update the root
		if (addSymbol) {
			currNode.logPest += currNode.logKTMul(sym);
			currNode.incrCount(sym);
			// m_added_symbol_count++;
		} else {
			assert (currNode.count(sym) > 0);
			currNode.decrCount(sym);
			// improve numerical stability:
			if ((currNode.count(false) + currNode.count(true)) == 0) {
				currNode.logPest = 0.0;
			} else {
				currNode.logPest -= currNode.logKTMul(sym);
			}
			// m_added_symbol_count--;
		}

		// Update all nodes with that context

		int d = Math.max(underlyingHistory.size() - depth, 0);

		for (int i = underlyingHistory.size() - 1; i >= d; i--) {

			boolean currSymbol = underlyingHistory.get(i);
			// may have to create a new node.
			if (currNode.child(currSymbol) == null) {
				assert (addSymbol == true);
				currNode.setChild(currSymbol, new CTNode());
				// currNode.child(currSymbol).parent = currNode;
			}
			currNode = currNode.child(currSymbol);

			// Update the node
			if (addSymbol) {
				currNode.logPest += currNode.logKTMul(sym);
				currNode.incrCount(sym);
			} else {
				assert (currNode.count(sym) > 0);
				currNode.decrCount(sym);
				// Delete the current node
				// the current node cannot be the root node here, which was
				// processed above already
				if ((currNode.count(false) + currNode.count(true)) == 0) {
					currNode = currNode.parent;
					currNode.setChild(currSymbol, null);
					break;
				} else {
					currNode.logPest -= currNode.logKTMul(sym);
				}
			}

		}

		// Update the probabilities
		// using the weighted probabilities from section 5.7
		if (currNode.child(false) == null && currNode.child(true) == null) {
			currNode.logPweight = currNode.logPest;
			currNode = currNode.parent;
		}

		double[] pChild_w = new double[2];

		while (currNode != null) {
			for (int c = 0; c < 2; c++) {
				boolean cBool = c == 1 ? true : false;
				pChild_w[c] = (currNode.child(cBool) != null) ? currNode
						.child(cBool).logPweight : 0;
			}

			// We want to calculate
			// log(Pw) = log(0.5(P_est + P0*P1))
			// Doing a bit of calculus (and using Google "sum of log
			// probabilities) we can rewrite this:
			// log(Pw) = log(0.5(P_est + P0*P1)) = log(0.5) + log(P_est + P0*P1)
			// = log(0.5) + log(1+ (P0*P1)/P_est) + logP_est
			// = log(0.5) + log(1+ exp(log((P0*P1)/P_est))) + logP_est =
			// log(0.5) + log(1+ exp( logP0+logP1-logP_est ) + logP_est
			// and so we only need one exp
			double p_x = pChild_w[0] + pChild_w[1] - currNode.logPweight;

			// if (p_x < 80) {
			// p_x = Math.log(1.0 + Math.exp(p_x));
			// }
			// if (p_x < -80) {
			// p_x = 1;
			// }

			double p_w = p_x + Math.log(0.5) + currNode.logPweight;

			if (Util.DebugOutput) {
				System.out
						.println("currNode.log_prob_weighted: "
								+ currNode.logPweight + " p_x: " + p_x
								+ " p_w: " + p_w);
			}
			// Make sure that the probabilities are updated correctly
			assert (!addSymbol || currNode.logPweight >= p_w);
			assert (addSymbol || currNode.logPweight <= p_w);
			currNode.logPweight = p_w;

			// continue with parent node
			currNode = currNode.parent;
		}
	}

	/**
	 * update the model with a symbol.
	 * 
	 * @param sym
	 */
	protected void update(boolean sym) {
		add(sym, history);
		history.push(sym); // The symbol is now history...
	}

	/**
	 * most recent symbol has the highest index.
	 * 
	 * @param symlist
	 */
	public void update(List<Boolean> symlist) {
		// Update the symbol list
		for (int i = 0; i < symlist.size(); i++) {
			update(symlist.get(i));
		}
	}

	/**
	 * updates the history statistics, without touching the context tree
	 * 
	 * most recent symbol has the highest index.
	 * 
	 * @param symlist
	 */
	public void updateHistory(List<Boolean> symlist) {
		// System.out.println("update history: " + Util.toString(symlist));
		history.addAll(symlist);
	}

	/**
	 * removes the most recently observed symbol from the context tree
	 */
	public void revert() {
		// We can only revert if there is at least one symbol in the history.
		assert (history.size() > 0);

		// we need to access the history otherwise we can not tell which the
		// last inserted symbol was.
		Boolean sym = history.pop();
		remove(sym, history);
	}

	public void revert(int numSymbols) {
		assert (history.size() >= numSymbols);
		for (int i = 0; i < numSymbols; i++) {
			revert();
		}
	}

	/**
	 * shrinks the history down to a former size
	 * 
	 * @param newsize
	 */
	public void revertHistory(int newsize) {

		assert (newsize <= history.size());

		while (history.size() > newsize) {
			history.pop();
		}
	}

	/**
	 * generate a specified number of random symbols distributed according to
	 * the context tree statistics
	 * 
	 * @param bits
	 * @return
	 */
	public List<Boolean> genRandomSymbols(int bits) {
		List<Boolean> result = genRandomSymbolsAndUpdate(bits);
		// restore the context tree to it's original state
		for (int i = 0; i < bits; i++) {
			revert();
		}
		return result;
	}

	double eps = 1E-8;

	/**
	 * generate a specified number of random symbols distributed according to
	 * the context tree statistics and update the context tree with the newly
	 * generated bits. last predicted symbol has the highest index.
	 */
	public List<Boolean> genRandomSymbolsAndUpdate(int bits) {
		List<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < bits; i++) {
			boolean sampledSymbol = false;
			if (Math.random() <= predict(new Bits().one())) {
				sampledSymbol = true;
			}
			result.add(sampledSymbol);
			update(sampledSymbol);
		}
		return result;
	}

	/**
	 * the estimated probability of observing a particular sequence
	 * 
	 * @param symbols
	 * @return
	 */
	public double predict(List<Boolean> symbols) {

		double logProbBefore = logBlockProbability();

		// We calculate the probability for a '1' by first taking the ratio
		// between the probability
		// before we update the tree with '1' and afterwards. This then
		// yields the probability for
		// the symbol '1'

		// add all the symbols to the tree
		for (boolean sym : symbols) {
			update(sym);
		}

		double logProbAfter = logBlockProbability();
		assert (logProbAfter <= logProbBefore);

		// Make sure calculation is correct.
		// assert (probDebug <= 1.0 && probDebug >= 0.0);
		// assert (logProbAfter <= logProbBefore);

		for (int i = 0; i < symbols.size(); i++) {
			revert();
		}

		// equation (8) in the MC-AIXI-CTW paper: conditional probability for
		// the 'symbols' substring
		double logProbSym = logProbAfter - logProbBefore;

		double result = Math.exp(logProbSym);
		assert (result >= 0.0 && result <= 1.0);

		return result;
	}

	/**
	 * the logarithm of the block probability of the whole sequence
	 * 
	 * @return
	 */
	public double logBlockProbability() {
		double logProb = root.logPweight;
		assert (!Double.isNaN(logProb) && !Double.isInfinite(logProb));
		assert (Math.exp(logProb) >= 0.0 && Math.exp(logProb) <= 1.0);
		return logProb;
	}

	/**
	 * get the n'th most recent history symbol, n == 0 returns the most recent
	 * symbol.
	 * 
	 * @param n
	 * @return
	 */
	public boolean nthHistorySymbol(int n) {
		assert (n >= 0);
		assert (n < history.size());
		return history.get(history.size() - (n + 1));
	}

	public int historySize() {
		return history.size();
	}

	public int size() {
		return root.size();
	}

	public int depth() {
		return this.depth;
	}

	/*
	 * getters and setters for Entity.
	 */
	public Stack<Boolean> getHistory() {
		return history;
	}

	public void setHistory(Stack<Boolean> history) {
		this.history = history;
	}

	public CTNode getRoot() {
		return root;
	}

	public void setRoot(CTNode root) {
		this.root = root;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * returns the node corresponding to the given context. returns null if the
	 * node does not exist.
	 * 
	 * @param symbols
	 * @return
	 */
	public CTNode getNode(List<Boolean> symbols) {
		CTNode currNode = root;
		for (int i = 0; i < symbols.size(); i++) {
			boolean currSym = symbols.get(i);
			currNode = currSym ? currNode.getChild1() : currNode.getChild0();
			if (currNode == null) {
				return null;
			}
		}
		return currNode;
	}
}
