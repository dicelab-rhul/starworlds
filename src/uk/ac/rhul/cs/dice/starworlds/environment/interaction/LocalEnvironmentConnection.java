package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;

public class LocalEnvironmentConnection extends AbstractEnvironmentConnection {

	protected LocalEnvironmentConnection remoteConnection;

	public LocalEnvironmentConnection(EnvironmentConnector connector,
			EnvironmentAppearance environmentAppearance) {
		super(connector, environmentAppearance);

	}

	public LocalEnvironmentConnection(EnvironmentConnector connector,
			LocalEnvironmentConnection remoteConnection,
			EnvironmentAppearance environmentAppearance) {
		super(connector, environmentAppearance);
		this.remoteConnection = remoteConnection;
		this.remoteConnection.setRemoteConnection(this);
	}

	public EnvironmentConnection getRemoteConnection() {
		return remoteConnection;
	}

	@Override
	public boolean isConnected() {
		return this.getRemoteConnection() != null;
	}

	@Override
	public EnvironmentAppearance getRemoteAppearance() {
		return this.remoteConnection.getAppearance();
	}

	@Override
	public void send(Event event) {
		if (isOpen()) {
			this.getRemoteConnection().receive(event);
		}
	}

	private void setRemoteConnection(LocalEnvironmentConnection connection) {
		this.remoteConnection = connection;
	}
}
