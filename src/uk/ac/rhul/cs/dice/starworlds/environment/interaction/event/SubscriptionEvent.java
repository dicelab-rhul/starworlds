package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.utils.Identifiable;

public class SubscriptionEvent extends AbstractEvent {

	private static final long serialVersionUID = 7319333553482315563L;

	private Collection<Class<? extends Event>> events;

	public SubscriptionEvent(Identifiable origin,
			Collection<Class<? extends Event>> events) {
		super(origin);
		this.events = events;
	}

	public Collection<Class<? extends Event>> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return this.getOrigin() + " subscription to: " + events;
	}
}
