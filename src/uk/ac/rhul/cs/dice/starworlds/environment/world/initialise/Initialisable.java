package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise;

public interface Initialisable<T extends Initialiser<?>> {

	public void initialise(T initialiser);

}
