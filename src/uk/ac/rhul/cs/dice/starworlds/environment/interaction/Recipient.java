package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;

/**
 * A simple Observable class that contains some {@link Receiver}s who should be
 * notified using the {@link Recipient#notifyReceivers(Object)} method when any
 * data is received by this {@link Recipient}.
 * 
 * @author Ben
 *
 */
public abstract class Recipient {

	private Set<Receiver> receivers;

	public Recipient() {
		receivers = new HashSet<>();
	}

	public void notifyReceivers(Event event) {
		for (Receiver r : receivers) {
			r.receive(this, event);
		}
	}

	public void addReciever(Receiver receiver) {
		this.receivers.add(receiver);
	}
}
