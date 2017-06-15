package uk.ac.rhul.cs.dice.starworlds.environment.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.base.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.base.interfaces.Message;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.INetEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.inet.INetDefaultServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class EnvironmentConnectionManager implements Receiver, Observer {

	protected AbstractConnectedEnvironment localenvironment;
	protected Map<EnvironmentAppearance, AbstractEnvironmentConnection> subEnvironmentConnections;
	protected Map<EnvironmentAppearance, AbstractEnvironmentConnection> neighbouringEnvironmentsConnections;
	protected AbstractEnvironmentConnection superEnvironmentConnection;

	protected INetServer server;
	protected Map<EnvironmentAppearance, Collection<AbstractMessage<?>>> recievedMessages;

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
			AbstractConnectedEnvironment localenvironment,
			AbstractConnectedEnvironment localsuperenvironment,
			Collection<AbstractConnectedEnvironment> localsubenvironments,
			Collection<AbstractConnectedEnvironment> localneighbouringenvironments,
			Integer port) {
		this.recievedMessages = new HashMap<>();
		this.localenvironment = localenvironment;
		initialiseLocalEnvironments(localsubenvironments,
				localneighbouringenvironments);
		this.server = new INetDefaultServer(port);
		this.server.addObserver(this);
	}

	/**
	 * Constructor specifically for local connections.
	 * 
	 * @param localenvironment
	 *            : the {@link Environment} that this
	 *            {@link EnvironmentConnectionManager} is managing
	 * @param localsubenvironments
	 *            : the local {@link Environment}s sub {@link Environment}s
	 * @param localneighbouringenvironments
	 *            : the local {@link Environment}s neighbouring
	 *            {@link Environment}s
	 */
	public EnvironmentConnectionManager(
			AbstractConnectedEnvironment localenvironment,
			Collection<AbstractConnectedEnvironment> localsubenvironments,
			Collection<AbstractConnectedEnvironment> localneighbouringenvironments) {
		this.recievedMessages = new HashMap<>();
		this.localenvironment = localenvironment;
		subEnvironmentConnections = new HashMap<>();
		neighbouringEnvironmentsConnections = new HashMap<>();
		initialiseLocalEnvironments(localsubenvironments,
				localneighbouringenvironments);
	}

	/**
	 * Constructor specifically for remote connections.
	 * 
	 * @param localenvironment
	 *            : the {@link Environment} that this
	 * @param port
	 *            : the port that any remote {@link Environment} will try to
	 *            connect to
	 */
	public EnvironmentConnectionManager(
			AbstractConnectedEnvironment localenvironment, Integer port) {
		this.recievedMessages = new HashMap<>();
		subEnvironmentConnections = new HashMap<>();
		neighbouringEnvironmentsConnections = new HashMap<>();
		this.localenvironment = localenvironment;
		this.server = new INetDefaultServer(port);
		this.server.addObserver(this);
	}

	/**
	 * unused?
	 */
	public Map<EnvironmentAppearance, Collection<AbstractMessage<?>>> flushMessages() {
		Map<EnvironmentAppearance, Collection<AbstractMessage<?>>> result = new HashMap<>();
		// TODO if a message is received while this is happening? will it fail?
		// i dont know!
		recievedMessages.forEach((EnvironmentAppearance app,
				Collection<AbstractMessage<?>> messages) -> {
			if (!messages.isEmpty()) {
				result.put(app, messages);
				recievedMessages.put(app, new ArrayList<>());
			}
		});
		return result;
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
				System.out.println("ADDING NEW CONNECTION!");
				INetEnvironmentConnection connection = new INetEnvironmentConnection(
						this.localenvironment.getAppearance(), (INetSlave) arg);
			}
		}
	}

	public void connectToEnvironment(String host, Integer port) {
		INetEnvironmentConnection connection = new INetEnvironmentConnection(
				this.localenvironment.getAppearance(), server, host, port);
	}

	@Override
	public synchronized void receive(Recipient recipient, Message<?> message) {
		// System.out.println("Received: " + System.lineSeparator() + "    "
		// + recipient + System.lineSeparator() + "        " + message);
		this.localenvironment
				.handleMessage(
						(EnvironmentAppearance) ((AbstractEnvironmentConnection) recipient)
								.getRemoteAppearance(), message);
		// perhaps this? depends on what messages are received
		// this.recievedMessages.get(
		// ((AbstractEnvironmentConnection) recipient)
		// .getRemoteAppearance()).add(message);
	}

	public void addSubEnviroment(AbstractEnvironment environment) {
		System.out.println("ADDING SUB ENVIRONMENT: "
				+ environment.getAppearance());
		DefaultEnvironmentConnection connection = new DefaultEnvironmentConnection(
				localenvironment.getAppearance());
		((AbstractConnectedEnvironment) environment)
				.getConnectedEnvironmentManager()
				.setSuperEnvironmentConnection(connection);
		if (connection.getMutualConnector() != null) {
			this.addSubEnvironmentConnection(connection);
			recievedMessages.put(
					(EnvironmentAppearance) connection.getRemoteAppearance(),
					new ArrayList<>());
			connection.addReciever(EnvironmentConnectionManager.this);
		} else {
			System.out.println(environment + " refused connection");
		}
	}

	private void initialiseLocalEnvironments(
			Collection<AbstractConnectedEnvironment> localsubenvironments,
			Collection<AbstractConnectedEnvironment> localneighbouringenvironments) {
		if (localsubenvironments != null) {
			localsubenvironments.forEach((AbstractConnectedEnvironment e) -> {
				addSubEnviroment(e);
			});
		}
		// TODO
		// if (localneighbouringenvironments != null) {
		// localneighbouringenvironments
		// .forEach((AbstractEnvironment e) -> this.state
		// .addNeighbouringEnvironment(e));
		// }
	}

	public void addNeighbouringEnvironmentConnection(
			AbstractEnvironmentConnection mutualConnector) {
		// this.subEnvironments.put(environment.getAppearance(),
		// new DefaultEnvironmentConnection(
		// (AbstractConnectedEnvironment) environment);
	}

	public void addSubEnvironmentConnection(
			AbstractEnvironmentConnection connection) {
		this.subEnvironmentConnections.put(
				(EnvironmentAppearance) connection.getRemoteAppearance(),
				connection);

	}

	/**
	 * A super environment should call this method with its own
	 * {@link AbstractEnvironmentConnection}. The sub environment will then (if
	 * it accepts the super {@link Environment}) return its own
	 * {@link AbstractEnvironmentConnection}.
	 * 
	 * @param environment
	 */
	public void setSuperEnvironmentConnection(
			AbstractEnvironmentConnection connection) {
		this.superEnvironmentConnection = new DefaultEnvironmentConnection(
				(DefaultEnvironmentConnection) connection,
				localenvironment.getAppearance());
		recievedMessages.put(connection.getAppearance(), new ArrayList<>());
		this.superEnvironmentConnection
				.addReciever(EnvironmentConnectionManager.this);
	}

	public Collection<EnvironmentAppearance> getNeighbouringEnvironmentAppearances() {
		return this.neighbouringEnvironmentsConnections.keySet();
	}

	public Collection<AbstractEnvironmentConnection> getNeighbouringEnvironmentConnections() {
		return this.neighbouringEnvironmentsConnections.values();
	}

	public Collection<EnvironmentAppearance> getSubEnvironmentAppearances() {
		return this.subEnvironmentConnections.keySet();
	}

	public Collection<AbstractEnvironmentConnection> getSubEnvironmentConnections() {
		return this.subEnvironmentConnections.values();
	}

	public EnvironmentAppearance getSuperEnvironmentAppearance() {
		return this.superEnvironmentConnection.getAppearance();
	}

	public AbstractEnvironmentConnection getSuperEnvironmentConnection() {
		return this.superEnvironmentConnection;
	}

	public void sendToAllNeighbouringEnvironments(AbstractMessage<?> obj) {
		neighbouringEnvironmentsConnections.values().forEach(
				(AbstractEnvironmentConnection c) -> {
					c.send(obj);
				});
	}

	public void sendToAllSubEnvironments(AbstractMessage<?> obj) {
		subEnvironmentConnections.values().forEach(
				(AbstractEnvironmentConnection c) -> {
					c.send(obj);
				});
	}

	public void sendToEnvironment(EnvironmentAppearance environment,
			AbstractMessage<?> obj) {
		AbstractEnvironmentConnection con;
		if ((con = subEnvironmentConnections.get(environment)) != null) {
			con.send(obj);
		} else if ((con = neighbouringEnvironmentsConnections.get(environment)) != null) {
			con.send(obj);
		} else if (environment.equals(superEnvironmentConnection
				.getRemoteAppearance())) {
			superEnvironmentConnection.send(obj);
		} else {
			System.err.println("Cannot send: " + obj
					+ " to unknown environment: " + environment);
		}
	}

	public void sendToEnvironments(
			Collection<EnvironmentAppearance> environments,
			AbstractMessage<?> obj) {
		if (environments != null) {
			// System.out.println("SEND: " + obj);
			if (subEnvironmentConnections != null) {
				Collection<AbstractEnvironmentConnection> envs = getAll(
						this.subEnvironmentConnections, environments);
				// System.out.println("THIS: " + localenvironment.getId());
				// System.out.println("CHECK: " +
				// this.subEnvironmentConnections);
				// System.out.println("MENT FOR: " + environments);
				// System.out.println("TO: " + envs);
				envs.forEach((AbstractEnvironmentConnection c) -> c.send(obj));
			}
			if (superEnvironmentConnection != null) {
				// System.out.println("MENT FOR: " + environments);
				// System.out.println("CHECK: " +
				// this.superEnvironmentConnection);
				if (environments.contains(superEnvironmentConnection
						.getRemoteAppearance())) {
					superEnvironmentConnection.send(obj);
				}
			}
		}
	}

	public void sendToNeighbouringEnvironment(EnvironmentAppearance appearance,
			AbstractMessage<?> obj) {
		neighbouringEnvironmentsConnections.get(appearance).send(obj);
	}

	public void sendToSubEnvironment(EnvironmentAppearance appearance,
			AbstractMessage<?> obj) {
		subEnvironmentConnections.get(appearance).send(obj);
	}

	public void sendToSuperEnvironment(AbstractMessage<?> obj) {
		superEnvironmentConnection.send(obj);
	}

	// utility function TODO move to utils
	protected <K, V> Collection<V> getAll(Map<K, V> map, Collection<K> keys) {
		Map<K, V> newMap = new HashMap<K, V>(map);
		newMap.keySet().retainAll(keys);
		return newMap.values();
	}
}
