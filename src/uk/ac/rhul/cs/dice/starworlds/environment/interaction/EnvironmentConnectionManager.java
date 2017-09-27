package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SynchronisationEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetConnectionPacket;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetDefaultServer;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class EnvironmentConnectionManager implements Observer {

	protected EnvironmentConnector connector;
	protected INetServer server = null;

	public class ExpectedConnection {
		public EnvironmentAppearance appearance;
		public EnvironmentRelation relation;

		public ExpectedConnection(EnvironmentAppearance appearance,
				EnvironmentRelation relation) {
			super();
			this.appearance = appearance;
			this.relation = relation;
		}
	}

	/**
	 * Constructor specifically for mixed connections.
	 * 
	 * @param localenvironment
	 *            : the {@link Environment} that this
	 * @param localsubenvironments
	 *            : the local {@link Environment}s sub {@link Environment}s
	 * @param localneighbouringenvironments
	 *            : the local {@link Environment}s neighbouring
	 *            {@link Environment}s
	 * @param port
	 *            : the port that any remote {@link Environment} will try to
	 *            connect to
	 */
	public EnvironmentConnectionManager(
			AbstractConnectedEnvironment localenvironment, Integer port) {
		connector = new EnvironmentConnector(localenvironment.getAppearance());
		if (port != null) {
			this.server = new INetDefaultServer(port);
			this.server.addObserver(this);
		}
	}

	public void addLocalEventListener(Class<? extends Event> event,
			EventListener listener) {
		connector.addLocalEventListener(event, listener);
	}

	public void addLocalEventListener(Class<? extends Event> event,
			Collection<EventListener> listeners) {
		connector.addLocalEventListener(event, listeners);
	}

	public void synchronise(SynchronisationEvent event) {
		connector.sychronise(event);
	}

	/**
	 * Subscribe to the {@link Event}s given by the <code>events</code> argument
	 * from the given {@link Environment}.
	 * 
	 * @param environment
	 *            : to subscribe to
	 * @param events
	 *            : to subscribe to
	 * 
	 */
	public void subscribeTo(EnvironmentAppearance environment,
			Collection<Class<? extends Event>> events) {
		connector.subscribeTo(environment, events);
	}

	/**
	 * This method should only be called by the {@link INetServer} which this
	 * {@link EnvironmentConnectionManager} is observing. This method will be
	 * called when a new connection has been made and will supply the new
	 * {@link INetSlave} that has been created by the {@link INetServer}.
	 */
	@Override
	public void update(Observable obs, Object arg) {
		if (INetServer.class.isAssignableFrom(obs.getClass())) {
			if (INetSlave.class.isAssignableFrom(arg.getClass())) {
				INetSlave slave = (INetSlave) arg;
				slave.addObserver(this);
			} else {
				invalidUpdateError(obs, arg);
			}
		} else if (INetSlave.class.isAssignableFrom(obs.getClass())) {
			if (INetConnectionPacket.class.isAssignableFrom(arg.getClass())) {
				INetSlave slave = (INetSlave) obs;
				INetConnectionPacket packet = (INetConnectionPacket) arg;
				INetEnvironmentConnection connection = new INetEnvironmentConnection(
						slave, connector, this.getLocalEnvironment(),
						packet.appearance);
				slave.deleteObserver(this);
				packet.relation.addConnection(connector, connection);
				slave.send(this.getLocalEnvironment());
			} else {
				invalidUpdateError(obs, arg);
			}
		} else {
			invalidUpdateError(obs, arg);
		}
	}

	private void invalidUpdateError(Observable obs, Object arg) {
		System.err
				.println("Received invalid update : " + arg + " from: " + obs);
	}

	// **************************************************************** //
	// ******** INITIALISATION METHODS FOR REMOTE ENVIRONMENTS ******** //
	// **************************************************************** //

	/**
	 * Generally this method will be called from the {@link World}, the world
	 * sets up static connections between {@link Environment}s. One can take
	 * advantage of this to implement dynamic connections. See {@link World} and
	 * the methods it exposes for more information.
	 * 
	 * @param addr
	 *            : of the remote {@link Environment}, this is the same as the
	 *            remote {@link World} address (ip address)
	 * @param port
	 *            : of the remote {@link Environment} (that is, the port that
	 *            the remote {@link EnvironmentConnectionManager} is listening
	 *            to
	 * @param relation
	 *            : relationship between this {@link Environment} and the remote
	 *            one
	 * @param remoteAppearance
	 *            : of the {@link Environment} that will be connected to
	 * @return
	 */
	public boolean connect(String addr, Integer port,
			EnvironmentRelation relation, EnvironmentAppearance remoteAppearance) {
		try {
			SocketAddress saddr = server.connect(addr, port);
			if (saddr != null) {
				INetSlave slave = server.getSlave(saddr);
				Object reply = slave
						.sendAndWaitForReply(new INetConnectionPacket(this
								.getLocalEnvironment(), relation.inverse()));
				if (EnvironmentAppearance.class.isAssignableFrom(reply
						.getClass())) {
					INetEnvironmentConnection connection = new INetEnvironmentConnection(
							slave, connector, this.getLocalEnvironment(),
							(EnvironmentAppearance) reply);
					relation.addConnection(connector, connection);
					return true;
				} else {
					System.err.println("invalid reply on connect: " + reply);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// **************************************************************** //
	// * INITIALISATION METHODS FOR LOCAL (SAME MACHINE) ENVIRONMENTS * //
	// **************************************************************** //

	/*
	 * Environments are connected via EnvironmentConnections, each has a
	 * reference to the other EnvironmentConnection - its remoteConnection.
	 */

	/**
	 * Method to call when initialising a connection between two
	 * {@link Environment}. Sets up an {@link EnvironmentConnection} for both
	 * the local and remote {@link Environment}.
	 * 
	 * @param toConnect
	 *            : the {@link Environment} to connect to
	 * @param relation
	 *            : the type of connection to make, see
	 *            {@link EnvironmentRelation}.
	 */
	public void initialiseConnection(AbstractConnectedEnvironment toConnect,
			EnvironmentRelation relation) {
		// create a local connection
		LocalEnvironmentConnection localConnection = createEnvironmentConnection();
		// initialise the connection (creating a connection on the remote side)
		toConnect.getConnectedEnvironmentManager().initialiseConnection(
				localConnection, relation.inverse());
		if (localConnection.isConnected()) {
			relation.addConnection(connector, localConnection);
		} else {
			System.out.println(connector.getLocalEnvironment()
					+ " could not connect to: " + toConnect
					+ System.lineSeparator() + "Cause: Connection Refused");
		}
	}

	/**
	 * Method called by a remote {@link Environment} to try to initialise a
	 * connection.
	 * 
	 * @param connectionTypeInitialiser
	 *            : the type of connection to make, see
	 *            {@link ConnectionTypeInitialiser}.
	 * @param remoteEnvironment
	 *            : the {@link Environment} to connect to
	 */
	protected void initialiseConnection(
			LocalEnvironmentConnection remoteConnection,
			EnvironmentRelation relation) {
		LocalEnvironmentConnection localConnection = createEnvironmentConnection(remoteConnection);
		relation.addConnection(connector, localConnection);
	}

	/**
	 * Creates an {@link EnvironmentConnection} (that is not connected).
	 * 
	 * @return the {@link EnvironmentConnection}
	 */
	protected LocalEnvironmentConnection createEnvironmentConnection() {
		return new LocalEnvironmentConnection(connector,
				connector.getLocalEnvironment());
	}

	/**
	 * Creates an {@link EnvironmentConnection} (that is connected to
	 * remoteConnection).
	 * 
	 * @param remoteConnection
	 *            : to connect to
	 * @return the {@link EnvironmentConnection}
	 */
	protected LocalEnvironmentConnection createEnvironmentConnection(
			LocalEnvironmentConnection remoteConnection) {
		return new LocalEnvironmentConnection(connector, remoteConnection,
				connector.getLocalEnvironment());
	}

	public EnvironmentAppearance getLocalEnvironment() {
		return connector.getLocalEnvironment();
	}

	public Collection<EnvironmentAppearance> getConnectedEnvironments() {
		return connector.getConnectedEnvironments();
	}

	public Collection<EnvironmentAppearance> getNeighbouringEnvironments() {
		return connector.getNeighbouringEnvironments();
	}

	public Collection<EnvironmentAppearance> getSubEnvironments() {
		return connector.getSubEnvironments();
	}

	public EnvironmentAppearance getSuperEnvironment() {
		return connector.getSuperEnvironment();
	}

	public boolean hasNeighbouringEnvironments() {
		return connector.hasNeighbouringEnvironments();
	}

	public boolean hasSubEnvironments() {
		return connector.hasSubEnvironments();
	}

	public boolean hasSuperEnvironment() {
		return connector.hasSuperEnvironment();
	}

	// utility function TODO move to utils
	protected <K, V> Collection<V> getAll(Map<K, V> map, Collection<K> keys) {
		Map<K, V> newMap = new HashMap<K, V>(map);
		newMap.keySet().retainAll(keys);
		return newMap.values();
	}

	public boolean isDistributed() {
		return server != null;
	}

	public Integer getLocalPort() {
		return server.getLocalPort();
	}

	// @Override
	// public String toString() {
	// StringBuilder builder = new StringBuilder(this.getClass()
	// .getSimpleName() + System.lineSeparator());
	// builder.append("  LOCAL: " + this.localenvironment
	// + System.lineSeparator());
	// builder.append("  SUPER: " + this.superEnvironmentConnection
	// + System.lineSeparator() + "  SUB: " + System.lineSeparator());
	// this.subEnvironmentConnections.values().forEach(
	// (con) -> builder.append("    " + con + System.lineSeparator()));
	// builder.append("  NEIGHBOUR: " + System.lineSeparator());
	// this.neighbouringEnvironmentConnections.values().forEach(
	// (con) -> builder.append("    " + con + System.lineSeparator()));
	// return builder.toString();
	// }
}
