package uk.ac.rhul.cs.dice.starworlds.environment.world.edge;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;

public class WorldEdgeRemote extends WorldEdge {

	public WorldEdgeRemote(WorldNodeLocal n1, EnvironmentRelation relation,
			WorldNodeRemote n2) {
		super(n1, relation, n2);
	}

	@Override
	public void initialise(WorldEdgeInitialiser initialiser) {
		initialiser.initialise(this);
	}

	@Override
	public WorldNodeLocal getNode1() {
		return (WorldNodeLocal) super.getNode1();
	}

	@Override
	public WorldNodeRemote getNode2() {
		return (WorldNodeRemote) super.getNode2();
	}
}
