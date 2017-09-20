package uk.ac.rhul.cs.dice.starworlds.environment.interaction;

import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;

public interface EnvironmentConnection {

	public void send(Event event);

	public void receive(Event event);

	public boolean isOpen();

	public void close();

	public void open();

	public boolean isConnected();

	public EnvironmentConnection getRemoteConnection();

	/**
	 * Getter for the {@link Appearance} of the local {@link Environment}.
	 * 
	 * @return the local {@link EnvironmentAppearance}.
	 */
	public Appearance getAppearance();

	/**
	 * Getter for the {@link Appearance} of the remote {@link Environment}.
	 * (That is, the {@link Appearance} of the {@link EnvironmentConnection}
	 * that this {@link EnvironmentConnection} is connected to.
	 * 
	 * @return
	 */
	public Appearance getRemoteAppearance();

}
