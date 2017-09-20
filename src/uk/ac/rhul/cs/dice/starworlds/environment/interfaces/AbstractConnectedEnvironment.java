package uk.ac.rhul.cs.dice.starworlds.environment.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.actions.Action;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.AbstractAmbient;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.Ambient;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.EnvironmentConnectionManager;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.ActionEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SubscriptionEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.SynchronisationEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.Physics;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.time.EnvironmentSynchroniser;
import uk.ac.rhul.cs.dice.starworlds.environment.subscription.ConnectedSubscriptionHandler;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser.ConnectionInitialiser;
import uk.ac.rhul.cs.dice.starworlds.perception.AbstractPerception;

public abstract class AbstractConnectedEnvironment extends AbstractEnvironment {

	private static final Collection<Class<? extends AbstractEnvironmentalAction>> DEFAULTSUBSCRIPTIONACTIONS = new ArrayList<>();
	static {
		DEFAULTSUBSCRIPTIONACTIONS.add(SensingAction.class);
		DEFAULTSUBSCRIPTIONACTIONS.add(CommunicationAction.class);
	}

	public static final String SUBSCRIBE = "SUBSCRIBE", ACTION = "ACTION",
			PERCEPTION = "PERCEPTION";

	protected EnvironmentConnectionManager envconManager;
	// protected Map<Pair<String, Integer>, AmbientRelation> initialConnections;
	protected ActionEventListener actionEventListener;

	/**
	 * Constructor. This Constructor allows local {@link Environment}s to
	 * connect to this one.
	 * 
	 * @param ambient
	 *            : a {@link Ambient} instance.
	 * @param physics
	 *            : the {@link Physics} of the {@link Environment}
	 * @param appearance
	 *            : the {@link Appearance} of the {@link Environment}
	 * @param possibleActions
	 *            : a {@link Collection} of {@link Action}s that are possible in
	 *            this {@link Environment}
	 * @param bounded
	 *            : a {@link Boolean} value indicating whether the
	 *            {@link Environment} is bounded or not.
	 */
	public AbstractConnectedEnvironment(
			AbstractAmbient ambient,
			AbstractConnectedPhysics physics,
			EnvironmentAppearance appearance,
			Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions,
			Boolean bounded) {
		super(ambient, physics, new ConnectedSubscriptionHandler(), appearance,
				possibleActions, bounded);
		this.envconManager = new EnvironmentConnectionManager(this, null);
		this.initialiseEventListeners();
	}

	/**
	 * Constructor. This Constructor allows local and remote {@link Environment}
	 * s to connect to this one. Remote {@link Environment}s should connect via
	 * the give port.
	 *
	 * @param port
	 *            : the port that any remote {@link Environment} will try to
	 *            make connections to
	 * @param ambient
	 *            : a {@link Ambient} instance
	 * @param physics
	 *            : the {@link Physics} of the {@link Environment}
	 * 
	 * @param appearance
	 *            : the {@link Appearance} of the {@link Environment}.
	 * @param possibleActions
	 *            : a {@link Collection} of {@link Action}s that are possible in
	 *            this {@link Environment}
	 * @param bounded
	 *            : a {@link Boolean} value indicating whether the
	 *            {@link Environment} is bounded or not
	 */
	public AbstractConnectedEnvironment(
			Integer port,
			AbstractAmbient ambient,
			AbstractConnectedPhysics physics,
			EnvironmentAppearance appearance,
			Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions,
			Boolean bounded) {
		super(ambient, physics, new ConnectedSubscriptionHandler(), appearance,
				possibleActions, bounded);
		this.envconManager = new EnvironmentConnectionManager(this, port);
		this.initialiseEventListeners();
	}

	/**
	 * This method is called after all {@link Environment}s have been created
	 * and are connected by a {@link ConnectionInitialiser}. It should be used
	 * to setting parameter in the {@link Ambient} etc. By Default this method
	 * handles the initial subscription of {@link ActionEvent}s to connected
	 * {@link Environment}s. See
	 * {@link AbstractConnectedEnvironment#initialActionSubscribe()}. It also
	 * initialises the {@link EnvironmentSynchroniser}s of the system. See
	 * {@link AbstractConnectedEnvironment#initialiseSynchroniser()}.
	 */
	@Override
	public void postInitialisation() {
		// this.initialActionSubscribe();
		this.initialiseSynchroniser();
	}

	protected void initialiseSynchroniser() {
		this.getPhysics().getSynchroniser().initialiseSynchronisation();
	}

	public void synchronise(SynchronisationEvent event) {
		envconManager.synchronise(event);
	}

