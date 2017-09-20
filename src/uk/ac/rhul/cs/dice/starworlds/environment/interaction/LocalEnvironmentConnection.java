package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;

public class LocalEnvironmentConnection extends AbstractEnvironmentConnection
		implements EventListener {

	/*
	 * The local connector will be notified of any events that this connection
	 * has received.
	 */
	private EnvironmentConnector connector;

	public LocalEnvironmentConnection(EnvironmentConnector connector,
			EnvironmentAppearance environmentAppearance) {
		super(environmentAppearance);
		this.connector = connector;

	}

	public LocalEnvironmentConnection(EnvironmentConnector connector,
			AbstractEnvironmentConnection remoteConnection,
			EnvironmentAppearance environmentAppearance) {
		super(remoteConnection, environmentAppearance);
		this.connector = connector;
	}

	@Override
	public void receive(Event event) {
		if (isOpen()) {
			connector.update(this, event);
		}
	}

	@Override
	public void send(Event event) {
		if (isOpen()) {
			this.getRemoteConnection().receive(event);
		}
	}

	@Override
	public void update(Object origin, Event event) {
		send(event);
	}
}
