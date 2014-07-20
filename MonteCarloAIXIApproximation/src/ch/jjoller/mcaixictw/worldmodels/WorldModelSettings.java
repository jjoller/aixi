package ch.jjoller.mcaixictw.worldmodels;

import java.io.File;

import ch.jjoller.mcaixictw.Settings;

/**
 * Provides some parameters of how to build the model of the world.
 */
public class WorldModelSettings extends Settings {

	public WorldModelSettings() {
		super();
	}

	public WorldModelSettings(String file) {
		super(file);
	}

	private int depth;
	private boolean facContextTree;

	public String toString() {
		String s = "depth: " + depth + "\n";
		s += "facContextTree: " + facContextTree;
		return s;
	}

	@Override
	public void parseSettings(File file) {
		depth = parseInt("ct-depth", depth, file);
		facContextTree = parseBoolean("factorial-tree", facContextTree, file);
	}

	public void loadDefaultSettings() {
		this.depth = 10;
		this.facContextTree = true;
	}

	@Override
	public WorldModelSettings clone() {
		WorldModelSettings r = new WorldModelSettings();
		r.setDepth(depth);
		r.setFacContextTree(facContextTree);
		return r;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean isFacContextTree() {
		return facContextTree;
	}

	public void setFacContextTree(boolean facContextTree) {
		this.facContextTree = facContextTree;
	}

}
