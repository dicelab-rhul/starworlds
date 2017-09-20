package uk.ac.rhul.cs.dice.starworlds.environment.world;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Universe;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser.ConnectionInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser.PostInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser.ValidateInitialiser;

public class WorldDeployer {

	private final static Set<Thread> WORLDS = new HashSet<>();

	/**
	 * Deploys the given {@link World} and starts the simulation. The
	 * {@link World} is run in its own thread, which is returned or which can be
	 * accessed via the {@link WorldDeployer#getWorlds()} method
	 * 
	 * @param world
	 *            : to deploy and run
	 * @return the {@link Thread} that the {@link World} is running in
	 */
	public static Thread deployAndRun(World world) {
		Thread t = new Thread(new WorldDeployer().initialiseWorld(world));
		WORLDS.add(t);
		t.start();
		return t;
	}

	/**
	 * Runs the given world, starting the simulation. The {@link World} should
	 * have been previously deployed using {@link WorldDeployer#deploy(World)}.
	 * 
	 * @param world
	 *            : to run
	 * @return the {@link Thread} that the {@link World} is running in
	 */
	public static Thread run(World world) {
		Thread t = new Thread(world);
		WORLDS.add(t);
		t.start();
		return t;
	}

	/**
	 * Deploys the given {@link World}. This method will not start the
	 * simulation.
	 * 
	 * @param world
	 *            : to deploy
	 * @return the deployed {@link World}
	 */
	public static World deploy(World world) {
		return new WorldDeployer().initialiseWorld(world);
	}

	/**
	 * Initialise the given {@link World World}. The {@link World World} is
	 * traversed as a depth first search using {@link Visitor}s, initialisation
	 * has 3 phases: The first {@link ValidateInitialiser}, the second
	 * {@link ConnectionInitialiser}, the third {@link PostInitialiser}. See
	 * classes for details. This method may be overridden if additional
	 * {@link World World} initialisation is required. However it is recommended
	 * that this method is always called first.
	 * 
	 * @param world
	 *            : to initialise
	 */
	protected World initialiseWorld(World world) {
		world.initialiseNodes(new ValidateInitialiser());
		ConnectionInitialiser civ;
		world.initialiseEdges((civ = new ConnectionInitialiser()));
		try {
			civ.waitForRemoteConnections();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		world.initialiseNodes(new PostInitialiser());
		return world;
	}

	public static Set<Thread> getWorlds() {
		return WORLDS;
	}
}
