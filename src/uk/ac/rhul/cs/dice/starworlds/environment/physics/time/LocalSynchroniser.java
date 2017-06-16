package uk.ac.rhul.cs.dice.starworlds.environment.physics.time;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.base.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.base.EnvironmentConnectionManager;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;

public class LocalSynchroniser implements Synchroniser {

	private Collection<Synchroniser> subsynchronisers;
	private AbstractConnectedPhysics physics;
	private AbstractConnectedEnvironment environment;

	public LocalSynchroniser(AbstractConnectedEnvironment environment,
			Collection<AbstractConnectedEnvironment> subenvironments,
			Collection<AbstractConnectedEnvironment> neighbouringenvironments) {
		this.environment = environment;
		this.physics = (AbstractConnectedPhysics) environment.getPhysics();
		this.subsynchronisers = new ArrayList<>();
		// get all local synchronisers
		updateSynchronisers(subenvironments, neighbouringenvironments);
		System.out.println(this + " SUBSYNCS: " + subsynchronisers);
	}

	public LocalSynchroniser(AbstractConnectedEnvironment environment) {
		this.environment = environment;
		this.physics = (AbstractConnectedPhysics) environment.getPhysics();
		this.subsynchronisers = new ArrayList<>();
		// get all local synchronisers
		updateSynchronisers(null, null);
	}

	public void addRemoteSynchronisers(
			Collection<EnvironmentAppearance> remoteSubEnvironments,
			Collection<EnvironmentAppearance> remoteNeighbouringEnvironments) {
		remoteSubEnvironments.forEach((app) -> subsynchronisers
				.add(new RemoteSynchroniser(environment
						.getConnectedEnvironmentManager(), app)));
		// TODO neighbouring environments
	}

	public void addRemoteSubSynchroniser(
			EnvironmentAppearance remoteSubEnvironment) {
		subsynchronisers.add(new RemoteSynchroniser(environment
				.getConnectedEnvironmentManager(), remoteSubEnvironment));
	}

	public void updateSynchronisers(
			Collection<AbstractConnectedEnvironment> subenvironments,
			Collection<AbstractConnectedEnvironment> neighbouringenvironments) {
		// TODO check that the synchroniser has not been added already
		// add local synchronisers
		if (subenvironments != null) {
			subenvironments.forEach((AbstractConnectedEnvironment e) -> {
				// TODO if the connection starts closed?
					subsynchronisers.add(((AbstractConnectedPhysics) e
							.getPhysics()).getSynchroniser());
				});
		}
	}

	@Override
	public void runagents() {
		// TODO multithreaded
		for (Synchroniser s : subsynchronisers) {
			s.runagents();
		}
		physics.runAgents();
	}

	@Override
	public void executeactions() {
		// TODO multithreaded
		for (Synchroniser s : subsynchronisers) {
			s.executeactions();
		}
		physics.executeActions();
	}

	@Override
	public void propagateActions() {
		// TODO multithreaded
		for (Synchroniser s : subsynchronisers) {
			s.propagateActions();
		}
		physics.propagateActions();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " : " + physics.getId();
	}

}
