package uk.ac.rhul.cs.dice.starworlds.environment.concrete;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.base.AbstractEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.base.interfaces.Message;

public class DefaultEnvironmentConnection extends AbstractEnvironmentConnection {

	public DefaultEnvironmentConnection(
			DefaultEnvironmentConnection mutualConnector,
			EnvironmentAppearance appearance) {
		super(mutualConnector, appearance);
		// System.out.println("CONNECTED: " + this);
	}

	public DefaultEnvironmentConnection(EnvironmentAppearance appearance) {
		super(appearance);
	}

	@Override
	public void send(Message<?> obj) {
		super.send(obj);
	}

	@Override
	public void receive(Message<?> obj) {
		if (isOpen()) {
			this.notifyReceivers(obj);
		}
	}
}
