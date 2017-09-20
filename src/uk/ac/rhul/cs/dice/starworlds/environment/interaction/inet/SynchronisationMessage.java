package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import uk.ac.rhul.cs.dice.starworlds.environment.physics.time.SyncPoint;

public class SynchronisationMessage extends INetAbstractMessage<SyncPoint> {

	private static final long serialVersionUID = 1L;

	public SynchronisationMessage(SyncPoint sync) {
		super(sync);
	}

	@Override
	public Object getOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

}
