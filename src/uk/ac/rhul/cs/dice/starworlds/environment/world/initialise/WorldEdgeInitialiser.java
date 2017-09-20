package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise;

import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeRemote;

public interface WorldEdgeInitialiser extends Initialiser<WorldEdgeRemote> {

	public void initialise(WorldEdgeRemote edge);

	public void initialise(WorldEdgeLocal edge);

}
