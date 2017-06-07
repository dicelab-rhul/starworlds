package uk.ac.rhul.cs.dice.starworlds.environment.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.CommunicationAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.PhysicalAction;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.SensingAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.entities.ActiveBody;
import uk.ac.rhul.cs.dice.starworlds.entities.Agent;
import uk.ac.rhul.cs.dice.starworlds.entities.PassiveBody;
import uk.ac.rhul.cs.dice.starworlds.entities.PhysicalBody;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.components.Actuator;
import uk.ac.rhul.cs.dice.starworlds.entities.agents.components.Sensor;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.Physics;
import uk.ac.rhul.cs.dice.starworlds.utils.Pair;

public abstract class AbstractState implements State {

	private final static String AGENTSKEY = "AGENTS",
			ACTIVEBODIESKEY = "ACTIVEBODIES",
			PASSIVEBODIESKEY = "PASSIVEBODIES", RANDOM = "RANDOM",
			SELF = "SELF", APPEARANCEFILTER = "APPEARANCE";

	private HashMap<String, Object> environmentVariables;
	private HashMap<String, Filter> filters;

	private List<SensingAction> sensingActions;
	private List<PhysicalAction> physicalActions;
	private List<CommunicationAction<?>> communicationActions;
	protected AbstractPhysics physics;

	public AbstractState(AbstractPhysics physics) {
		this.sensingActions = new ArrayList<>();
		this.physicalActions = new ArrayList<>();
		this.communicationActions = new ArrayList<>();
		this.environmentVariables = new HashMap<>();
		this.filters = new HashMap<>();
		initialiseEnvironmentVariables(physics);
		this.physics = physics;
	}

	/**
	 * Initialises the default environment variables, namely, {@link Agent}s,
	 * {@link ActiveBody}s, {@link PassiveBody}s. This method should be
	 * Overridden if a user wishes to add more environment variables. When
	 * adding more use the {@link State#addEnvironmentVariable(String, Object)}
	 * method.
	 */
	protected void initialiseEnvironmentVariables(AbstractPhysics physics) {
		environmentVariables.put(AGENTSKEY, physics.getAgents());
		environmentVariables.put(ACTIVEBODIESKEY, physics.getActiveBodies());
		environmentVariables.put(PASSIVEBODIESKEY, physics.getPassiveBodies());
		filters.put(RANDOM, new RandomEnvironmentVariable());
		filters.put(SELF, new SelfFilter());
		filters.put(APPEARANCEFILTER, new AppearanceFilter());
	}

	@Override
	public void addSenseAction(SensingAction action) {
		this.sensingActions.add(action);
		// System.out.println(getSensingActions());
	}

	@Override
	public void addPhysicalAction(PhysicalAction action) {
		this.physicalActions.add(action);
	}

	@Override
	public void addCommunicationAction(CommunicationAction<?> action) {
		this.communicationActions.add(action);
	}

	@Override
	public List<SensingAction> getSensingActions() {
		return sensingActions;
	}

	@Override
	public List<PhysicalAction> getPhysicalActions() {
		return physicalActions;
	}

	@Override
	public List<CommunicationAction<?>> getCommunicationActions() {
		return communicationActions;
	}

	/**
	 * Getter for the environment variable key chain as a single {@link String}.
	 * 
	 * @return the key chain as a {@link String}
	 */
	@Override
	public String getEnvironmentVariableKeysAsString() {
		return Arrays.toString(environmentVariables.keySet().toArray());
	}

	/**
	 * Getter for the environment variable key chain.
	 * 
	 * @return all keys for environment variables.
	 */
	@Override
	public Set<String> getEnvironmentVariableKeys() {
		return Collections.unmodifiableSet(environmentVariables.keySet());
	}

	/**
	 * Adds a new environment variable with the given key. Adding a new variable
	 * with an existing key will have no effect on the key chain - the new value
	 * will not replace the old.
	 * 
	 * @return true if the variable is added successfully, false otherwise
	 */
	@Override
	public boolean addEnvironmentVariable(String key, Object variable) {
		return this.environmentVariables.putIfAbsent(key, variable) == null;
	}

	@Override
	public Set<String> getFilterKeys() {
		return Collections.unmodifiableSet(filters.keySet());
	}

	@Override
	public String getFilterKeysAsString() {
		return Arrays.toString(environmentVariables.keySet().toArray());
	}

