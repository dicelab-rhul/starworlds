package uk.ac.rhul.cs.dice.gawl.interfaces.appearances;

/**
 * The interface for appearances.<br/><br/>
 * 
 * Known implementations: {@link AbstractAppearance}, {@link SimpleEnvironmentAppearance}.
 * 
 * @author cloudstrife9999 a.k.a. Emanuele Uliana
 * @author Ben Wilkins
 * @author Kostas Stathis
 *
 */
public interface Appearance {
	/**
	 * Returns a {@link String} representation of the appearance.
	 * 
	 * @return a {@link String} representation of the appearance.
	 */
	public abstract String represent();
	
	/**
	 * Returns the {@link String} name of the object the appearance refers to.
	 * 
	 * @return the {@link String} name of the object the appearance refers to.
	 */
	public abstract String getName();
	
	/**
	 * Sets the {@link String} name of the object the appearance refers to.
	 * 
	 * @param name : the {@link String} name of the object the appearance refers to.
	 */
	public abstract void setName(String name);
}