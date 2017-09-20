package uk.ac.rhul.cs.dice.starworlds.environment.world;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetDefaultServer;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdge;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeRemote;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;
import uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph.Graph;

public class World extends Graph<WorldNode, WorldEdge> implements Runnable {

	private INetDefaultServer server;
	private Map<String, SocketAddress> otherWorlds;

	public World(int port, Collection<WorldAddress> addrs)
			throws UnknownHostException, IOException {
		server = new INetDefaultServer(port);
		otherWorlds = new HashMap<>();
		for (WorldAddress addr : addrs) {
			SocketAddress saddr = server.connect(addr.getAddress(),
					addr.getPort());
			otherWorlds.put(addr.getName(), saddr);
		}
	}

	public World() {
	}

	@Override
	public void run() {
		Collection<WorldNode> nodes = this.nodes.values();
		Collection<Thread> threads = new ArrayList<>();
		for (WorldNode n : nodes) {
			Thread t = new Thread(n.getEnvironment().getPhysics()
					.getSynchroniser());
			t.start();
		}
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEnvironment(String aliasid) {
		super.addNode(new WorldNodeRemote(aliasid));
	}

	public void addEdge(AbstractConnectedEnvironment environment,
			EnvironmentRelation relation, String aliasid) {
		WorldNodeRemote remoteNode = new WorldNodeRemote(aliasid);
		super.addEdge(new WorldEdgeRemote(new WorldNodeLocal(environment),
				relation, remoteNode));
	}

	public void addEnvironment(AbstractConnectedEnvironment environment) {
		super.addNode(new WorldNodeLocal(environment));
	}

	public void addEdge(AbstractConnectedEnvironment environment1,
			EnvironmentRelation relation,
			AbstractConnectedEnvironment environment2) {
		super.addEdge(new WorldEdgeLocal(new WorldNodeLocal(environment1),
				relation, new WorldNodeLocal(environment2)));
	}

	public AbstractConnectedEnvironment getEnvironment(String id) {
		return super.getNode(id).getEnvironment();
	}

	public void initialiseNodes(WorldNodeInitialiser initialiser) {
		for (WorldNode n : nodes.values()) {
			n.initialise(initialiser);
		}
	}

	public void initialiseEdges(WorldEdgeInitialiser initialiser) {
		for (WorldEdge e : edges) {
			e.initialise(initialiser);
		}
	}

}