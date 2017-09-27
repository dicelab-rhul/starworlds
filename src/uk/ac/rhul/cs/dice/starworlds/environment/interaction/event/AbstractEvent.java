package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import uk.ac.rhul.cs.dice.starworlds.initialisation.IDFactory;
import uk.ac.rhul.cs.dice.starworlds.utils.Identifiable;

public abstract class AbstractEvent implements Event {

	private static final IDFactory IDFACTORY = IDFactory.getInstance();
	private static final long serialVersionUID = -6385222899824701869L;

	private String id;
	private Object origin;
	
	public AbstractEvent() {
	}

	public AbstractEvent(Identifiable origin) {
		this.origin = origin;
		this.id = origin.getId() + ":" + IDFACTORY.getNewID();
	}

	@Override
	public Object getOrigin() {
		return origin;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + this.id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
	}
}
