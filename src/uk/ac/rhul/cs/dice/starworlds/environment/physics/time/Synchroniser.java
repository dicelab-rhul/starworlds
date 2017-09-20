package uk.ac.rhul.cs.dice.starworlds.environment.physics.time;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Simulator;

public interface Synchroniser extends EventListener, Simulator {

	public AbstractConnectedEnvironment getEnvironment();

	public void runActors();

	public void propagateActions();

	public void executeActions();
}