	protected void initialiseEventListeners() {
		actionEventListener = new ActionEventListener();
		// synchronisation event listener
		this.envconManager.addLocalEventListener(SynchronisationEvent.class,
				this.getPhysics().getSynchroniser());
		// action event listener
		this.envconManager.addLocalEventListener(ActionEvent.class,
				actionEventListener);
	}

	public void clearActionListener() {
		actionEventListener.clearEvents();
	}

	public void sendPerception(AbstractEnvironmentalAction action,
			AbstractPerception<?> perception) {
		if (perception != null) {
			// this.envconManager.sendToEnvironment(actionEventListener
			// .getSender(action), new INetDefaultMessage(PERCEPTION,
			// new Pair<>(action, perception)));
		}
	}

	public void sendPerceptions(AbstractEnvironmentalAction action,
			Collection<AbstractPerception<?>> perceptions) {
		if (perceptions != null) {
			if (!perceptions.isEmpty()) {
				// this.envconManager.sendToEnvironment(actionEventListener
				// .getSender(action), new INetDefaultMessage(PERCEPTION,
				// new Pair<>(action, perceptions)));
			}
		}
	}

	public EnvironmentConnectionManager getConnectedEnvironmentManager() {
		return envconManager;
	}

	/**
	 * This method should provide the {@link Class}es of the
	 * {@link AbstractEnvironmentalAction}s that this {@link Environment} should
	 * subscribe to at initialisation (this may be none, specified by an empty
	 * {@link Collection}). That is, the {@link Action}s that it wishes to be
	 * notified of if they occur in this {@link Environment}s sub, neighbouring
	 * or super {@link Environment}(s). This method should be overridden in a
	 * subclass for custom initial action subscription. If this
	 * {@link Environment} should not subscribe to all the prior mentioned
	 * {@link Environment}s the
	 * {@link AbstractConnectedEnvironment#initialActionSubscribe()} should be
	 * overridden. The default {@link Action}s to subscribe are
	 * {@link SensingAction} and {@link CommunicationAction}. </br> If the
	 * subscription is going to happen over network, the return collection must
	 * be {@link Serializable}.
	 * 
	 * @return a {@link Collection} of default
	 *         {@link AbstractConnectedEnvironment} {@link Class}es to subscribe
	 *         to
	 */
	protected Collection<Class<? extends AbstractEnvironmentalAction>> getInitialActionsToSubscribe() {
		return DEFAULTSUBSCRIPTIONACTIONS;
	}

	/**
	 * Getter for a subscription message with the default {@link Action}s
	 * specified by
	 * {@link AbstractConnectedEnvironment#getInitialActionsToSubscribe()}.
	 * 
	 * @return the subscription message
	 */
	public SubscriptionEvent getDefaultActionSubscriptionEvent() {
		return null; // new SubscriptionEvent(this.getAppearance(),
						// getInitialActionsToSubscribe());
	}

	@Override
	public Boolean isSimple() {
		return this.envconManager.hasSubEnvironments();
	}

	@Override
	public synchronized void updateAmbient(AbstractEnvironmentalAction action) {
		action.setLocalEnvironment(this.getAppearance());
		super.updateAmbient(action);
	}

	public <T extends AbstractEnvironmentalAction> void sendAction(T action) {
		if (action != null) {
//			Collection<EnvironmentAppearance> toSend = this.subscriber
//					.getEnvironmentsFromSubscribedAction(action.getClass());
//			if (toSend != null) {
//				// dont send to the environment that the action originated from!
//				toSend.remove(actionEventListener.getSender(action));
//				// if (!toSend.isEmpty()) {
//				// this.envconManager.sendToEnvironments(toSend,
//				// new INetDefaultMessage(ACTION,
//				// (Serializable) action));
//				// }
//			}
		}
	}

	public <T extends AbstractEnvironmentalAction> void sendActions(
			Collection<T> actions) {
		if (actions != null) {
			if (!actions.isEmpty()) {
//				Collection<EnvironmentAppearance> toSend = this.subscriber
//						.getEnvironmentsFromSubscribedAction(actions.iterator()
//								.next().getClass());
//				if (toSend != null) {
//					if (!toSend.isEmpty()) {
//						// TODO dont send to environments that have already
//						// received it
//						// this.envconManager.sendToEnvironments(toSend,
//						// new INetDefaultMessage(ACTION,
//						// (Serializable) actions));
//					}
//				}
			}
		}
	}

	@Override
	public AbstractConnectedPhysics getPhysics() {
		return (AbstractConnectedPhysics) this.physics;
	}
}
