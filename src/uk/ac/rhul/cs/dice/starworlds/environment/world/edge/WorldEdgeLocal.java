package uk.ac.rhul.cs.dice.starworlds.environment.world.edge;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;

public class WorldEdgeLocal extends WorldEdge {

	private EnvironmentRelation relation;

	public WorldEdgeLocal(WorldNodeLocal n1, EnvironmentRelation relation,
			WorldNodeLocal n2) {
		super(n1, relation, n2);
		this.relation = relation;
	}

	public EnvironmentRelation getRelation() {
		return relation;
	}

	@Override
	public void initialise(WorldEdgeInitialiser initialiser) {
		initialiser.initialise(this);
	}

	@Override
	public String toString() {
		return "[" + n1 + relation.toString() + n2 + "]";
	}
}
