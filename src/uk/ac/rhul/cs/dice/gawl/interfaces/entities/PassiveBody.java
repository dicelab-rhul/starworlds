package uk.ac.rhul.cs.dice.gawl.interfaces.entities;

import uk.ac.rhul.cs.dice.gawl.interfaces.appearances.Appearance;

/**
 * A subclass of {@link PhysicalBody} which implements {@link ObjectInterface}.<br/>
 * <br/>
 * 
 * Known direct subclasses: none.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public abstract class PassiveBody extends PhysicalBody {

	/**
	 * Constructor with a {@link PassiveBodyAppearance}.
	 * 
	 * @param externalAppearance
	 *            : the {@link PassiveBodyAppearance}.
	 */
	public PassiveBody(Appearance appearance) {
		super(appearance);
	}
}