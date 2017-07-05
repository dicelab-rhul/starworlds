package uk.ac.rhul.cs.dice.starworlds.experiment.communicatingagents;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.entities.agent.AbstractAutonomousAgent;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultAmbient;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultConnectedPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultUniverse;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultWorld;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment.AmbientRelation;
import uk.ac.rhul.cs.dice.starworlds.initialisation.AgentFactory;
import uk.ac.rhul.cs.dice.starworlds.initialisation.IDFactory;
import uk.ac.rhul.cs.dice.starworlds.initialisation.WorldDeployer;

public class CommunicationExperimentClient {

	private static AgentFactory FACTORY = AgentFactory.getInstance();
	// The set of actions possible in the Environments
	private static Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions = new HashSet<>();
	static {
		possibleActions.add(SensingAction.class);
		possibleActions.add(CommunicationAction.class);
	}

	public static void main(String[] args) {
		Integer port = 10001;
		Integer portremote = 5000;
		String addr = "10.2.105.128";
		DefaultWorld world = new DefaultWorld(new DefaultUniverse(port,
				new DefaultAmbient(getAgents(1), null, null, null),
				new DefaultConnectedPhysics(), new EnvironmentAppearance(
						IDFactory.getInstance().getNewID(), false, false),
				possibleActions));
		world.getRoot()
				.getValue()
				.addRemoteConnection(addr, portremote,
						AmbientRelation.NEIGHBOUR);
		WorldDeployer.deployAndRun(world);
	}

	// creates some default communicating agents
	private static Set<AbstractAutonomousAgent> getAgents(int numAgents) {
		Set<AbstractAutonomousAgent> agents = new HashSet<>();
		for (int i = 0; i < numAgents; i++) {
			agents.add(FACTORY.createCustomDefaultAgent(
					FACTORY.getDefaultSensors(), FACTORY.getDefaultActuators(),
					new RandomCommunicatingAgentMind()));
		}
		return agents;
	}

}
