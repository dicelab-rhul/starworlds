package uk.ac.rhul.cs.dice.starworlds.environment.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.actions.Action;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.PhysicalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.ActiveBodyAppearance;
import uk.ac.rhul.cs.dice.starworlds.entities.ActiveBody;
import uk.ac.rhul.cs.dice.starworlds.entities.Agent;
import uk.ac.rhul.cs.dice.starworlds.entities.agent.AbstractAgent;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.components.AbstractSensor;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.components.concrete.ListeningSensor;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.components.concrete.SeeingSensor;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.AbstractAmbient;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.Ambient;
import uk.ac.rhul.cs.dice.starworlds.environment.concrete.DefaultPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Simulator;
import uk.ac.rhul.cs.dice.starworlds.parser.DefaultConstructorStore.DefaultConstructor;
import uk.ac.rhul.cs.dice.starworlds.perception.AbstractPerception;
import uk.ac.rhul.cs.dice.starworlds.perception.CommunicationPerception;
import uk.ac.rhul.cs.dice.starworlds.perception.DefaultPerception;
import uk.ac.rhul.cs.dice.starworlds.perception.NullPerception;
import uk.ac.rhul.cs.dice.starworlds.utils.Pair;

/**
 * An abstract implementation of {@link Physics}. This physics handles the
 * Perceive, Decide, Execute cycle of all {@link Agent}s in its
 * {@link Environment} - it is the time keeper. The {@link Physics} is
 * responsible for executing any {@link Action}s that an {@link Agent} or
 * {@link ActiveBody} performs. These {@link Action}s default as follows: </br>
 * {@link SensingAction}, {@link CommunicationAction}, {@link PhysicalAction}.
 * For details on each type see their documentation. </br>
 * 
 * 
 * Known direct subclasses: {@link DefaultPhysics}.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public abstract class AbstractPhysics implements Physics, Simulator {

	private static final long DEFAULTFRAMELENGTH = 1000;
	private long framelength = DEFAULTFRAMELENGTH;
	protected AbstractEnvironment environment;
	protected AbstractAmbient state;

	// TODO change to not default
	private TimeState timestate = new TimeStateSerial();

	@DefaultConstructor
	public AbstractPhysics() {
	}

	public void simulate() {
		while (true) {
			cycle();
			try {
				Thread.sleep(framelength);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void cycle() {
		this.runAgents();
		this.executeActions();
		this.cycleAddition();
	}

	/**
	 * The method is 
	 */
	public abstract void cycleAddition();

	@Override
	public void runAgents() {
		timestate.simulate();
	}

	@Override
	public void executeActions() {
		doPhysicalActions(environment.getState().flushPhysicalActions());
		doSensingActions(environment.getState().flushSensingActions());
		doCommunicationActions(environment.getState()
				.flushCommunicationActions());
	}

	protected void doPhysicalActions(Collection<PhysicalAction> actions) {
		Collection<PhysicalAction> failedactions = new ArrayList<>();
		actions.forEach((PhysicalAction a) -> {
			if (!this.execute(a, environment.getState())) {
				failedactions.add(a);
			}
		});
		actions.removeAll(failedactions);
		// get the perceptions of all non-failed actions, this must be done so
		// that any perceptions are not out of sync
		actions.forEach((PhysicalAction a) -> {
			try {
				this.doPhysicalPerceptions(a, environment.getState());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	protected void doSensingActions(Collection<SensingAction> actions) {
		// System.out.println("DO SENSING ACTIONS"
		// + environment.getState().getSensingActions());
		actions.forEach((SensingAction s) -> this.execute(s,
				environment.getState()));
	}

	protected void doCommunicationActions(
			Collection<CommunicationAction<?>> actions) {
		actions.forEach((CommunicationAction<?> c) -> this.execute(c,
				environment.getState()));
	}

	// ***************************************************** //
	// ****************** PHYSICAL ACTIONS ***************** //
	// ***************************************************** //

	@Override
	public boolean execute(PhysicalAction action, Ambient context) {
		AbstractPerception<?> perception = null;
		if (isPossible(action, context)) {
			if (perform(action, context)) {
				if (verify(action, context)) {
					return true;
				}
			}
		}
		// notify the agent that their action has failed
		return false;
	}

	// finds the perceptions generated by a given physical action
	private void doPhysicalPerceptions(PhysicalAction action, Ambient context)
			throws Exception {
		Collection<AbstractPerception<?>> agentPerceptions = action
				.getAgentPerceptions(this, context);
		if (agentPerceptions != null) {
			environment.notify(action, action.getActor(), agentPerceptions,
					context);
		}

		Collection<AbstractPerception<?>> otherPerceptions = action
				.getOtherPerceptions(this, context);
		if (otherPerceptions != null) {
			Collection<AbstractAgent> others = new HashSet<>(state.getAgents());
			others.remove(state.getAgent(action.getActor().getId()));
			for (ActiveBody a : others) {
				environment.notify(action, a.getAppearance(), otherPerceptions,
						context);
			}
		}
	}

	@Override
	public boolean perform(PhysicalAction action, Ambient context) {
		try {
			return action.perform(this, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isPossible(PhysicalAction action, Ambient context) {
		try {
			return action.isPossible(this, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean verify(PhysicalAction action, Ambient context) {
		try {
			return action.verify(this, context);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ***************************************************** //
	// ****************** SENSING ACTIONS ****************** //
	// ***************************************************** //

	@Override
	public void execute(SensingAction action, Ambient context) {
		Set<Pair<String, Object>> perceptions = null;
		if (isPossible(action, context)) {
			perceptions = perform(action, context);
			verify(action, context); // TODO what to verify?
		}
		// should other agents be able to sense that this agent is sensing?
		if (perceptions != null) {
			Collection<AbstractPerception<?>> perceptionsToNotify = new HashSet<>();
			for (Pair<String, Object> p : perceptions) {
				if (p != null) {
					perceptionsToNotify
							.add(new DefaultPerception<Pair<String, Object>>(p));
				} else {
					perceptionsToNotify.add(new NullPerception());
				}
			}
			environment.notify(action, action.getActor(), perceptionsToNotify,
					context);
		}
	}

	@Override
	public Set<Pair<String, Object>> perform(SensingAction action,
			Ambient context) {
		return context.filterActivePerception(action.getKeys(), action);
	}

	@Override
	public boolean isPossible(SensingAction action, Ambient context) {
		String[] keys = action.getKeys();
		int count = 0;
		for (int i = 0; i < keys.length; i++) {
			String[] subkeys = keys[i].split("\\.");
			// TODO handle integer parameters
			if (context.environmentVariableExists(subkeys[0])) {
				for (int j = 1; j < subkeys.length; j++) {
					if (!context.filterExists(subkeys[j])) {
						keys[i] = null;
						count++;
						break;
					}
				}
			} else {
				keys[i] = null;
				count++;
			}
		}
		return count < keys.length;
	}

	@Override
	public boolean verify(SensingAction action, Ambient context) {
		return true;
	}

	// ***************************************************** //
	// *************** COMMUNICATION ACTIONS *************** //
	// ***************************************************** //

	@Override
	public void execute(CommunicationAction<?> action, Ambient context) {
		if (isPossible(action, context)) {
			perform(action, context);
			verify(action, context);
		}
		// TODO notify - should the sending agent be notified?
	}

	@Override
	public boolean perform(CommunicationAction<?> action, Ambient context) {
		if (action.getRecipientsIds().isEmpty()) {
			// send to all agents except the sender.
			Collection<AbstractAgent> recipients = new HashSet<>(
					state.getAgents());
			recipients.remove(action.getActor());
			recipients
					.forEach((AbstractAgent a) -> {
						Collection<AbstractPerception<?>> perceptionsToNotify = new HashSet<>();
						perceptionsToNotify.add(new CommunicationPerception<>(
								action.getPayload()));
						environment.notify(action, a.getAppearance(),
								perceptionsToNotify, context);
					});
		}
		// Clone the action as a special case - the perception should be
		// returned to the recipients that (may) reside in this environment
		CommunicationAction<?> clone = new CommunicationAction<>(action);
		clone.setLocalEnvironment(this.environment.getAppearance());
		action.getRecipientsIds()
				.forEach(
						(String s) -> {
							AbstractAgent agent = state.getAgent(s);
							if (agent != null) {
								ActiveBodyAppearance appearance = agent
										.getAppearance();
								Collection<AbstractPerception<?>> perceptionsToNotify = new HashSet<>();
								perceptionsToNotify
										.add(new CommunicationPerception<>(
												action.getPayload()));
								// the agent resides within this environment
								environment.notify(clone, appearance,
										perceptionsToNotify, context);
							}
						});
		// TODO if something failed
		return true;
	}

	@Override
	public boolean isPossible(CommunicationAction<?> action, Ambient context) {
		// TODO check if the message is for sub/super environments
		return true;
	}

	@Override
	public boolean verify(CommunicationAction<?> action, Ambient context) {
		// TODO check that the state has the proper perceptions
		return true;
	}

	@Override
	public void setEnvironment(AbstractEnvironment environment) {
		if (this.environment == null) {
			this.environment = environment;
			this.state = environment.getState();
		}
	}

	protected AbstractEnvironment getEnvironment() {
		return this.environment;
	}

	/**
	 * The state of a {@link Physics} that may be either serial or parallel see
	 * {@link TimeStateSerial}, {@link TimeStateParallel}. These
	 * {@link TimeState}s define the order in which the {@link Agent}s should
	 * run, namely whether the system is multi-threaded.
	 * 
	 * @author Ben Wilkins
	 *
	 */
	protected interface TimeState {
		public void start();

		public void simulate();
	}

	/**
	 * The implementation of {@link TimeState} for serial, agents will run in an
	 * arbitrary order one at a time.
	 * 
	 * @author Ben Wilkins
	 *
	 */
	protected class TimeStateSerial implements TimeState {

		public void simulate() {
			state.getAgents().forEach((AbstractAgent a) -> {
				a.run();
			});
		}

		@Override
		public void start() {

		}
	}

	/**
	 * The implementation of {@link TimeState} for parallel, agents will run in
	 * their own thread in parallel.
	 * 
	 * @author Ben Wilkins
	 *
	 */
	protected class TimeStateParallel implements TimeState {

		@Override
		public void start() {

		}

		@Override
		public void simulate() {
			// split into threads
			Collection<Thread> threads = new ArrayList<>();
			state.getAgents().forEach((AbstractAgent a) -> {
				Thread t = new Thread(a);
				threads.add(t);
				t.start();
			});
			waitForAgents(threads);
		}

		private void waitForAgents(Collection<Thread> threads) {
			try {
				for (Thread t : threads)
					t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Perceivable method for all {@link SeeingSensor}s. See
	 * {@link Physics#perceivable(AbstractSensor, AbstractPerception, Ambient)}.
	 */
	public boolean perceivable(SeeingSensor sensor,
			AbstractPerception<?> perception, Ambient context) {
		return SeeingSensor.DEFAULTPERCEPTION.isAssignableFrom(perception
				.getClass())
				|| SeeingSensor.NULLPERCEPTION.isAssignableFrom(perception
						.getClass());
	}

	/**
	 * Perceivable method for all {@link ListeningSensor}s. See
	 * {@link Physics#perceivable(AbstractSensor, AbstractPerception, Ambient)}.
	 */
	public boolean perceivable(ListeningSensor sensor,
			AbstractPerception<?> perception, Ambient context) {
		return ListeningSensor.COMMUNICATIONPERCEPTION
				.isAssignableFrom(perception.getClass());
	}

	@Override
	public boolean perceivable(AbstractSensor sensor,
			AbstractPerception<?> perception, Ambient context) {
		return AbstractSensor.NULLPERCEPTION.isAssignableFrom(perception
				.getClass());
	}

	/**
	 * Returns the id: "P" + {@link Environment} id that this {@link Physics}
	 * resides in.
	 * 
	 * @return the inherited id
	 */
	@Override
	public String getId() {
		return "P" + environment.getId();
	}

	/**
	 * Unused, a physics inherits its id from its {@link Environment}.
	 */
	@Override
	public void setId(String id) {
		// cannot set the id of a physics
	}

	protected void setTimeState(boolean serial) {
		if (serial) {
			timestate = new TimeStateSerial();
		} else {
			timestate = new TimeStateParallel();
		}
	}

	public void setFramelength(long framelength) {
		this.framelength = framelength;
	}

	@Override
	public void run() {
		this.simulate();
	}
}