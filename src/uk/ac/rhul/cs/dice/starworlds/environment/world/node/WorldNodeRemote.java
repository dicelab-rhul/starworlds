package uk.ac.rhul.cs.dice.starworlds.environment.world.node;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldAddress;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;

public class WorldNodeRemote extends WorldNode {

	private WorldAddress world;

	public WorldNodeRemote(String aliasid, WorldAddress world) {
		this.setId(aliasid);
		this.world = world;
	}

	@Override
	public AbstractConnectedEnvironment getEnvironment() {
		return null;
	}

	@Override
	public void initialise(WorldNodeInitialiser initialiser) {
		initialiser.initialise(this);
	}

	public WorldAddress getWorld() {
		return world;
	}
}
