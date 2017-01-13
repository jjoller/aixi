# Monte-Carlo AIXI approximation

Implementation of the Monte-Carlo AIXI approximation using CTW as described by Joel Veness et al.

Veness, Joel, et al. "A monte-carlo aixi approximation." Journal of Artificial Intelligence Research 40.1 (2011): 95-142.

Have a look at the RunAgentTest.java file for an example of how to set up and run the intelligent agent.

## Example code
~~~
// set up the biased coin environment. The coin lands on one side with a probability of 0.7.
Environment env = new CoinFlipEnv(0.7);

// set up a world model
WorldModelSettings modelSettings = new WorldModelSettings();
modelSettings.setFacContextTree(true);
modelSettings.setDepth(3);
Worldmodel model = Worldmodel.getInstance("CoinFlipTraining", modelSettings);

AgentController controller = new AgentController(env, new ControllerSettings(), new UCTSettings(), model);


// Play 1000 rounds against the biased coin environment
// A smart agent should learn to always choose the biased side and should come close to an average reward of 0.7
controller.play(1000, false);
~~~
