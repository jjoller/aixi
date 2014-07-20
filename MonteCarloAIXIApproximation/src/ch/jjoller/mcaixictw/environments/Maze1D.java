package ch.jjoller.mcaixictw.environments;

import ch.jjoller.mcaixictw.Environment;
import ch.jjoller.mcaixictw.Util;


/**
 * 1d-maze. The 1d-maze is a simple problem from the work of Cassandra,
 * Kaelbling, and Littman (1994). The agent begins at a random, non-goal
 * location within a 1 × 4 maze. There is a choice of two actions: left or
 * right. Each action transfers the agent to the adjacent cell if it exists,
 * otherwise it has no effect. If the agent reaches the third cell from the
 * left, it receives a reward of 1. Otherwise it receives a reward of 0. The
 * distinguishing feature of this problem is that the observations are
 * uninformative; every observation is the same regardless of the agent’s actual
 * location.
 */
public class Maze1D extends Environment {
	private final int REWARD_POSITION = 2;
	private int reward = 0;
	private int observation = 0;
	private int position = 0;

	public Maze1D() {
		startAtNewPosition();
		reward = 0;
		// observation is is always the same.
		observation = 0;
	}

	private void startAtNewPosition() {
		// Start at a random position except for the reward position
		do {
			position = Util.randRange(4);
		} while (position == REWARD_POSITION);
	}

	@Override
	public void performAction(int action) {
		reward = 0;
		if (action == 0) {
			// Go left until we hit the wall
			if (position > 0) {
				position--;
			}
		} else {
			// Go right until we hit the wall
			if (position < 3) {
				position++;
			}
		}

		// If the agent is at the REWARD_POSITION give him a reward and restart
		// the game
		if (position == REWARD_POSITION) {
			reward = 1;
			startAtNewPosition();
		}
	}

	@Override
	public int getObservation() {
		return observation;
	}

	@Override
	public int getReward() {
		return reward;
	}

	@Override
	public int numActions() {
		return 2;
	}

	@Override
	public double neutralReward() {
		return 0;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public String environmentName() {
		return "1d-Maze";
	}

	@Override
	public int observationBits() {
		return 1;
	}

	@Override
	public int rewardBits() {
		return 1;
	}
}