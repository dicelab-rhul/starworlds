package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;

public class WorldNodeInfoInitialiser implements WorldNodeInitialiser {

	@Override
	public void initialise(WorldNodeRemote node) {
		System.out.println("remote: " + node.getId());
	}

	@Override
	public void initialise(WorldNodeLocal node) {
		System.out.println(node.getEnvironment());
	}
}
