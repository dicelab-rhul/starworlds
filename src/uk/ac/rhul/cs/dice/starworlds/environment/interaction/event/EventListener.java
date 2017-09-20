package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

public interface EventListener {

	public abstract void update(Object origin, Event event);

}
