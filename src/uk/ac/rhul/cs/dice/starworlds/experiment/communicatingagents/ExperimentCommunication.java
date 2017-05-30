package uk.ac.rhul.cs.dice.starworlds.experiment.communicatingagents;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.AbstractAgent;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultState;
import uk.ac.rhul.cs.dice.starworlds.experiment.ExperimentPhysics;
import uk.ac.rhul.cs.dice.starworlds.initialisation.AgentFactory;

class ExperimentCommunication {

	private final static int NUMAGENTS = 2;

	public static void main(String[] args) {
		Set<Class<? extends AbstractEnvironmentalAction>> possibleActions = new HashSet<>();
		possibleActions.add(CommunicationAction.class);
		possibleActions.add(SensingAction.class);

		ExperimentPhysics physics = new ExperimentPhysics(possibleActions,
				getDefaultAgents(NUMAGENTS), null, null);
		new DefaultEnvironment(new DefaultState(physics), physics, null);
		physics.start(false);
	}

	public static Set<AbstractAgent> getDefaultAgents(int num) {
		Set<AbstractAgent> agents = new HashSet<>();
		for (int i = 0; i < num; i++) {
			AbstractAgent a = AgentFactory.getInstance().createDefaultAgent(
					null, new RandomCommunicatingAgentMind());
			agents.add(a);
			a.setId(String.valueOf(i));
		}
		return agents;
	}
}
