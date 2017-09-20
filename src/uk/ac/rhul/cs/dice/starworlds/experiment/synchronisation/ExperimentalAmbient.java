package uk.ac.rhul.cs.dice.starworlds.experiment.synchronisation;

import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.entities.agent.AbstractAutonomousAgent;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.AbstractAmbient;

public class ExperimentalAmbient extends AbstractAmbient {

	public ExperimentalAmbient(Set<AbstractAutonomousAgent> agents) {
		super(agents, null, null, null);
	}

}
