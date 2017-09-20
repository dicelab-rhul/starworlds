package uk.ac.rhul.cs.dice.starworlds.environment.world.edge;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.Initialisable;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph.GEdge;

public abstract class WorldEdge extends GEdge<WorldNode> implements
		Initialisable<WorldEdgeInitialiser> {

	private EnvironmentRelation relation;

	public WorldEdge(WorldNode n1, EnvironmentRelation relation, WorldNode n2) {
		super(n1, n2);
		this.relation = relation;
	}

	public EnvironmentRelation getRelation() {
		return relation;
	}

	@Override
	public String toString() {
		return "[" + n1 + relation.toString() + n2 + "]";
	}
}
