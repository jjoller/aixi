package ch.jjoller.mcaixictw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * one node of the upper confidence bound applied to trees algorithm.
 */
public class UCTNode {

	public UCTNode(boolean isChanceNode) {
		this.isChanceNode = isChanceNode;
		visits = 0;
		mean = 0;
		children = new TreeMap<Integer, UCTNode>();
	}

	private Map<Integer, UCTNode> children; // stores the children
	private boolean isChanceNode; // true if this node is a chance node
	private double mean; // the expected reward of this node
	private int visits; // number of times the search node has been visited
	private double explorationRatio = 1.41; // Exploration-Exploitation
											// constant

	/**
	 * Returns the action with the highest expected reward.
	 * 
	 * @return
	 */
	public int bestAction() {
		int bestAction = 0;
		double maxReward = Double.MIN_VALUE;
		Iterator<Entry<Integer, UCTNode>> iter = children.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<Integer, UCTNode> curr = iter.next();
			double expectedReward = curr.getValue().mean;
			if (expectedReward > maxReward) {
				maxReward = expectedReward;
				bestAction = curr.getKey();
			}
		}
		return bestAction;
	}

	/**
	 * determine the next action to play
	 * 
	 * @param agent
	 * @param dfr
	 * @return
	 */
	private int actionSelect(Agent agent, int dfr) {
		assert (agent.numActions() >= children.size());

		double maxValue = Double.MIN_VALUE;
		int selectedAction = 0;

		// If we haven't explored all possible actions, choose one uniformly
		// at random
		if (children.size() < agent.numActions()) {
			List<Integer> unexplored = new ArrayList<Integer>();
			for (int a = 0; a < agent.numActions(); a++) {
				if (!children.containsKey(a)) {
					unexplored.add(a);
				}
			}
			selectedAction = unexplored.get(Util.randRange(unexplored.size()));
		} else {
			// The general idea is to explore the most promising(with the
			// highest expected reward) actions. But also
			// explore other actions not to get stuck with wrong decisions.

			Iterator<Entry<Integer, UCTNode>> it = children.entrySet()
					.iterator();

			while (it.hasNext()) {

				Entry<Integer, UCTNode> curr = it.next();
				UCTNode currNode = curr.getValue();

				double value = 1.0
						/ (double) (dfr * (agent.maxReward() - agent
								.minReward()))
						* curr.getValue().expectation()
						+ explorationRatio
						* Math.sqrt(Math.log((double) visits())
								/ (double) currNode.visits());

				if (value > maxValue) {
					maxValue = value;
					selectedAction = curr.getKey();
				}
			}

		}
		return selectedAction;
	}

	/**
	 * the expected reward of this node.
	 * 
	 * @return expected reward
	 */
	double expectation() {
		return mean;
	}

	/**
	 * perform a sample run through this node and it's m_children, returning the
	 * accumulated reward from this sample run.
	 * 
	 * @param agent
	 * @param m
	 *            remaining horizon
	 * @param ctUpdate
	 *            update CTW
	 * @return accumulated reward
	 */
	public double sample(Agent agent, int m) {

		ModelUndo undo = new ModelUndo(agent);

		double futureTotalReward;

		if (m == 0) {
			// we have reached the horizon of the agent
			return agent.reward();
		} else if (isChanceNode) {
			int p = agent.genPerceptAndUpdate();
			if (!children.containsKey(p)) {
				children.put(p, new UCTNode(false));
			}
			futureTotalReward = children.get(p).sample(agent, m - 1);
		} else if (visits == 0) {
			futureTotalReward = rollout(agent, m);
		} else {
			int a = actionSelect(agent, m);
			if (!children.containsKey(a)) {
				children.put(a, new UCTNode(true));
			}
			agent.modelUpdate(a);
			futureTotalReward = children.get(a).sample(agent, m);
		}

		// Calculate the expected average reward
		double reward = futureTotalReward - undo.getReward();

		// update the mean reward
		mean = 1.0 / (double) (visits + 1) * (reward + (double) visits * mean);

		visits++;

		// System.out.println("m: " + m + " visits: " + visits + " mean: " +
		// mean
		// + " sample rew: " + reward + " future tot rew: "
		// + futureTotalReward + " undo.getRew: " + undo.getReward());

		agent.modelRevert(undo);

		assert (undo.getAge() == agent.age());
		assert (undo.getHistorySize() == agent.historySize());
		assert (undo.getReward() == agent.reward());
		assert (undo.isLastUpdatePercept() == agent.lastUpdatePercept());

		return futureTotalReward;
	}

	/**
	 * number of times the search node has been visited
	 */
	int visits() {
		return visits;
	}

	/**
	 * simulate a path through a hypothetical future for the agent within it's
	 * internal model of the world, returning the accumulated reward.
	 * 
	 * @param agent
	 * @param rolloutLength
	 * @param ctUpdate
	 * @return accumulated reward
	 */
	private double rollout(Agent agent, int rolloutLength) {
		assert (!isChanceNode);
		assert (rolloutLength > 0);
		for (int i = 0; i < rolloutLength; i++) {
			agent.genRandomActionAndUpdate();
			agent.genPerceptAndUpdate();
		}
		return agent.reward();
	}

	/**
	 * returns the subtree rooted at [root,action,percept]. If that subtree does
	 * not exist the tree is cleared and a new search tree is returned.
	 * 
	 * @param action
	 * @param percept
	 * @return
	 */
	public UCTNode getSubtree(int action, int percept) {
		assert (!isChanceNode);
		assert (children.containsKey(action));

		UCTNode chanceNode = children.get(action);

		if (chanceNode.children.containsKey(percept)) {
			return chanceNode.children.get(percept);
		} else {
			return new UCTNode(false);
		}
		/*
		 * 
		 * NodeSearch afterAction, afterPercept; boolean found = false;
		 * afterAction = child(action); if(afterAction != NULL) { afterPercept =
		 * afterAction->child(percept); if(afterPercept != NULL) { found = true;
		 * } }
		 * 
		 * if(found) { afterAction->m_children[percept] = 0; delete root; return
		 * afterPercept; } else { delete root; return new NodeSearch(false); }
		 */
	}
}