package ch.jjoller.mcaixictw.worldmodels;

/**
 * A node of a context tree.
 *
 */
public class CTNode {

	public CTNode() {
		logPest = 0.0;
		logPweight = 0.0;
		count0 = 0;
		count1 = 0;
	}

	private long id;
	protected double logPest; // log KT estimated probability
	protected double logPweight; // log weighted block probability
	// one slot for each symbol a,b in CTW literature
	private int count0;
	private int count1;
	private CTNode child0;
	private CTNode child1;
	protected CTNode parent;

	public int count(boolean sym) {
		return sym ? count1 : count0;
	}

	public void incrCount(boolean sym) {
		if (sym) {
			count1++;
		} else {
			count0++;
		}
	}

	public void decrCount(boolean sym) {
		if (sym) {
			count1--;
		} else {
			count0--;
		}
	}

	public CTNode child(boolean sym) {
		return sym ? child1 : child0;
	}

	public void setChild(boolean sym, CTNode child) {
		if (child != null) {
			child.setParent(this);
		}
		if (sym) {
			child1 = child;
		} else {
			child0 = child;
		}
	}

	/**
	 * compute the logarithm of the KT-estimator update multiplier
	 * 
	 * @param sym
	 * @return
	 */
	double logKTMul(boolean sym) {
		double result = Math.log((double) count(sym) + 0.5)
				- Math.log((double) (count(false) + count(true) + 1));
		assert (result <= 0);
		return result;
	}

	// 1 + number of descendants of a node in the context tree
	public int size() {
		int rval = 1;
		rval += child(false) != null ? child(false).size() : 0;
		rval += child(true) != null ? child(true).size() : 0;
		return rval;
	}

	public String toString() {
		String out = "";
		out += "[a=" + count(false) + ",b=" + count(true) + ",p="
				+ Math.exp(logPweight) + "]";
		return out;
	}

	/*
	 * getters and setters are just here because this class is an Entity.
	 */

	public double getLogProbEst() {
		return logPest;
	}

	public void setLogProbEst(double logProbEst) {
		this.logPest = logProbEst;
	}

	public double getLogProbWeighted() {
		return logPweight;
	}

	public void setLogProbWeighted(double logProbWeighted) {
		this.logPweight = logProbWeighted;
	}

	public int getCount0() {
		return count0;
	}

	public void setCount0(int count0) {
		this.count0 = count0;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public CTNode getChild0() {
		return child0;
	}

	public void setChild0(CTNode child0) {
		this.child0 = child0;
	}

	public CTNode getChild1() {
		return child1;
	}

	public void setChild1(CTNode child1) {
		this.child1 = child1;
	}

	public CTNode getParent() {
		return parent;
	}

	public void setParent(CTNode parent) {
		this.parent = parent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
