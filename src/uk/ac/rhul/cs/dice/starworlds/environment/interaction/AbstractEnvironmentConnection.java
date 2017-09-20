package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;

public abstract class AbstractEnvironmentConnection implements
		EnvironmentConnection {

	private Boolean open = true;
	// The connection to the pair environment
	private AbstractEnvironmentConnection remoteConnection;
	// The appearance of the local environment
	private EnvironmentAppearance environmentAppearance;

	public AbstractEnvironmentConnection(
			EnvironmentAppearance environmentAppearance) {
		this.environmentAppearance = environmentAppearance;
	}

	public AbstractEnvironmentConnection(
			AbstractEnvironmentConnection remoteConnection,
			EnvironmentAppearance environmentAppearance) {
		this.remoteConnection = remoteConnection;
		this.environmentAppearance = environmentAppearance;
		remoteConnection.setRemoteConnection(this);
	}

	public EnvironmentAppearance getEnvironmentAppearance() {
		return environmentAppearance;
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
	public boolean isConnected() {
		return this.getRemoteConnection() != null;
	}

	@Override
	public EnvironmentConnection getRemoteConnection() {
		return remoteConnection;
	}

	@Override
	public EnvironmentAppearance getRemoteAppearance() {
		return this.remoteConnection.getAppearance();
	}

	private void setRemoteConnection(EnvironmentConnection mutualConnector) {
		this.remoteConnection = (AbstractEnvironmentConnection) mutualConnector;
	}

	@Override
	public EnvironmentAppearance getAppearance() {
		return this.environmentAppearance;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
				+ " ["
				+ this.getAppearance()
				+ "<->"
				+ ((this.getRemoteConnection() != null) ? this
						.getRemoteConnection().getAppearance() : " ") + "]";
	}
}
