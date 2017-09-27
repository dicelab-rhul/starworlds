package uk.ac.rhul.cs.dice.starworlds.experiment.synchronisation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldAddress;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;

public class ExperimentSynchronisation {

	public static final Collection<Class<? extends AbstractEnvironmentalAction>> SIMPLEPOSSIBLEACTIONS = new HashSet<>();
	static {
		SIMPLEPOSSIBLEACTIONS.add(SensingAction.class);
	}

	private static String RELATIONSUPER = "~";
	private static String RELATIONNEIGHBOUR = "-";
	private static String REGEX = "((?<=(" + RELATIONNEIGHBOUR + "|"
			+ RELATIONSUPER + "))|(?=(" + RELATIONNEIGHBOUR + "|"
			+ RELATIONSUPER + ")))";

	private static String HOST = "localhost";
	private static Integer PORT = 10000;

	private static final WorldAddress world1 = new WorldAddress("w1", HOST,
			PORT);
	private static final WorldAddress world2 = new WorldAddress("w2", HOST,
			(PORT + 1));

	private static final Map<String, WorldAddress> worldmap = new HashMap<>();

	private static final String[] configlocal = new String[] { "a-b" };

	// configuration of world 1
	private static final String[] config1 = new String[] { "c-w2@b", "a-w2@b" };
	// configuration of world 2
	private static final String[] config2 = new String[] { "w1@a-b", "w1@c-b" }; 
	
	static {
		worldmap.put(world1.getName(), world1);
		worldmap.put(world2.getName(), world2);
	}

	public static void main(String[] args) {
		// localWorldExample(configlocal);
		remoteWorldExample(world1, world2, config1, config2);
	}

	private static void remoteWorldExample(WorldAddress addr1,
			WorldAddress addr2, String[] config1, String[] config2) {
		Collection<WorldAddress> worlds1 = new HashSet<>();
		worlds1.add(addr2);
		Collection<WorldAddress> worlds2 = new HashSet<>();
		worlds2.add(addr1);

		/*
		 * Connecting two remote worlds is slightly more complex than a local
		 * world. It is done in a number of steps as follows:
		 */
		/* 1. Create both worlds. */
		World world1 = new World(addr1);
		World world2 = new World(addr2);
		/* 2. Initialise the connection between the worlds */
		try {
			/*
			 * It is possible that both worlds call
			 * initialiseConnectionToOtherWorlds however, in this case we know
			 * that world1 will initiate the connection. So world2 only needs to
			 * wait.
			 */
			world1.initialiseConnectionToOtherWorlds(worlds1);
			world2.waitForOtherWorldsToConnect(worlds2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(world1.getAddress() + " is connected to: "
				+ world1.getOtherWorlds());
		System.out.println(world2.getAddress() + " is connected to: "
				+ world2.getOtherWorlds());

		/*
		 * 3. Create the environments in the worlds (this could be done before
		 * step 2)
		 */
		createWorld(world1, config1);
		createWorld(world2, config2);

		WorldDeployer.deploy(world1);
		WorldDeployer.deploy(world2);

//		world1.initialiseNodes(new WorldNodeInfoInitialiser());
//		world2.initialiseNodes(new WorldNodeInfoInitialiser());
//
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WorldDeployer.run(world1);
		WorldDeployer.run(world2);

	}

	private static void localWorldExample(String[] config) {
		World world = new World();
		world = createWorld(world, config);
		WorldDeployer.deployAndRun(world);
	}

	private static World createWorld(World world, String[] configuration) {
		for (int i = 0; i < configuration.length; i++) {
			String[] input = splitInput(configuration[i]);
			WorldNode env1 = getExperimentalNode(world, input[0]);
			WorldNode env2 = getExperimentalNode(world, input[2]);
			world.addEdge(env1, getRelation(input[1]), env2);
		}
		return world;
	}

	private static EnvironmentRelation getRelation(String in) {
		if (RELATIONNEIGHBOUR.equals(in)) {
			return EnvironmentRelation.NEIGHBOUR;
		}
		return EnvironmentRelation.SUPER;
	}

	private static WorldNode getExperimentalNode(World world, String id) {
		if (id.contains("@")) {
			String[] split = id.split("@");
			return new WorldNodeRemote(split[1], worldmap.get(split[0]));
		} else {
			return new WorldNodeLocal(getExperimentalEnvironment(world, id));
		}
	}

	private static AbstractConnectedEnvironment getExperimentalEnvironment(
			World world, String id) {
		if (world.containsNode(id)) {
			return world.getEnvironment(id);
		} else {
			return new ExperimentalEnvironment(id);
		}
	}

	private static String[] splitInput(String in) {
		String[] result = in.split(REGEX);
		// System.out.println(Arrays.toString(result));
		if (result.length != 3) {
			throw new IllegalArgumentException("Illegal input: " + in);
		}
		return result;
	}

	public Integer getPort() {
		return PORT++;
	}

	public String getHost() {
		return HOST;
	}
}
