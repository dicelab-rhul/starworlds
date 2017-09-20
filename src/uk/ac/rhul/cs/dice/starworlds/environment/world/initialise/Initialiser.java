package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise;

public interface Initialiser<T extends Initialisable<?>> {

	public void initialise(T initialisable);
}
