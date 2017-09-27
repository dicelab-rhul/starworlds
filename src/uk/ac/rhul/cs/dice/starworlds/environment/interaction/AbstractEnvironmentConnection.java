package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;

public abstract class AbstractEnvironmentConnection implements
		EnvironmentConnection, EventListener {

	protected Boolean open = true;
	// The appearance of the local environment
	protected EnvironmentAppearance environmentAppearance;

	/*
	 * The local connector will be notified of any events that this connection
	 * has received.
	 */
	protected EnvironmentConnector connector;

	public AbstractEnvironmentConnection(EnvironmentConnector connector,
			EnvironmentAppearance environmentAppearance) {
		this.environmentAppearance = environmentAppearance;
		this.connector = connector;
	}

	@Override
	public void receive(Event event) {
		if (isOpen()) {
			connector.update(this, event);
		}
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() {
		open = false;
	}

	@Override
	public void open() {
		open = true;
	}

	@Override
	public EnvironmentAppearance getAppearance() {
		return this.environmentAppearance;
	}

	public EnvironmentAppearance getEnvironmentAppearance() {
		return environmentAppearance;
	}

	@Override
	public abstract EnvironmentAppearance getRemoteAppearance();

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + this.getAppearance()
				+ "<->" + this.getRemoteAppearance() + "]";
	}

	@Override
	public void update(Object origin, Event event) {
		send(event);
	}
}
