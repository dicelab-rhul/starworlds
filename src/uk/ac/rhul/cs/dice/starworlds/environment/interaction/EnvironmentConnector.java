package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventHandler;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SubscriptionEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SynchronisationEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.initialisation.InvalidConfigurationException;

public class EnvironmentConnector extends EventHandler implements EventListener {

	private class SubscriptionEventListener implements EventListener {
		@Override
		public void update(Object origin, Event event) {
			EnvironmentConnector.this.publishTo(origin,
					(SubscriptionEvent) event);
		}
	}

	private class LocalEventHandler extends EventHandler implements
			EventListener {
		@Override
		public void update(Object origin, Event event) {
			this.notifyListeners(event);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + ":" + localEnvironment;
		}
	}

	// store what this environment subscribes to so that it can be checked

	// store the actions so that they can be checked (for loops)

	private SubscriptionEventListener subscriptionHandler;
	private LocalEventHandler eventHandler;

	private EnvironmentAppearance localEnvironment;

	private Map<EnvironmentAppearance, LocalEnvironmentConnection> environmentConnections;
	private Map<EnvironmentAppearance, LocalEnvironmentConnection> subEnvironmentConnections;
	private Map<EnvironmentAppearance, LocalEnvironmentConnection> neighbourEnvironmentConnections;
	private LocalEnvironmentConnection superEnvironmentConnection;

	private Map<String, Event> eventmap = new HashMap<>();

	public EnvironmentConnector(EnvironmentAppearance localEnvironment) {
		eventHandler = new LocalEventHandler();
		this.localEnvironment = localEnvironment;
		this.environmentConnections = new HashMap<>();
		subEnvironmentConnections = new HashMap<>();
		neighbourEnvironmentConnections = new HashMap<>();
		subscriptionHandler = new SubscriptionEventListener();
		this.addEventListener(SubscriptionEvent.class, subscriptionHandler);
		this.addEventListener(Event.class, eventHandler);
	}

	public void addLocalEventListener(Class<? extends Event> event,
			EventListener listener) {
		eventHandler.addEventListener(event, listener);
	}

	public void addLocalEventListener(Class<? extends Event> event,
			Collection<EventListener> listeners) {
		eventHandler.addEventListener(event, listeners);
	}

	public void clearEvents() {
		this.eventmap.clear();
	}

	@Override
	public void update(Object origin, Event event) {
		if (!this.eventmap.containsKey(event.getId())) {
			this.eventmap.put(event.getId(), event);
			Collection<EventListener> listeners = eventListeners.get(event
					.getClass());
			if (listeners != null) {
				for (EventListener l : listeners) {
					if (!l.equals(origin)) {
//						System.out.println(localEnvironment + " update: " + l
//								+ " with event: " + event);
						l.update(origin, event);
					}
				}
			}
		}
	}

	@Override
	public void notifyListeners(Event e) {
		System.err.println("DO NOT USE NOTIFY LISTENERS, USE UPDATE IN:"
				+ this.getClass().getSimpleName());
		// update notifies all listeners, the notify is special and requires
		// more information than the arguments of this method can provide.
	}

	/**
	 * Set up publishing events from this {@link Environment} to another, the
	 * event {@link SubscriptionEvent} was received from another environment and
	 * was the result of a call to
	 * {@link EnvironmentConnector#subscribeTo(Collection, EnvironmentAppearance)}
	 * in that other {@link Environment}.
	 * 
	 * @param origin
	 *            : the {@link EventListener} to publish the {@link Event}s to
	 * @param event
	 *            : containing the information necessary for this
	 *            {@link Environment} to publish events to the subscribing
	 *            {@link Environment}
	 */
	public void publishTo(Object origin, SubscriptionEvent event) {
		for (Class<? extends Event> e : event.getEvents())
			this.addEventListener(e, (EventListener) origin);
	}

	/**
	 * Subscribe to the {@link Event}s given by the <code>events</code> argument
	 * from the given {@link Environment}.
	 * 
	 * @param environment
	 *            : to subscribe to
	 * @param events
	 *            : to subscribe to
	 * 
	 */
	public void subscribeTo(EnvironmentAppearance environment,
			Collection<Class<? extends Event>> events) {
		environmentConnections.get(environment).update(this,
				new SubscriptionEvent(this.localEnvironment, events));
	}

	public void sychronise(SynchronisationEvent event) {
		update(eventHandler, event);
	}

	protected void addEnvironment(LocalEnvironmentConnection connection) {
		environmentConnections
				.put(connection.getRemoteAppearance(), connection);
		// all connections should subscribe to synchronisation events by default
		this.addEventListener(SynchronisationEvent.class, connection);
	}

	public void addSubEnvironment(LocalEnvironmentConnection connection) {
		addEnvironment(connection);
		subEnvironmentConnections.put(connection.getRemoteAppearance(),
				connection);
	}

	public void addNeighbourEnvironment(LocalEnvironmentConnection connection) {
		addEnvironment(connection);
		neighbourEnvironmentConnections.put(connection.getRemoteAppearance(),
				connection);
	}

	public void setSuperEnvironment(LocalEnvironmentConnection connection) {
		if (this.superEnvironmentConnection == null) {
			addEnvironment(connection);
			this.superEnvironmentConnection = connection;
		} else {
			throw new InvalidConfigurationException(connection.getAppearance()
					+ " cannot have multiple super environments: "
					+ superEnvironmentConnection.getRemoteAppearance()
					+ " and " + connection.getRemoteAppearance());
		}
	}

	public EnvironmentConnection getConnection(EnvironmentAppearance environment) {
		return environmentConnections.get(environment);
	}

	public EnvironmentAppearance getLocalEnvironment() {
		return localEnvironment;
	}

	public Collection<EnvironmentAppearance> getConnectedEnvironments() {
		return environmentConnections.keySet();
	}

	public Collection<EnvironmentAppearance> getNeighbouringEnvironments() {
		return neighbourEnvironmentConnections.keySet();
	}

	public Collection<EnvironmentAppearance> getSubEnvironments() {
		return subEnvironmentConnections.keySet();
	}

	public EnvironmentAppearance getSuperEnvironment() {
		return superEnvironmentConnection.getAppearance();
	}

	public boolean hasNeighbouringEnvironments() {
		return !neighbourEnvironmentConnections.isEmpty();
	}

	public boolean hasSubEnvironments() {
		return !subEnvironmentConnections.isEmpty();
	}

	public boolean hasSuperEnvironment() {
		return superEnvironmentConnection != null;
	}

}
