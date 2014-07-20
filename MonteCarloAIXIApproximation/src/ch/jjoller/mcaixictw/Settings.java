package ch.jjoller.mcaixictw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class Settings implements Cloneable {

	/**
	 * load standard settings
	 */
	public Settings() {
		loadDefaultSettings();
	}

	/**
	 * parse from file
	 * 
	 * @param path
	 *            path to file
	 */
	public Settings(String path) {
		loadDefaultSettings();
		File file = new File(path);
		parseSettings(file);
	}

	public abstract void parseSettings(File file);

	public abstract void loadDefaultSettings();

	public abstract Settings clone();

	/**
	 * parse occurrence of the form "parameter = true" and return the value on
	 * the right side of the "=". Return the default (def) value if no
	 * occurrence found.
	 * 
	 * @param parameter
	 * @param def
	 * @param file
	 * @return
	 */
	protected boolean parseBoolean(String parameter, boolean def, File file) {
		try (Scanner scanner = new Scanner(file)) {
			String s = scanner.findWithinHorizon(parameter
					+ "\\p{Space}*=\\p{Space}*(true|false)", 10000);
			if (s != null) {
				s = s.split("=")[1].trim();
				return s.equals("true") ? true : false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * parse occurrence of the form "parameter = 10" and return the value on the
	 * right side of the "=". Return the default (def) value if no occurrence
	 * found.
	 * 
	 * @param parameter
	 * @param def
	 * @param file
	 * @return
	 */
	protected int parseInt(String parameter, int def, File file) {
		try (Scanner scanner = new Scanner(file)) {
			String s = scanner.findWithinHorizon(parameter
					+ "\\p{Space}*=\\p{Space}*\\d+", 10000);
			if (s != null) {
				return new Integer(s.split("=")[1].trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * parse occurrence of the form "parameter = 10.72" and return the value on
	 * the right side of the "=". Return the default (def) value if no
	 * occurrence found.
	 * 
	 * @param parameter
	 * @param def
	 * @param file
	 * @return
	 */
	protected double parseDouble(String parameter, double def, File file) {
		try (Scanner scanner = new Scanner(file)) {
			String s = scanner.findWithinHorizon(parameter
					+ "\\p{Space}*=\\p{Space}*\\d+\\.\\d+", 10000);
			if (s != null) {
				return new Double(s.split("=")[1].trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return def;
	}

	public abstract String toString();

	// public String toString() {
	// String s = "Agent Settings\n==============\n";
	// s += "ctDepth: " + ctDepth + "\n";
	// s += "horizon: " + horizon + "\n";
	// s += "exploration: " + exploration + "\n";
	// s += "exploreDecay: " + exploreDecay + "\n";
	// s += "mcSimulations: " + mcSimulations + "\n";
	// s += "terminationAge: " + terminationAge + "\n";
	// s += "recycleUCT: " + recycleUCT + "\n";
	// s += "factorialTree: " + factorialTree + "\n";
	// s += "updateCTinMC: " + updateCTinMC + "\n";
	// return s;
	// }

}
