package uk.ac.rhul.cs.dice.starworlds.environment.world.node;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;

public class WorldNodeLocal extends WorldNode {

	private AbstractConnectedEnvironment environment;

	public WorldNodeLocal(AbstractConnectedEnvironment environment) {
		if (environment == null) {
			throw new NullPointerException(this.getClass().getSimpleName()
					+ " environment cannot be null");
		}
		this.environment = environment;
		this.setId(environment.getId());
	}

	public AbstractConnectedEnvironment getEnvironment() {
		return environment;
	}
	
	@Override
	public void initialise(WorldNodeInitialiser initialiser) {
		initialiser.initialise(this);
	}
}
