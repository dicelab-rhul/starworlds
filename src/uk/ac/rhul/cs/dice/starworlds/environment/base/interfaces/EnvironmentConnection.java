package uk.ac.rhul.cs.dice.starworlds.environment.base.interfaces;

import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.base.AbstractMessage;

public interface EnvironmentConnection {

	public void send(AbstractMessage<?> obj);

	public void receive(AbstractMessage<?> obj);

	public boolean isOpen();

	public void close();

	public void open();

	public EnvironmentConnection getMutualConnector();

	/**
	 * Getter for the {@link Appearance} of the local {@link Environment}.
	 * 
	 * @return the local {@link EnvironmentAppearance}.
	 */
	public EnvironmentAppearance getAppearance();

	/**
	 * Getter for the {@link Appearance} of the remote {@link Environment}.
	 * (That is, the {@link Appearance} of the {@link EnvironmentConnection}
	 * that this {@link EnvironmentConnection} is connected to.
	 * 
	 * @return
	 */
	public EnvironmentAppearance getRemoteAppearance();

}
