package ch.jjoller.mcaixictw;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.jjoller.mcaixictw.worldmodels.ContextTree;
import ch.jjoller.mcaixictw.worldmodels.WorldModelSettings;
import ch.jjoller.mcaixictw.worldmodels.Worldmodel;


public class ContextTreeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		WorldModelSettings settings = new WorldModelSettings();
		settings.setFacContextTree(false);
		settings.setDepth(2);
		ct = Worldmodel.getInstance("ContextTreeTestModel", settings);
		// List<Boolean> list = new ArrayList<Boolean>();
		// for (int i = 0; i < 1000; i++) {
		// list.add(Util.randSym());
		// }
		// ct.update(list);
	}

	double eps = 1E-8; // tolerance
	Worldmodel ct;

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * check that the probability for 0 and 1 always sums up to 1.
	 * 
	 * @param model
	 */
	private void sumUpTo1(ContextTree model) {
		double p_1 = model.predict(new Bits().one());
		double p_0 = model.predict(new Bits().zero());
		System.out.println("p_1: " + p_1 + " p_0: " + p_0);
		assertTrue(Math.abs(1.0 - (p_1 + p_0)) < eps);
	}

	@Test
	public final void testFromFAISlides() {
		// http://www.hutter1.net/ethz/slides-ctw.pdf
		// page 14

		WorldModelSettings settings = new WorldModelSettings();
		settings.setFacContextTree(false);
		settings.setDepth(3);
		ContextTree ct = (ContextTree) Worldmodel.getInstance(
				"ContextTreeTestModel", settings);
		sumUpTo1(ct);

		List<Boolean> past, context;

		// 110
		past = new ArrayList<Boolean>();
		past.add(true);
		past.add(true);
		past.add(false);

		ct.updateHistory(past);
		sumUpTo1(ct);

		// 0100110
		ct.update(new Bits().zero());
		sumUpTo1(ct);
		ct.update(new Bits().one());
		sumUpTo1(ct);
		ct.update(new Bits().zero());
		sumUpTo1(ct);
		ct.update(new Bits().zero());
		sumUpTo1(ct);
		ct.update(new Bits().one());
		sumUpTo1(ct);
		ct.update(new Bits().one());
		sumUpTo1(ct);
		ct.update(new Bits().zero());
		sumUpTo1(ct);

		System.out.println(ct);

		// root
		assertTrue(equals(Math.exp(ct.getRoot().getLogProbWeighted()),
				7.0 / 2048.0));

		// first level
		context = new ArrayList<Boolean>();
		context.add(true);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 16.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				9.0 / 128.0));

		// second level
		context = new ArrayList<Boolean>();
		context.add(true);
		context.add(true);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 2.0));

		context = new ArrayList<Boolean>();
		context.add(true);
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 8.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		context.add(true);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				5.0 / 16.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				3.0 / 8.0));

		// third level
		context = new ArrayList<Boolean>();
		context.add(true);
		context.add(true);
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 2.0));

		context = new ArrayList<Boolean>();
		context.add(true);
		context.add(false);
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 8.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		context.add(true);
		context.add(true);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 2.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		context.add(true);
		context.add(false);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				1.0 / 2.0));

		context = new ArrayList<Boolean>();
		context.add(false);
		context.add(false);
		context.add(true);
		assertTrue(equals(Math.exp(ct.getNode(context).getLogProbWeighted()),
				3.0 / 8.0));

	}

	private boolean equals(double v1, double v2) {
		double diff = Math.abs(v1 - v2);
		if (diff < eps) {
			System.out.println("equal v1: " + v1 + " v2: " + v2);
			return true;
		} else {
			System.out.println("not equal v1: " + v1 + " v2: " + v2);
		}
		return false;
	}

	@Test
	public final void test0() {

		List<Boolean> symbols, sym1, sym0;

		symbols = new ArrayList<Boolean>();
		boolean b = true;
		symbols.add(b);
		symbols.add(!b);
		// symbols.add(b);
		// symbols.add(b);
		// symbols.add(b);
		// symbols.add(!b);
		// symbols.add(b);
		// symbols.add(!b);

		ct.update(symbols);
		System.out.println("updated");

		sym1 = new ArrayList<Boolean>();
		sym0 = new ArrayList<Boolean>();
		sym1.add(true);
		sym0.add(false);
		double p1, p0;
		p1 = ct.predict(sym1);
		p0 = ct.predict(sym0);
		System.out.println("p_1: " + p1 + " p_0: " + p0);

		System.out.println(ct);

		// for (int i = 0; i < 2; i++) {
		//
		// ct.update(sym1);
		// System.out.println("updated");
		//
		// double p1, p0;
		//
		// p1 = ct.predict(sym1);
		// p0 = ct.predict(sym0);
		//
		// System.out.println("p_1: " + p1 + " p_0: " + p0);
		// }

	}

	@Test
	public final void test1() {

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(false);
		for (int i = 0; i < 25; i++) {
			ct.update(list);
		}

		list.clear();
		list.add(true);
		double p1 = ct.predict(list);

		for (int i = 0; i < 7; i++) {
			ct.update(list);
		}

		double p2 = ct.predict(list);
		assertTrue(p2 > p1);
	}

	@Test
	public final void test2() {

		WorldModelSettings settings = new WorldModelSettings();
		settings.setFacContextTree(false);
		int depth = 2;
		settings.setDepth(depth);

		ContextTree ct = (ContextTree) Worldmodel.getInstance(
				"ContextTreeTestModel", settings);

		// if there isn't a history of length = depth, the predictions of the
		// context tree won't sum up to 1.
		
		
		ct.updateHistory(new Bits().rand(depth));
		System.out.println("empty");
		System.out.println(ct);

		this.sumUpTo1(ct);

		System.out.println("update with 1: ");
		ct.update(new Bits().one());
		this.sumUpTo1(ct);

		ct.update(new Bits().zero());

		System.out.println(ct);

		System.out.println("update with 0: ");
		ct.update(new Bits().one());
		this.sumUpTo1(ct);

		System.out.println(ct);

		System.out.println("revert");
		ct.revert();
		this.sumUpTo1(ct);

		System.out.println(ct);

		System.out.println("update with 1");
		ct.update(new Bits().one());
		this.sumUpTo1(ct);

		System.out.println(ct);

		System.out.println("revert");
		ct.revert();
		this.sumUpTo1(ct);

		System.out.println(ct);

		System.out.println("revert");
		ct.revert();
		this.sumUpTo1(ct);
		System.out.println(ct);

	}

	@Test
	public final void test3() {
		WorldModelSettings settings = new WorldModelSettings();
		settings.setFacContextTree(false);
		int depth = 10;
		settings.setDepth(depth);

		ContextTree ct = (ContextTree) Worldmodel.getInstance(
				"ContextTreeTestModel", settings);
		Bits history = new Bits();
		for (int i = 0; i < depth; i++) {
			history.rand();
		}
		ct.updateHistory(history);
		int testLength = 1000;
		for (int i = 0; i < testLength; i++) {
			ct.update(new Bits().rand());
			sumUpTo1(ct);
		}
	}

	@Test
	public final void testToString() {

		String r = ct.toString();

		System.out.println(r);

		assertTrue(r != null);
	}

	@Test
	public final void testUpdateBoolean() {

		boolean toPredict = false;

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(toPredict);

		double p1 = ct.predict(list);

		List<Boolean> list2 = new ArrayList<Boolean>();

		int symbols = 100;
		for (int i = 0; i < symbols; i++) {
			list2.add(Util.randSym());
		}
		ct.update(list2);
		ct.revert(list2.size());

		double p3 = ct.predict(list);

		assertTrue(p1 - p3 < eps);
	}

	@Test
	public final void testUpdateListOfBoolean() {

		List<Boolean> list = new ArrayList<Boolean>();

		list.add(true);
		list.add(false);
		list.add(true);

		List<Boolean> listTrue = new ArrayList<Boolean>();
		listTrue.add(true);

		double p1 = ct.predict(listTrue);

		ct.update(list);
		ct.revert(3);

		assertTrue(p1 - ct.predict(listTrue) < eps);
	}

	@Test
	public final void testUpdateHistory() {

		List<Boolean> listTrue = new ArrayList<Boolean>();
		listTrue.add(true);
		double p1 = ct.predict(listTrue);

		int s1 = ct.historySize();

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(true);
		list.add(false);
		list.add(true);

		ct.updateHistory(list);

		assertTrue(ct.historySize() == s1 + list.size());

		ct.revertHistory(s1);

		assertTrue(ct.historySize() == s1);
		assertTrue(ct.predict(listTrue) - p1 < eps);
	}

	@Test
	public final void testRevert() {

		List<Boolean> listTrue = new ArrayList<Boolean>();
		listTrue.add(true);

		List<Boolean> listFalse = new ArrayList<Boolean>();
		listFalse.add(false);

		double pTrue = ct.predict(listTrue);
		double pFalse = ct.predict(listFalse);

		int numSymbols = 100;
		List<Boolean> list = new ArrayList<Boolean>();
		for (int i = 0; i < numSymbols; i++) {
			list.add(Util.randSym());
		}

		ct.update(list);
		ct.revert(list.size());

		assertTrue(ct.predict(listTrue) - pTrue < eps);
		assertTrue(ct.predict(listFalse) - pFalse < eps);
	}

	@Test
	public final void testRevertHistory() {

		List<Boolean> list = new ArrayList<Boolean>();
		int s1 = ct.historySize();
		int numSymbols = 100;
		for (int i = 0; i < numSymbols; i++) {
			list.add(Util.randSym());
		}
		double p = ct.predict(list);
		ct.updateHistory(list);
		ct.revertHistory(s1);
		assertTrue(p - ct.predict(list) < eps);
	}

	@Test
	public final void testGenRandomSymbols() {

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(true);
		double p1 = ct.predict(list);

		ct.genRandomSymbols(10);

		assertTrue(ct.predict(list) - p1 < eps);
	}

	@Test
	public final void testGenRandomSymbolsAndUpdate() {

		int s1 = ct.historySize();

		int numSymbols = 11;
		ct.genRandomSymbolsAndUpdate(numSymbols);

		assertTrue(ct.historySize() == s1 + numSymbols);

	}

	@Test
	public final void testPredictBoolean() {

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		double p = ct.predict(list);

		for (int i = 0; i < 100; i++) {
			// test whether it always returns the same probability.
			assertTrue(ct.predict(list) - p < eps);
		}

		double pTrue = ct.predict(list);
		list.clear();
		list.add(false);
		double pFalse = ct.predict(list);

		assertTrue(pTrue + pFalse - 1.0 < eps);
	}

	@Test
	public final void testPredictListOfBoolean() {

		List<Boolean> list = new ArrayList<Boolean>();
		list.add(true);
		list.add(false);
		list.add(true);
		list.add(false);

		double p1 = ct.predict(list);

		List<Boolean> updateList = new ArrayList<Boolean>();

		int numSymbols = 100;
		for (int i = 0; i < numSymbols; i++) {
			updateList.clear();
			updateList.add(Util.randSym());
			ct.update(updateList);
		}
		for (int i = 0; i < numSymbols; i++) {
			ct.revert(1);
		}

		assertTrue(ct.predict(list) - p1 < eps);
	}

	@Test
	public final void testNthHistorySymbol() {

		List<Boolean> updateList = new ArrayList<Boolean>();
		updateList.add(false);
		updateList.add(false);
		updateList.add(true);
		updateList.add(false);
		updateList.add(false);

		ct.update(updateList);

		assertTrue(!ct.nthHistorySymbol(0));
		assertTrue(!ct.nthHistorySymbol(1));
		assertTrue(ct.nthHistorySymbol(2));
		assertTrue(!ct.nthHistorySymbol(3));
		assertTrue(!ct.nthHistorySymbol(4));

	}

}
