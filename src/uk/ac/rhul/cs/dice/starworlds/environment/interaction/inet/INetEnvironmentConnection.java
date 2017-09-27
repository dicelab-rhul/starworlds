package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.util.Observable;
import java.util.Observer;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.AbstractEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.EnvironmentConnector;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class INetEnvironmentConnection extends AbstractEnvironmentConnection
		implements Observer {

	private INetSlave slave;
	private EnvironmentAppearance remoteEnvironmentAppearance;

	public INetEnvironmentConnection(INetSlave slave,
			EnvironmentConnector connector,
			EnvironmentAppearance environmentAppearance,
			EnvironmentAppearance remoteEnvironmentAppearance) {
		super(connector, environmentAppearance);
		this.slave = slave;
		slave.addObserver(this);
		this.remoteEnvironmentAppearance = remoteEnvironmentAppearance;
	}

	@Override
	public void send(Event event) {
		slave.send(event);
	}

	// called by the slave when some a new message has been received
	@Override
	public void update(Observable o, Object arg) {
		if (this.isOpen()) {
			if (Event.class.isAssignableFrom(arg.getClass())) {
				receive((Event) arg);
			} else {
				System.err.println(this + " receive invalid data: " + arg
						+ " is not an Event");
			}
		}
	}

	@Override
	public boolean isConnected() {
		return this.slave.getRemoteSocketAddress() != null;
	}

	@Override
	public EnvironmentAppearance getRemoteAppearance() {
		return remoteEnvironmentAppearance;
	}
}
