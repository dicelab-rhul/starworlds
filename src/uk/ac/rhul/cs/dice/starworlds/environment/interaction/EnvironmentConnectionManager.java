package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SynchronisationEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetDefaultServer;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.InitialisationMessage;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class EnvironmentConnectionManager implements Receiver, Observer {

	protected EnvironmentConnector connector;
	protected INetServer server = null;
	protected volatile int numRemoteConnections = 0;
	protected volatile int expectedRemoteConnections = 0;

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

	// TODO
	public void setExpectedRemoteConnections(int expectedRemoteConnections) {
		this.expectedRemoteConnections = expectedRemoteConnections;
	}

	public boolean expectingRemoteConnections() {
		System.out.println(connector.getLocalEnvironment().getId()
				+ " EXPECTED CONNCETIONS: " + expectedRemoteConnections);
		return this.expectedRemoteConnections > 0;
	}

	public boolean reachedExpectedRemoteConnections() {
		return expectedRemoteConnections <= numRemoteConnections;
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
				// System.out.println("ADDING NEW CONNECTION!");
				// new
				// INetEnvironmentConnection(connector.getLocalEnvironment(),
				// (INetSlave) arg).addReciever(this);
			}
		}
	}

	// public void connectToEnvironment(String host, Integer port,
	// AmbientRelation relation) {
	// INetEnvironmentConnection connection = new INetEnvironmentConnection(
	// this.localenvironment.getAppearance(), relation, server, host,
	// port);
	// connection.addReciever(this);
	// addRemoteEnvironment(connection);
	// }

	// public void addRemoteEnvironment(INetEnvironmentConnection connection) {
	// AmbientRelation remoteRelation = connection.getRelationship()
	// .getSecond();
	// System.out.println(this.localenvironment + " CONNECTED TO: "
	// + connection);
	// // set the synchroniser
	// connection.setSynchroniser(this.localenvironment.getPhysics()
	// .getSynchroniser().addRemoteSynchroniser(connection));
	// // TODO optimise, handle matching sub/neighbours
	// if (remoteRelation.equals(AmbientRelation.SUB)) {
	// this.subEnvironmentConnections.put(
	// (EnvironmentAppearance) connection.getRemoteAppearance(),
	// connection);
	// } else if (remoteRelation.equals(AmbientRelation.NEIGHBOUR)) {
	// this.neighbouringEnvironmentConnections.put(
	// (EnvironmentAppearance) connection.getRemoteAppearance(),
	// connection);
	// } else if (remoteRelation.equals(AmbientRelation.SUPER)) {
	// if (this.superEnvironmentConnection == null) {
	// this.superEnvironmentConnection = connection;
	// } else {
	// String c = Environment.class.getSimpleName();
	// System.err.println("Inconsistant " + c + " heirarchy, an " + c
	// + " cannot have multiple super " + c + "s: "
	// + this.superEnvironmentConnection + "," + connection);
	// // TODO throw a custom exception?
	// }
	// }
	// }

	@Override
	public synchronized void receive(Recipient recipient, Event event) {
		if (InitialisationMessage.class.isAssignableFrom(event.getClass())) {
			// // TODO redo this
			// if (INetEnvironmentConnection.class.isAssignableFrom(recipient
			// .getClass())) {
			// INetEnvironmentConnection connection =
			// (INetEnvironmentConnection) recipient;
			// // addRemoteEnvironment(connection);
			// connection.send((Event) this.localenvironment
			// .getDefaultActionSubscriptionEvent());
			// this.numRemoteConnections++;
			// System.out.println("NUM REMOTE CONNECTIONS: "
			// + numRemoteConnections);
			//
			// }
		} else {
			connector.notifyListeners(event);
		}
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
