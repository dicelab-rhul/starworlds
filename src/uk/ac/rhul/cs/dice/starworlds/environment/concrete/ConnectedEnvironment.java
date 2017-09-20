package uk.ac.rhul.cs.dice.starworlds.environment.concrete;

import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.actions.Action;
import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.AbstractAmbient;
import uk.ac.rhul.cs.dice.starworlds.environment.ambient.Ambient;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.AbstractConnectedPhysics;
import uk.ac.rhul.cs.dice.starworlds.environment.physics.Physics;

/**
 * 
 * 
 * Known direct subclasses: {@link ConnectedUniverse}.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public class ConnectedEnvironment extends AbstractConnectedEnvironment {

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
	public ConnectedEnvironment(
			AbstractAmbient ambient,
			AbstractConnectedPhysics physics,
			EnvironmentAppearance appearance,
			Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions,
			Boolean bounded) {
		super(ambient, physics, appearance, possibleActions, bounded);
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
	public ConnectedEnvironment(
			Integer port,
			AbstractAmbient ambient,
			AbstractConnectedPhysics physics,
			EnvironmentAppearance appearance,
			Collection<Class<? extends AbstractEnvironmentalAction>> possibleActions,
			Boolean bounded) {
		super(port, ambient, physics, appearance, possibleActions, bounded);
	}

	@Override
	public Boolean isSimple() {
		return false;
	}
}