	@Override
	public boolean addFilter(String key, Filter filter) {
		return this.filters.putIfAbsent(key, filter) == null;
	}

	@Override
	public boolean filterExists(String key) {
		return filters.containsKey(key);
	}

	@Override
	public boolean environmentVariableExists(String key) {
		return environmentVariables.containsKey(key);
	}

	@Override
	public Set<Pair<String, Object>> filterActivePerception(String[] keys,
			SensingAction action) {
		// TODO optimise the recursive environment variables e.g. agents that
		// are in some space, or sub environment
		// System.out.println("FILTER: " + Arrays.toString(keys));
		HashSet<Pair<String, Object>> perceptions = new HashSet<>();

		for (String key : keys) {
			if (key == null)
				continue;
			String[] subkeys = key.split("\\."); // TODO optimise, we dont want
													// to do this twice!
			// TODO handle integer parameters
			Object result = environmentVariables.get(subkeys[0]);
			// System.out.println("SUB: " + Arrays.toString(subkeys));
			// System.out.println("DATA: " + result);
			for (int i = 1; i < subkeys.length; i++) {
				result = filters.get(subkeys[i]).get(action, result);
			}
			perceptions.add(new Pair<String, Object>(key, result));
			// perceptions.add(null);
		}
		System.out.println("PERCEPTIONS: " + perceptions);
		return perceptions;
	}

	/**
	 * Filters the actions provided by an {@link Actuator} or {@link Sensor}
	 * into groups depending on their type. Actions will be accessed later,
	 * during the executing of the actions by the {@link Physics}. This method
	 * will insert actions in order based on their priority. //TODO
	 */
	@Override
	public synchronized void filterAction(AbstractEnvironmentalAction action) {
		// TODO filter based on priority
		if (SensingAction.class.isAssignableFrom(action.getClass())) {
			this.addSenseAction((SensingAction) action);
		} else if (CommunicationAction.class
				.isAssignableFrom(action.getClass())) {
			this.addCommunicationAction((CommunicationAction<?>) action);
		} else if (PhysicalAction.class.isAssignableFrom(action.getClass())) {
			this.addPhysicalAction((PhysicalAction) action);
		} else {
			// TODO provide more types of actions!
			System.err.println("ERROR HANDLING ACTION TYPE "
					+ action.getClass());
			Thread.dumpStack();
		}
	}

	private class RandomEnvironmentVariable implements Filter {

		// private Random random = new Random();

		@Override
		public Object get(SensingAction action, Object... args) {
			// we dont need the action for this
			if (Collection.class.isAssignableFrom(args[0].getClass())) {
				Collection<?> col = (Collection<?>) args[0];
				Optional<?> o = col.stream()
						.skip((long) (Math.random() * col.size())).findAny();
				if (o != null) {
					return o.get();
				}
			}
			variablemissuse(this);
			return null;
		}
	}

	private class SelfFilter extends AppearanceFilter {
		@Override
		public Set<Appearance> get(SensingAction action, Object... args) {
			Set<Appearance> self = new HashSet<>();
			self.add(((ActiveBody) action.getActor()).getExternalAppearance());
			return self;
		}
	}

	public class AppearanceFilter implements Filter {

		@Override
		public Set<Appearance> get(SensingAction action, Object... args) {
			Set<Appearance> appearances;
			if (Collection.class.isAssignableFrom(args[0].getClass())) {
				Collection<?> col = (Collection<?>) args[0];
				// TODO
				System.out.println("TYPE: "
						+ Arrays.toString(col.getClass().getTypeParameters()));
				Thread.dumpStack();
				
				// if(ActiveBody.class.isAssignableFrom(col.getClass().getTypeParameters().))
				// {
				//
				// }
				// for(Object o : col) {
				// appearances.add(e)
				// }
			} else if (PhysicalBody.class.isAssignableFrom(args[0].getClass())) {
				appearances = new HashSet<>();
				appearances.add(((PhysicalBody) args[0])
						.getExternalAppearance());
				return appearances;
			}
			variablemissuse(this);
			return null;
		}
	}

	public interface Filter {
		public Object get(SensingAction action, Object... args);
	}

	private void variablemissuse(Filter fev) {
		System.err.println("MISSUSE OF FUNCTIONAL ENVIRONMENT VARIABLE " + fev);
	}
}
