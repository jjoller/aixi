package ch.jjoller.mcaixictw;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class UtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void randRange() {

		for (int i = 0; i < 1000; i++) {
			int range = 1 + (int) Math.round(Math.random() * 7);
			int r = Util.randRange(range);
			assertTrue(r < range && r >= 0);
		}
	}

	@Test
	public final void testDecode() {
		List<Boolean> list = new ArrayList<Boolean>();
		list.add(true);
		list.add(true);
		list.add(false);
		list.add(true);
		assertTrue(Util.decode(list) == 13);
	}

	@Test
	public final void testEncode() {
		List<Boolean> list = Util.encode(13, 4);
		assertTrue(list.get(0) == true);
		assertTrue(list.get(1) == true);
		assertTrue(list.get(2) == false);
		assertTrue(list.get(3) == true);
	}

}
