package uk.ac.rhul.cs.dice.starworlds.experiment.synchronisation;

import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;

public class ExperimentalPhysics extends AbstractConnectedPhysics {

	private static final Long WAITTIME = 5000l;

	@Override
	public void cycleAddition() {
	}

	@Override
	public void executeActions() {
		try {
			Long sleep = (long) (Math.random() * WAITTIME);
			System.out.println(this.getId() + " SLEEP: " + sleep);
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.executeActions();
	}

	@Override
	public void propagateActions() {
		try {
			Long sleep = (long) (Math.random() * WAITTIME);
			System.out.println(this.getId() + " SLEEP: " + sleep);
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.propagateActions();
	}

	@Override
	public void runActors() {
		try {
			Long sleep = (long) (Math.random() * WAITTIME);
			System.out.println(this.getId() + " SLEEP: " + sleep);
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.runActors();
	}
}
