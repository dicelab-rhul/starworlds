package uk.ac.rhul.cs.dice.starworlds.environment.world;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.ConnectionEventStatus;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetConnectionEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetDefaultServer;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdge;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeRemote;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser.RunInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;
import uk.ac.rhul.cs.dice.starworlds.utils.CollectionUtilities;
import uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph.Graph;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class World extends Graph<WorldNode, WorldEdge> implements Runnable,
		Observer {

	private WorldAddress localAddr;
	private INetDefaultServer server;
	private Map<WorldAddress, SocketAddress> otherWorlds;

	private Set<WorldEdgeRemote> remoteEdges;

	private String connectionFailedCause = "";

	public World() {
	}

	public World(WorldAddress localAddr) {
		this.localAddr = localAddr;
		init(localAddr.getPort());
	}

	public World(String name, int port) {
		this.localAddr = new WorldAddress(name, "localhost", port);
		init(port);
	}

	private void init(Integer port) {
		server = new INetDefaultServer(port);
		server.addObserver(this);
		otherWorlds = new HashMap<>();
		remoteEdges = new HashSet<>();
	}

	@Override
	public void update(Observable obs, Object arg) {
		// System.out.println("update: " + arg + " from: " + obs);
		if (INetServer.class.isAssignableFrom(obs.getClass())) {
			INetSlave slave = (INetSlave) arg;
			slave.addObserver(this);
		} else if (INetSlave.class.isAssignableFrom(obs.getClass())) {
			INetSlave slave = ((INetSlave) obs);
			if (WorldAddress.class.isAssignableFrom(arg.getClass())) {
				WorldAddress addr = (WorldAddress) arg;
				updateOtherWorlds(slave, addr);
			} else if (INetConnectionEvent.class.isAssignableFrom(arg
					.getClass())) {
				handleConnectionEvent((INetConnectionEvent) arg);
			}
		}
	}

	private WorldEdgeRemote getRemoteEdge(INetConnectionEvent event) {
		String id1 = event.getRemoteEnv();
		String id2 = event.getLocalEnv().getId();
		for (WorldEdgeRemote r : remoteEdges) {
			if (r.getNode1().getId().equals(id1)
					&& r.getNode2().getId().equals(id2)) {
				return r;
			}
		}
		connectionFailedCause = " Cause: " + this.remoteEdges
				+ System.lineSeparator() + "No matching edge: " + id1 + " "
				+ id2;
		return null;
	}

	protected void handleConnectionEvent(INetConnectionEvent event) {
		if (event.getStatus() == ConnectionEventStatus.INITIAL) {
			//System.out.println("connection event: " + event);
			WorldEdgeRemote edge = getRemoteEdge(event);
			if (edge != null) {
				boolean success = edge
						.getNode1()
						.getEnvironment()
						.getConnectedEnvironmentManager()
						.connect(event.getOrigin().getAddress(),
								event.getPort(), edge.getRelation(),
								event.getLocalEnv());
				if (success) {
					System.out.println(getAddress() + ":" + edge + ":" + event.getPort());
					this.remoteEdges.remove(edge);
					sendConnectionEvent(edge, ConnectionEventStatus.SUCCESS);
					return;
				}
			}
			System.err
					.println("Failed to connect establish environment connection: "
							+ event.getOrigin().getAddress()
							+ ":"
							+ event.getPort()
							+ System.lineSeparator()
							+ connectionFailedCause);
			sendConnectionEvent(edge, ConnectionEventStatus.FAILED);
		} else if (event.getStatus() == ConnectionEventStatus.SUCCESS) {
			// the connection has been established
			WorldEdgeRemote edge = getRemoteEdge(event);
			this.remoteEdges.remove(edge);
		} else if (event.getStatus() == ConnectionEventStatus.FAILED) {
			System.err.println(this.getAddress()
					+ " received failed to connect... " + event);
		}
	}

	public void initialiseRemoteConnection(WorldEdgeRemote edge) {
		// check if the connection has already been established
		if (remoteEdges.contains(edge)) {
			System.out
					.println("Sending connection event to connect local node: "
							+ edge.getNode1().getId() + " to remote node: "
							+ edge.getNode2().getId());
			sendConnectionEvent(edge, ConnectionEventStatus.INITIAL);
		}
		// wait for the connection to be established
		while (remoteEdges.contains(edge))
			;
	}

	private void sendConnectionEvent(WorldEdgeRemote edge,
			ConnectionEventStatus status) {
		server.send(this.otherWorlds.get(edge.getNode2().getWorld()),
				getConnectionEvent(edge, status));
	}

	private INetConnectionEvent getConnectionEvent(WorldEdge edge,
			ConnectionEventStatus status) {
		return new INetConnectionEvent(this.getAddress(), status, edge
				.getNode1().getEnvironment().getAppearance(), edge.getNode2()
				.getId(), edge.getNode1().getEnvironment()
				.getConnectedEnvironmentManager().getLocalPort());
	}

	private void updateOtherWorlds(INetSlave slave, WorldAddress addr) {
		otherWorlds.put(addr, slave.getRemoteSocketAddress());
		System.out.println(this.localAddr + "done");
	}

	public void initialiseConnectionToOtherWorlds(Collection<WorldAddress> addrs)
			throws UnknownHostException, IOException {
		for (WorldAddress addr : addrs) {
			if (!otherWorlds.containsKey(addr.getName())) {
				SocketAddress saddr = server.connect(addr.getAddress(),
						addr.getPort());
				INetSlave slave = server.getSlave(saddr);
				slave.addObserver(this);
				updateOtherWorlds(slave, addr);
				slave.send(this.localAddr);
			}
		}
		waitForOtherWorldsToConnect(addrs);
	}

	public void waitForOtherWorldsToConnect(Collection<WorldAddress> addrs) {
		while (!CollectionUtilities.containsAllKeys(addrs, this.otherWorlds))
			;
	}

	public void addEnvironment(String aliasid, WorldAddress remoteWorld) {
		super.addNode(new WorldNodeRemote(aliasid, remoteWorld));
	}

	public void addEnvironment(AbstractConnectedEnvironment environment) {
		super.addNode(new WorldNodeLocal(environment));
	}

	private void addRemoteEdge(WorldNode node1, EnvironmentRelation relation,
			WorldNode node2) {
		WorldEdgeRemote edge = new WorldEdgeRemote((WorldNodeLocal) node1,
				relation, (WorldNodeRemote) node2);
		this.remoteEdges.add(edge);
		super.addEdge(edge);
	}

	public void addEdge(WorldNode node1, EnvironmentRelation relation,
			WorldNode node2) {
		if (WorldNodeRemote.class.isAssignableFrom(node1.getClass())) {
			addRemoteEdge(node2, relation, node1);
		} else if (WorldNodeRemote.class.isAssignableFrom(node2.getClass())) {
			addRemoteEdge(node1, relation, node2);
		} else {
			super.addEdge(new WorldEdgeLocal((WorldNodeLocal) node1, relation,
					(WorldNodeLocal) node2));
		}
	}

	public void addEdge(AbstractConnectedEnvironment environment,
			EnvironmentRelation relation, String aliasid,
			WorldAddress remoteWorld) {
		WorldNodeRemote remoteNode = new WorldNodeRemote(aliasid, remoteWorld);
		addRemoteEdge(new WorldNodeLocal(environment), relation, remoteNode);
	}

	public void addEdge(WorldNodeLocal node1, EnvironmentRelation relation,
			WorldNodeRemote node2) {
		addRemoteEdge(node1, relation, node2);
	}

	public void addEdge(WorldNodeLocal node1, EnvironmentRelation relation,
			WorldNodeLocal node2) {
		super.addEdge(new WorldEdgeLocal(node1, relation, node2));
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

	@Override
	public void run() {
		RunInitialiser runner = new RunInitialiser();
		this.initialiseNodes(runner);
		Collection<Thread> threads = runner.getThreads();
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public INetServer getServer() {
		return server;
	}

	public Map<WorldAddress, SocketAddress> getOtherWorlds() {
		return otherWorlds;
	}

	public WorldAddress getAddress() {
		return localAddr;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + localAddr
				+ super.toString();
	}
}