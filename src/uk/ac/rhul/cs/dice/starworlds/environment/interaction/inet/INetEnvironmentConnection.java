package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.AbstractEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.EnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment.AmbientRelation;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.time.RemoteSynchroniser;
import uk.ac.rhul.cs.dice.starworlds.utils.Pair;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetServer;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class INetEnvironmentConnection extends AbstractEnvironmentConnection
		implements Observer {

	private INetSlave slave;
	private Appearance remoteAppearance;
	// this connection should notify the synchroniser when it receives sync
	// messages
	private RemoteSynchroniser synchroniser;

	/*
	 * The relationship is as follows: local -> remote. For example, if this is
	 * a super environment of the remote the relationship will be: <SUPER,SUB>
	 */
	private Pair<AmbientRelation, AmbientRelation> relationship;

	/**
	 * Constructor. Sets up a new connection via the server using the given host
	 * and port arguments. Sends an initial connect message containing the given
	 * {@link EnvironmentAppearance} which should be the {@link Appearance} of
	 * the local {@link Environment} as well as the {@link AmbientRelation} of
	 * this {@link Environment} to the other. As an example, if the connection
	 * is being made to a sub {@link Environment} of this {@link Environment}
	 * the {@link AmbientRelation} should be {@link AmbientRelation#SUPER}
	 * indicating that the local {@link Environment} is the super
	 * {@link Environment}. This constructor will block while waiting for a
	 * reply from the remote {@link Environment}.
	 * 
	 * @param environmentAppearance
	 *            :
	 * @param relation
	 *            :
	 * @param server
	 *            :
	 * @param host
	 *            :
	 * @param port
	 *            :
	 */
	public INetEnvironmentConnection(
			EnvironmentAppearance environmentAppearance,
			AmbientRelation relation, INetServer server, String host,
			Integer port) {
		super(environmentAppearance);
		relationship = new Pair<>(relation, null);
		try {
			this.slave = server.getSlave(server.connect(host, port));
			this.slave.addObserver(this);
			Object reply = this.slave.sendAndWaitForReply(
					new InitialisationMessage(this.getAppearance(), relation),
					3000L);
			InitialisationMessage message = validateInitialReply(reply);
			if (message != null) {
				this.remoteAppearance = message.getPayload();
				this.relationship.setSecond(message.getRelation());
			} else {
				System.err.println("Received invalid initialisation message: "
						+ reply + " from: "
						+ slave.getSocket().getRemoteSocketAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InitialisationMessage validateInitialReply(Object obj) {
		if (InitialisationMessage.class.isAssignableFrom(obj.getClass())) {
			InitialisationMessage message = (InitialisationMessage) obj;
			if (checkValidInitialisationMessage(message)) {
				if (this.relationship.getFirst().inverse()
						.equals(message.getRelation())) {
					return message;
				}
			}
		}
		return null;
	}

	/**
	 * Constructor. After a connection has been set up (via the other
	 * constructor at a different network address). This constructor finalises
	 * the connection.
	 * 
	 * @param environmentAppearance
	 *            :
	 * @param slave
	 *            :
	 */
	public INetEnvironmentConnection(
			EnvironmentAppearance environmentAppearance, INetSlave slave) {
		super(environmentAppearance);
		this.slave = slave;
		this.slave.addObserver(this);
	}

	@Override
	public void send(Event<?> message) {
		slave.send(message);
	}

	public Event<?> sendAndWaitForReply(Event<?> message) {
		return (Event<?>) slave.sendAndWaitForReply(message);
	}

	public Event<?> sendAndWaitForReply(Event<?> message, Long timeout) {
		return (Event<?>) slave.sendAndWaitForReply(message, timeout);
	}

	@Override
	public void receive(Event<?> message) {
		// TODO optimise
		if (InitialisationMessage.class.isAssignableFrom(message.getClass())) {
			InitialisationMessage imessage = (InitialisationMessage) message;
			if (checkValidInitialisationMessage(imessage)) {
				this.relationship = new Pair<>(
						imessage.getRelation().inverse(),
						imessage.getRelation());
				this.remoteAppearance = (EnvironmentAppearance) imessage
						.getPayload();
				slave.send(new InitialisationMessage(this.getAppearance(),
						imessage.getRelation().inverse()));
			}
		} else if (SynchronisationMessage.class.isAssignableFrom(message
				.getClass())) {
			SynchronisationMessage smessage = (SynchronisationMessage) message;
			synchroniser.receiveSyncMessage(smessage.getPayload());
			return;
		}
		notifyReceivers(message);
	}

	/**
	 * A check performed during the connection initialisation. Connecting
	 * {@link Environment} should share their {@link Appearance}s, this method
	 * checks that the received {@link Appearance} is valid. This method may be
	 * overridden in a sub class to perform more advanced checks. TODO If the
	 * {@link Appearance} is not valid. The connection will be aborted.
	 * 
	 * @param appearance
	 * @return
	 */
	protected boolean checkValidInitialisationMessage(
			InitialisationMessage message) {
		return EnvironmentAppearance.class.isAssignableFrom(message
				.getPayload().getClass());
	}

	@Override
	public Appearance getRemoteAppearance() {
		return this.remoteAppearance;
	}

	@Override
	public EnvironmentConnection getRemoteConnection() {
		// TODO
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		receive((Event<?>) arg);
	}

	/**
	 * Getter for the relationship between two remotely connected
	 * {@link Environment}s. The order of the relationship pair is as follows:
	 * <local,remote>. For example, if this is a super {@link Environment} of
	 * the remote the relationship will be: <SUPER,SUB>. If the
	 * {@link Environment}s are neighbouring, the relationship will be
	 * <NEIGHBOUR,NEIGHBOUR>.
	 *
	 * 
	 * @return
	 */
	public Pair<AmbientRelation, AmbientRelation> getRelationship() {
		return relationship;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " RELATION: " + relationship
				+ " -> " + remoteAppearance;
	}

	public RemoteSynchroniser getSynchroniser() {
		return synchroniser;
	}

	public void setSynchroniser(RemoteSynchroniser synchroniser) {
		this.synchroniser = synchroniser;
	}

	@Override
	public boolean isConnected() {
		// TODO
		return true;
	}
}
