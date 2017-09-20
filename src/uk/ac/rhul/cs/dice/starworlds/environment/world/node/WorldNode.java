package uk.ac.rhul.cs.dice.starworlds.environment.world.node;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialisable;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph.GNode;

public abstract class WorldNode extends GNode implements
		Initialisable<WorldNodeInitialiser> {

	public WorldNode() {
	}

	public abstract AbstractConnectedEnvironment getEnvironment();

	@Override
	public String toString() {
		return this.getId();
	}
}
