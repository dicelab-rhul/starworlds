package uk.ac.rhul.cs.dice.starworlds.actions.environmental;

import java.util.Arrays;

import uk.ac.rhul.cs.dice.starworlds.environment.physics.SensorSubscriber.SensiblePerception;
import uk.ac.rhul.cs.dice.starworlds.perception.CommunicationPerception;
import uk.ac.rhul.cs.dice.starworlds.perception.DefaultPerception;

/**
 * A subclass of {@link AbstractEnvironmentalAction} representing sensing
 * actions.<br/>
 * <br/>
 * 
 * Known direct subclasses: none.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public class SensingAction extends AbstractEnvironmentalAction {

	@SensiblePerception
	public static final Class<?> POSSIBLEPERCEPTION = DefaultPerception.class;

	private String[] keys;

	public SensingAction(String... keys) {
		this.keys = (keys != null) ? keys : new String[] {};
	}

	public String[] getKeys() {
		return keys;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + Arrays.toString(this.keys);
	}

	@Override
	public Object[] getCanSense() {
		return null;
	}
}