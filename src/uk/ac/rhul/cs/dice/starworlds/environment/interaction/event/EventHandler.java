package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.utils.TypeMap;

public class EventHandler {

	protected TypeMap<Event, EventListener> eventListeners;

	public EventHandler() {
		eventListeners = new TypeMap<Event, EventListener>(Event.class);
	}

	public void addEventListener(Class<? extends Event> event,
			EventListener listener) {
		eventListeners.add(event, listener);
	}

	public void addEventListener(Class<? extends Event> event,
			Collection<EventListener> listeners) {
		eventListeners.add(event, listeners);
	}

	public void notifyListeners(Event e) {
		Collection<EventListener> listeners = eventListeners.get(e.getClass());
		if (listeners != null) {
			for (EventListener l : listeners) {
				l.update(this, e);
			}
		}
	}
}
