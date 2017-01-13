package ch.jjoller.mcaixictw;

import java.util.ArrayList;
import java.util.List;

public class Util {
	
	public static int bool2Int(boolean b) {
		return b ? 1 : 0;
	}

	public static String toString(List<Boolean> symlist) {
		String s = "";
		for (boolean b : symlist) {
			String one = b ? "1" : "0";
			s += one;
		}
		return s;
	}

	/**
	 * Return a random integer between [0, end)
	 * 
	 * @param range
	 * @return
	 */
	public static int randRange(int end) {
		return ((int) Math.round(Math.random() * end)) % end;
	}

	public static boolean randSym() {
		return Math.random() < 0.5 ? true : false;
	}

	/**
	 * Decodes the value encoded on the end of a list of symbols
	 * 
	 * @param symlist
	 * @return
	 */
	public static int decode(List<Boolean> symlist) {
		int value = 0;
		for (int i = 0; i < symlist.size(); i++) {
			int k = symlist.get(i) ? 1 : 0;
			value = 2 * value + k;
		}
		return value;
	}

	/**
	 * Encodes a value onto the end of a symbol list using "bits" symbols. the
	 * most significant bit is at position 0 of the list.
	 * 
	 * @param value
	 * @param bits
	 * @return
	 */
	public static List<Boolean> encode(int value, int bits) {
		List<Boolean> symlist = new ArrayList<Boolean>();
		for (int i = 0; i < bits; i++, value /= 2) {
			boolean sym = (value & 1) == 1 ? true : false;
			symlist.add(0, sym);
		}
		return symlist;
	}

	/**
	 * decrease the resolution of the given value to a number with the given
	 * number of bits. the given value should be a number between 0 and 1. for
	 * example if the number of bits is 3 then 1 would be encoded as 7 (111).
	 * 
	 * @param val
	 * @param bits
	 * @return
	 */
	public static int decreaseResolution(double val, int bits) {
		assert (val <= 1.0 && val >= 0.0);
		// maximal possible value, all bits set to 1.
		int max = (1 << bits) - 1;
		return (int) Math.round(val * max);
	}

	/**
	 * returns the number of bits needed to encode the given amount of states.
	 * 
	 * @param states
	 * @return
	 */
	public static int bitsNeeded(int states) {
		assert (states > 1);
		int i, c;
		for (i = 1, c = 1; i < states; i *= 2, c++) {
		}
		assert (c - 1 > 0);
		return c - 1;
	}

	public static boolean DebugOutput = false;

}

