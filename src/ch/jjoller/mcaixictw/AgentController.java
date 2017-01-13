package ch.jjoller.mcaixictw;

import java.io.PrintWriter;
import java.util.logging.Logger;

import ch.jjoller.mcaixictw.worldmodels.Worldmodel;


/**
 * 
 * Manage interaction of the agent with the environment.
 * 
 */
public class AgentController {
	
	private static final Logger log = Logger.getLogger(AgentController.class
			.getName());

	/**
	 * use the given instance of WorldModel
	 * 
	 * @param env
	 * @param agentSettings
	 * @param uctSettings
	 * @param model
	 */
	public AgentController(Environment env, ControllerSettings agentSettings,
			UCTSettings uctSettings, Worldmodel model) {
		this.uctSettings = uctSettings;
		this.agentSettings = agentSettings;
		this.environment = env;
		agent = new Agent(environment.numActions(), env.observationBits(),
				env.rewardBits(), model);
		search = new UCTSearch();
	}

	public String toString() {
		String limiter = "=====================\n";
		String result = limiter;
		result += agentSettings;
		result += uctSettings;
		result += agent.getModel();
		result += "cycle: " + cycle + "\n";
		result += "avgRew: " + agent.averageReward() + "\n";
		result += limiter;
		return result;
	}

	private Environment environment;
	private Agent agent;
	private UCTSettings uctSettings;
	private ControllerSettings agentSettings;
	private UCTSearch search;
	private PrintWriter csv;
	private int cycle = 1;

	/**
	 * interact with the environment. if the random flag is true the agent will
	 * choose a random action on every cycle. Otherwise the agent will either
	 * use the UCT algorithm to estimate the optimal action or it will choose a
	 * random action with the probability given by the agentSettings
	 * (exploration).
	 * 
	 * @param cycles
	 * @param random
	 */
	public void play(int cycles, boolean random) {
		System.out.println("play " + cycles + " cycles");
		System.out.println(this);
		for (int k = 0; k < cycles; k++) {
			play(random);
		}
	}

	/**
	 * same as the other play function but play just one cycle.
	 * 
	 * @param random
	 */
	public void play(boolean random) {
		int obs = environment.getObservation();
		int rew = environment.getReward();
		agent.modelUpdate(obs, rew);
		boolean explore = (Math.random() < agentSettings.getExploration())
				|| random ? true : false;
		int action;
		if (explore) {
			action = agent.genRandomActionAndUpdate();
		} else {
			action = search.search(agent, uctSettings);
			agent.modelUpdate(action);
		}
		environment.performAction(action);
		
		log.fine(cycle + "," + obs + "," + rew + "," + action + ","
				+ explore + "," + agentSettings.getExploration() + ","
				+ agent.reward() + "," + agent.averageReward());
		
		if ((cycle & (cycle - 1)) == 0) {
			System.out.println(this);
		}
		agentSettings.setExploration(agentSettings.getExploration()
				* agentSettings.getExploreDecay());
		cycle++;
	}

	/**
	 * dictate observation, reward and action. can be used for training
	 * purposes.
	 * 
	 * @param obs
	 * @param rew
	 * @param action
	 */
	public void dictate(int obs, int rew, int action) {
		agent.modelUpdate(obs, rew);
		agent.modelUpdate(action);
	}

	public Worldmodel getModel() {
		return agent.getModel();
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public UCTSettings getUctSettings() {
		return uctSettings;
	}

	public void setUctSettings(UCTSettings uctSettings) {
		this.uctSettings = uctSettings;
	}

	public ControllerSettings getAgentSettings() {
		return agentSettings;
	}

	public void setAgentSettings(ControllerSettings agentSettings) {
		this.agentSettings = agentSettings;
	}

	public UCTSearch getSearch() {
		return search;
	}

	public void setSearch(UCTSearch search) {
		this.search = search;
	}

	public PrintWriter getCsv() {
		return csv;
	}

	public void setCsv(PrintWriter csv) {
		this.csv = csv;
	}
}
