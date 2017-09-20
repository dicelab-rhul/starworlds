package uk.ac.rhul.cs.dice.starworlds.experiment.synchronisation;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;

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

	private static String[] config = new String[] { "a-b", "b-c", "c-a"};

	public static void main(String[] args) {
		start(config);
	}

	public static void start(String[] configuration) {
		World world = new World();
		for (int i = 0; i < configuration.length; i++) {
			String[] input = splitInput(configuration[i]);
			AbstractConnectedEnvironment env1 = getExperimentalEnvironment(
					world, input[0]);
			AbstractConnectedEnvironment env2 = getExperimentalEnvironment(
					world, input[2]);
			world.addEdge(env1, getRelation(input[1]), env2);
		}
		System.out.println(world);
		WorldDeployer.deployAndRun(world);
	}

	private static EnvironmentRelation getRelation(String in) {
		if (RELATIONNEIGHBOUR.equals(in)) {
			return EnvironmentRelation.NEIGHBOUR;
		}
		return EnvironmentRelation.SUPER;
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
