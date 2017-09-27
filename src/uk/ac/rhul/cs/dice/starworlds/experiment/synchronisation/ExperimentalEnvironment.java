package uk.ac.rhul.cs.dice.starworlds.experiment.synchronisation;

import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.AbstractAmbient;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;

public class ExperimentalEnvironment extends AbstractConnectedEnvironment {

	public ExperimentalEnvironment(String id) {
		super(0, new ExperimentalAmbient(null), new ExperimentalPhysics(),
				new EnvironmentAppearance(id, false, false),
				ExperimentSynchronisation.SIMPLEPOSSIBLEACTIONS, true);
	}

	public ExperimentalEnvironment(
			AbstractAmbient ambient,
			AbstractConnectedPhysics physics,
			EnvironmentAppearance appearance,
			Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions) {
		super(ambient, physics, appearance, possibleActions, true);
	}
}
