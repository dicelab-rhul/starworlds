package uk.ac.rhul.cs.dice.gawl.interfaces.entities;

import java.util.List;

import uk.ac.rhul.cs.dice.gawl.interfaces.appearances.DependentBodyAppearance;
import uk.ac.rhul.cs.dice.gawl.interfaces.entities.agents.Actuator;
import uk.ac.rhul.cs.dice.gawl.interfaces.entities.agents.Sensor;

/**
 * A subclass of {@link ActiveBody} which implements {@link ObjectInterface}.<br/><br/>
 * 
 * Known direct subclasses: none.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public abstract class DependentBody<T1 extends Enum<?>, T2 extends Enum<?>> extends ActiveBody<T1, T2> implements ObjectInterface {

	/**
	 * Constructor with a {@link DependentBodyAppearance}, a {@link List} of {@link Sensor} instances and
	 * one of {@link Actuator} instances.
	 * 
	 * @param appearance : the {@link DependentBodyAppearance} of the {@link DependentBody}.
	 * @param sensors : a {@link List} of {@link Sensor} instances.
	 * @param actuators : a {@link List} of {@link Actuator} instances.
	 */
	public DependentBody(DependentBodyAppearance appearance, List<Sensor<T1>> sensors, List<Actuator<T2>> actuators) {
		super(appearance, sensors, actuators);
	}
}