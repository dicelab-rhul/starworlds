package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdge;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeRemote;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialisable;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;

/**
 * An {@link WorldEdgeInitialiser} class that
 * {@link Initialiser#initialise(Initialisable) initialises} {@link WorldEdge}s,
 * that is, creates a connection between the two {@link Environment}s given by a
 * {@link WorldEdge}.This {@link WorldEdgeInitialiser} is used in the
 * {@link WorldDeployer} to initialise the {@link World World}.
 * 
 * @author Ben Wilkins
 *
 */
public class ConnectionInitialiser implements WorldEdgeInitialiser {

	public World world;

	public ConnectionInitialiser(World world) {
		this.world = world;
	}

	@Override
	public void initialise(WorldEdgeLocal edge) {
		edge.getNode1()
				.getEnvironment()
				.getConnectedEnvironmentManager()
				.initialiseConnection(edge.getNode2().getEnvironment(),
						edge.getRelation());
		System.out.println("***Initialised Connection: " + edge);
	}

	@Override
	public void initialise(WorldEdgeRemote edge) {
		System.out.println("***Initialised Connection: " + edge);
		world.initialiseRemoteConnection(edge); 
	}

}
