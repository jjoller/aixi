package ch.jjoller.mcaixictw;

/**
 * provides ability to perform monte carlo UCT searches.
 */
public class UCTSearch {

	private UCTNode uctRoot;

	/**
	 * determine the best action by searching ahead using MCTS
	 * 
	 * @param agent
	 * @return
	 */
	public int search(Agent agent, UCTSettings settings) {

		if (uctRoot == null) {
			uctRoot = new UCTNode(false);
		} else {
			uctRoot = uctRoot.getSubtree(agent.getLastAction(),
					agent.getLastPercept());
		}
		for (int i = 0; i < settings.getMcSimulations(); i++) {
			// Sample a path trough the possible future
			uctRoot.sample(agent, settings.getHorizon());
		}
		// The currently best action according to the agent's model of the
		// future
		int bestAction = uctRoot.bestAction();

		// If we do not want to recycle the tree just throw it away
		if (!settings.isRecycleUCT()) {
			uctRoot = null;
		}
		return bestAction;
	}
}
