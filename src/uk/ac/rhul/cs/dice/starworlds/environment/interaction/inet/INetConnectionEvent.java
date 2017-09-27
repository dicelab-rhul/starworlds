package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.AbstractEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldAddress;

public class INetConnectionEvent extends AbstractEvent {

	private static final long serialVersionUID = -7043018639651261620L;
	private Integer port;
	private EnvironmentAppearance localEnv;
	private String remoteEnv;
	private ConnectionEventStatus status;

	public INetConnectionEvent(WorldAddress origin,
			ConnectionEventStatus status, EnvironmentAppearance localEnv,
			String remoteEnv, Integer port) {
		super(origin);
		this.localEnv = localEnv;
		this.remoteEnv = remoteEnv;
		this.port = port;
		this.status = status;
	}

	@Override
	public WorldAddress getOrigin() {
		return (WorldAddress) super.getOrigin();
	}

	public EnvironmentAppearance getLocalEnv() {
		return localEnv;
	}

	public String getRemoteEnv() {
		return remoteEnv;
	}

	public Integer getPort() {
		return port;
	}

	public ConnectionEventStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": Origin: "
				+ this.getOrigin() + " " + getLocalEnv() + "-" + getRemoteEnv()
				+ " : " + port;
	}
}