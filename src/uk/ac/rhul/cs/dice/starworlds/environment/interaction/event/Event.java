package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import java.io.Serializable;

import uk.ac.rhul.cs.dice.starworlds.utils.Identifiable;

public interface Event extends Serializable, Identifiable {

	public Object getOrigin();

}
