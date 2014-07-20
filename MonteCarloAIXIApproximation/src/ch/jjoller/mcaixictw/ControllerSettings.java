package ch.jjoller.mcaixictw;

import java.io.File;

/**
 * Provides some parameters of how to interact with the environment.
 */
public class ControllerSettings extends Settings {

	public ControllerSettings() {
		super();
	}

	public ControllerSettings(String path) {
		super(path);
	}

	public String toString() {
		String result = "";
		result += "exploration: " + exploration + "\n";
		result += "exploreDecay: " + exploreDecay + "\n";
		return result;
	}

	public void parseSettings(File file) {
		exploration = parseDouble("exploration", exploration, file);
		exploreDecay = parseDouble("explore-decay", exploreDecay, file);
	}

	public void loadDefaultSettings() {
		this.exploration = 0.1;
		exploreDecay = 0.999;
	}

	@Override
	public ControllerSettings clone() {
		ControllerSettings s = new ControllerSettings();
		s.setExploration(exploration);
		s.setExploreDecay(exploreDecay);
		return s;
	}

	/*
	 * choose random actions at this level
	 */
	private double exploration;

	/*
	 * multiply exploration with this number after each cycle
	 */
	private double exploreDecay;

	public double getExploration() {
		return exploration;
	}

	public void setExploration(double exploration) {
		this.exploration = exploration;
	}

	public double getExploreDecay() {
		return exploreDecay;
	}

	public void setExploreDecay(double exploreDecay) {
		this.exploreDecay = exploreDecay;
	}

	public static ControllerSettings TEXAS_LIMIT_SETTINGS() {
		ControllerSettings s = new ControllerSettings();
		s.setExploration(0.1);
		s.setExploreDecay(0.999);
		return s;
	}
}
