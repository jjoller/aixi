package ch.jjoller.mcaixictw;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.jjoller.mcaixictw.environments.CoinFlipEnv;
import ch.jjoller.mcaixictw.worldmodels.WorldModelSettings;
import ch.jjoller.mcaixictw.worldmodels.Worldmodel;

public class RunAgentTest {

	private static Logger log = Logger.getLogger(RunAgentTest.class
			.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// set up the biased coin environment. The coin lands on one side with a
		// probability of 0.7.
		env = new CoinFlipEnv(p_biased_coin);

		WorldModelSettings modelSettings = new WorldModelSettings();
		modelSettings.setFacContextTree(true);
		modelSettings.setDepth(3);
		log.info("depth: " + modelSettings.getDepth());
		log.info("create new model");
		Worldmodel model = Worldmodel.getInstance(ctName, modelSettings);
		controller = new AgentController(env, controllerSettings, uctSettings,
				model);

	}

	private AgentController controller;
	private String ctName = "CoinFlipTraining";
	private double p_biased_coin = 0.7;
	private Environment env;
	private ControllerSettings controllerSettings = new ControllerSettings();
	private UCTSettings uctSettings = new UCTSettings();

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public final void test() {

		int n = 10000;
		log.info("Play " + n + " rounds against the biased coin environment");

		// A smart agent should learn to always choose the biased side and
		// should come close to an average reward of 0.7
		controller.play(n, false);
		
	}

}
