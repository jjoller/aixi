package ch.jjoller.mcaixictw.environments;

import ch.jjoller.mcaixictw.Environment;


/**
 * Environment flips a biased coin. Get reward of 1 if correctly guessing the
 * outcome of the coin flip.
 */
public class CoinFlipEnv extends Environment {

	public CoinFlipEnv(double coinFlipP) {
		assert (coinFlipP >= 0 && coinFlipP <= 1);
		this.coinFlipP = coinFlipP;
	}

	private double coinFlipP;
	private int obs = 0;
	private int rew = 0;

	@Override
	public void performAction(int action) {
		obs = Math.random() < coinFlipP ? 1 : 0;
		rew = action == obs ? 1 : 0;
	}

	@Override
	public int getObservation() {
		return obs;
	}

	@Override
	public int getReward() {
		return rew;
	}

	@Override
	public boolean isFinished() {
		return false;
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
	public String environmentName() {
		return "CoinFlip";
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