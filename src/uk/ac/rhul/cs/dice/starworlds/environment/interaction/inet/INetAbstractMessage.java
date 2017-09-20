package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.io.Serializable;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;

public abstract class INetAbstractMessage<T extends Serializable> implements
		Event {

	private static final long serialVersionUID = -7398687670848928068L;

	// this must be here for serialisation to work properly
	private T payload;

	public INetAbstractMessage() {
	}

	public INetAbstractMessage(T payload) {
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " : " + this.getPayload();
	}
}
