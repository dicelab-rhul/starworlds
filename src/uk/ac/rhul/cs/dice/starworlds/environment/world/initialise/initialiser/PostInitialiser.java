package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialisable;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;

/**
 * An {@link WorldNodeInitialiser} class that
 * {@link Initialiser#initialise(Initialisable) initialises} {@link WorldNode}s
 * calling the {@link AbstractConnectedEnvironment#postInitialisation()} method.
 * See this method for details. This {@link WorldNodeInitialiser} is used in the
 * {@link WorldDeployer} to initialise the {@link World World}.
 * 
 * @author Ben Wilkins
 *
 */
public class PostInitialiser implements WorldNodeInitialiser {

	@Override
	public void initialise(WorldNodeLocal node) {
		System.out.println("*** Post-Initialise: " + node);
		node.getEnvironment().postInitialisation();
	}

	@Override
	public void initialise(WorldNodeRemote node) {
		// do nothing
	}
}
