package ch.jjoller.mcaixictw;

/**
 * Representation of the world. An agent does interact with the world through
 * the Environment interface.
 * 
 */
public abstract class Environment {

	/**
	 * perform an action on the environment.
	 * 
	 * @param action
	 */
	public abstract void performAction(int action);

	/**
	 * current observation.
	 * 
	 * @return
	 */
	public abstract int getObservation();

	/**
	 * current reward.
	 * 
	 * @return
	 */
	public abstract int getReward();

	/**
	 * number of actions the agent can perform on this environment.
	 * 
	 * @return
	 */
	public abstract int numActions();

	/**
	 * bits necessary to encode an observation. there are 2^observationBits
	 * distinct observations.
	 * 
	 * @return
	 */
	public abstract int observationBits();

	/**
	 * bits necessary to encode a reward. there are 2^rewardBits distinct
	 * rewards.
	 * 
	 * @return
	 */
	public abstract int rewardBits();

	/**
	 * rewards are interpreted as unsigned integers. this means in the internal
	 * world of the agent there are actually no negative rewards. but in real
	 * world there are of course negative rewards. this value can be used to do
	 * calculate back the real world meaning of the reward.
	 * 
	 * @return
	 */
	protected abstract double neutralReward();

	/**
	 * return true if the environment wants to decide to end the game.
	 * 
	 * @return
	 */
	public abstract boolean isFinished();

	/**
	 * the name of the environment.
	 * 
	 * @return
	 */
	public abstract String environmentName();

	public String toString() {
		String s = "";
		s += "Environment: " + environmentName() + "\n";
		s += "numActions: " + numActions() + "\n";
		s += "neutralReward: " + neutralReward() + "\n";
		return s;
	}

}
