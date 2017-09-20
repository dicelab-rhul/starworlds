package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise;

import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;

public interface WorldNodeInitialiser extends Initialiser<WorldNodeRemote> {

	public void initialise(WorldNodeRemote node);

	public void initialise(WorldNodeLocal node);
}
