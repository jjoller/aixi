package ch.jjoller.mcaixictw;

import java.util.ArrayList;

/**
 * representation of a bit string
 */
public class Bits extends ArrayList<Boolean> {

	public Bits() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8828268179061588441L;

	public Bits one() {
		add(true);
		return this;
	}

	public Bits zero() {
		add(false);
		return this;
	}

	public Bits rand() {
		add(Util.randSym());
		return this;
	}

	public Bits rand(int length) {
		for (int i = 0; i < length; i++) {
			add(Util.randSym());
		}
		return this;
	}

}