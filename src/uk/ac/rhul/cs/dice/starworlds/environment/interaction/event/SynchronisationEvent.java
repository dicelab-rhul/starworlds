package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.time.SyncPoint;

public class SynchronisationEvent extends AbstractEvent {

	private static final long serialVersionUID = 8182351289118970052L;

	// private Long realtime; //maybe?
	private int cycletime;
	private SyncPoint syncPoint;

	public SynchronisationEvent(EnvironmentAppearance origin,
			SyncPoint syncPoint, int cycletime) {
		super(origin);
		this.syncPoint = syncPoint;
		this.cycletime = cycletime;
	}

	@Override
	public EnvironmentAppearance getOrigin() {
		return (EnvironmentAppearance) super.getOrigin();
	}

	public SyncPoint getSyncPoint() {
		return syncPoint;
	}

	public int getCycletime() {
		return cycletime;
	}

	@Override
	public String toString() {
		return super.toString() + " SyncPoint:" + syncPoint + " CycleTime:"
				+ cycletime;
	}
}
