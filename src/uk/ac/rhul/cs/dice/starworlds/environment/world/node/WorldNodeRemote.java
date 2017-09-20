package uk.ac.rhul.cs.dice.starworlds.environment.world.node;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;

public class WorldNodeRemote extends WorldNode {

	public WorldNodeRemote(String aliasid) {
		this.setId(aliasid);
	}

	@Override
	public AbstractConnectedEnvironment getEnvironment() {
		return null;
	}

	@Override
	public void initialise(WorldNodeInitialiser initialiser) {
		initialiser.initialise(this);
	}

}
