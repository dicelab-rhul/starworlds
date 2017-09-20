package uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Graph<N extends GNode, E extends GEdge<N>> {

	protected HashMap<String, N> nodes;
	protected Set<E> edges;

	public Graph() {
		nodes = new HashMap<>();
		edges = new HashSet<>();
	}

	public boolean containsNode(N node) {
		return nodes.containsKey(node.getId());
	}

	public boolean containsNode(String id) {
		return nodes.containsKey(id);
	}

	public N getNode(String id) {
		return nodes.get(id);
	}

	public void addEdge(E edge) {
		if (edge.getNode1() != null && edge.getNode2() != null) {
			if (!containsNode(edge.getNode1())) {
				addNode(edge.getNode1());
			}
			if (!containsNode(edge.getNode2())) {
				addNode(edge.getNode2());
			}
			edges.add(edge);
		} else {
			throw new NullPointerException(this.getClass().getSimpleName()
					+ ": Cannot add an edge with a null node");
		}
	}

	public void addNode(N node) {
		nodes.put(node.getId(), node);
	}

	public void removeEdge(E edge) {
		edges.remove(edge);
	}

	public void removeNode() {
		// TODO
	}

	@Override
	public String toString() {
		return "Graph:" + System.lineSeparator() + "     Nodes:"
				+ Arrays.toString(nodes.values().toArray())
				+ System.lineSeparator() + "     Edges:"
				+ Arrays.toString(edges.toArray());
	}

}
