package uk.ac.rhul.cs.dice.starworlds.environment.physics.time;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.dice.starworlds.actions.Action;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.entities.Agent;
import uk.ac.rhul.cs.dice.starworlds.entities.agent.Mind;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SynchronisationEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractPhysics;

public class EnvironmentSynchroniser implements Synchroniser {

	private Map<EnvironmentAppearance, SyncPoint> synchronisers;
	private AbstractConnectedPhysics physics;
	private int cycleTime = 0;

	public EnvironmentSynchroniser(AbstractConnectedEnvironment environment) {
		this.physics = (AbstractConnectedPhysics) environment.getPhysics();
		this.synchronisers = new HashMap<>();
	}

	public void initialiseSynchronisation() {
		// add all of the immediately connected environments
		// addSynchronisers(environments);
		/*
		 * Send an event out to all environments to tell them that you exist
		 */
		synchronise(SyncPoint.NONE);
	}

	/**
	 * Beging the simulation for the {@link Environment} associated with this
	 * {@link Synchroniser}. The order of execution is as follows:
	 * {@link SuperSynchroniser#runActors() runagents()}, </br>
	 * {@link SuperSynchroniser#propagateActions() propagateActions()}, </br>
	 * {@link SuperSynchroniser#executeActions() executeactions()},</br>
	 * {@link SuperSynchroniser#cycleAddition() cycleAddition()}.</br>
	 */
	@Override
	public void simulate() {

		System.out.println(this + " " + synchronisers + " SIMULATING...");
		while (true) {
			System.out.println("******************* CYCLE *******************");
			cycleTime++;
			this.runActors();
			this.propagateActions();
			this.executeActions();
			this.cycleAddition();
			try {
				Thread.sleep(physics.getFramelength());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void run() {
		simulate();
	}

	public void addSynchroniser(EnvironmentAppearance environment) {
		if (cycleTime == 0) {
			if (!synchronisers.containsKey(environment)) {
				synchronisers.put(environment, SyncPoint.NONE);
			}
		} else {
			// TODO
			System.err
					.println("Dynamic environment synchronisation is not yet implemented."
							+ System.lineSeparator()
							+ " if you wish to implement it see the "
							+ this.getClass().getSimpleName() + " class");
		}
	}

	public void addSynchronisers(Collection<EnvironmentAppearance> environments) {
		if (cycleTime == 0) {
			for (EnvironmentAppearance e : environments) {
				synchronisers.put(e, SyncPoint.NONE);
			}
		} else {
			// TODO
			System.err
					.println("Dynamic environment synchronisation is not yet implemented."
							+ System.lineSeparator()
							+ " if you wish to implement it see the "
							+ this.getClass().getSimpleName() + " class");
		}
	}

	/**
	 * Calls the {@link Agent#run()} method for all {@link Agent}s in the
	 * system. Each {@link Agent} performs its {@link Mind#cycle(Object...)
	 * cycle(Object...)} procedure here. Calls to sub {@link Environment}
	 * {@link EnvironmentSynchroniser}s are made recursively down the hierarchy,
	 * with the lower {@link EnvironmentSynchroniser}s executing first.
	 */
	@Override
	public void runActors() {
		// TODO multithreaded
		printStep("RUNNINGAGENTS...");
		physics.runActors();
		doneLocal(SyncPoint.RUNAGENTS);

	}

	/**
	 * Propagates {@link Action}s between {@link Environment}s local or remote.
	 * Calls to sub {@link Environment} {@link EnvironmentSynchroniser}s are
	 * made recursively down the hierarchy, with the lower
	 * {@link EnvironmentSynchroniser}s executing first.
	 */
	@Override
	public void propagateActions() {
		// TODO multithreaded
		printStep(" PROPAGATING ACTIONS...");
		physics.propagateActions();
		doneLocal(SyncPoint.PROPAGATEACTIONS);
		// done(SyncPoint.PROPAGATEACTIONS);
		// wait(syncWithRemoteEnvironments(SyncPoint.PROPAGATEACTIONS));
	}

	/**
	 * Executes {@link Action}s that have been received by all
	 * {@link Environment} after actions have been propagated. Calls to sub
	 * {@link Environment} {@link EnvironmentSynchroniser}s are made recursively
	 * down the hierarchy, with the lower {@link EnvironmentSynchroniser}s
	 * executing first.
	 */
	@Override
	public void executeActions() {
		// TODO multithreaded
		printStep(" EXECUTING ACTIONS...");
		physics.executeActions();
		doneLocal(SyncPoint.EXECUTEACTIONS);
		// done(SyncPoint.EXECUTEACTIONS);
		// wait(syncWithRemoteEnvironments(SyncPoint.EXECUTEACTIONS));
	}

	private boolean waitLocal(SyncPoint syncPoint) {
		// System.out.println(synchronisers);
		for (SyncPoint sp : synchronisers.values()) {
			if (sp != syncPoint) {
				return true;
			}
		}
		return false;
	}

	protected void doneLocal(SyncPoint reached) {
		synchronise(reached);
		while (waitLocal(reached))
			;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void synchronise(SyncPoint reached) {
		getEnvironment().synchronise(
				new SynchronisationEvent(getLocalEnvironmentAppearance(),
						reached, cycleTime));
	}

	protected EnvironmentAppearance getLocalEnvironmentAppearance() {
		return physics.getEnvironment().getAppearance();
	}

	protected void printStep(String message) {
		System.out.println(this + " " + this.synchronisers + message + " @ "
				+ System.currentTimeMillis() / 1000);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " : " + physics.getId();
	}

	/**
	 * A method that should be overridden in a subclass of
	 * {@link EnvironmentSynchroniser} if there are any additional things to be
	 * done at the end of a cycle. See
	 * {@link EnvironmentSynchroniser#simulate()}. This method may call
	 * {@link EnvironmentSynchroniser#cycleAddition()} for sub
	 * {@link EnvironmentSynchroniser} or perhaps call
	 * {@link AbstractPhysics#cycleAddition()}.
	 */
	public void cycleAddition() {
		physics.cycleAddition();
	}

	@Override
	public synchronized void update(Object origin, Event event) {
		SynchronisationEvent sevent = (SynchronisationEvent) event;
		System.out.println(this.getLocalEnvironmentAppearance()
				+ ": SYNCEVENT: " + event + " -> " + this.synchronisers + " @ "
				+ System.currentTimeMillis() / 1000);
		if (this.synchronisers.replace(
				(EnvironmentAppearance) sevent.getOrigin(),
				sevent.getSyncPoint()) == null) {
			if (sevent.getSyncPoint() == SyncPoint.NONE) {
				System.out.println(this.getLocalEnvironmentAppearance()
						+ ": adding new Synchroniser: " + event.getOrigin());
				addSynchroniser(sevent.getOrigin());
			} else {
				unknownEnvrionmentMessage(sevent);
			}
		}
	}

	private void unknownEnvrionmentMessage(SynchronisationEvent event) {
		System.out.println("Previously unknown origin " + event.getOrigin()
				+ " has sent a sync event: " + event + "."
				+ System.lineSeparator()
				+ "To synchronise more environments correctly send with "
				+ SyncPoint.class.getSimpleName() + "." + SyncPoint.NONE);
	}

	@Override
	public AbstractConnectedEnvironment getEnvironment() {
		return physics.getEnvironment();
	}
}
