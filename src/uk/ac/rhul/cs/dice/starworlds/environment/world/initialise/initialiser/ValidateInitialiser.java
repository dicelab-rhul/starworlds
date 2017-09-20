package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialisable;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;
import uk.ac.rhul.cs.dice.starworlds.initialisation.ReflectiveMethodStore;

/**
 * A {@link WorldNodeInitialiser} class that
 * {@link Initialiser#initialise(Initialisable) initialises} {@link Environment}
 * s and attempting to validate them. That is, it ensures that all methods
 * called via reflection have been defined by a developer. See
 * {@link ReflectiveMethodStore} for details. This {@link InitialisationVisitor}
 * is used in the {@link WorldDeployer} to initialise the {@link World World}.
 * 
 * @author Ben Wilkins
 *
 */
public class ValidateInitialiser implements WorldNodeInitialiser {

	public static final Collection<Class<? extends AbstractEnvironmentalAction>> IGNORE = new HashSet<>();
	static {
		IGNORE.add(SensingAction.class);
		IGNORE.add(CommunicationAction.class);
	}

	@Override
	public void initialise(WorldNodeRemote node) {
		// do nothing
	}

	@Override
	public void initialise(WorldNodeLocal node) {
		AbstractConnectedEnvironment env = node.getEnvironment();
		Collection<Class<? extends AbstractEnvironmentalAction>> actions = new HashSet<>(
				env.getPossibleActions());
		actions.removeAll(IGNORE);
		ReflectiveMethodStore.validateReflectiveActions(env.getPhysics()
				.getClass(), actions);
		// TODO validate sensors?
		System.out.println("***Validated: " + node);
	}
}
