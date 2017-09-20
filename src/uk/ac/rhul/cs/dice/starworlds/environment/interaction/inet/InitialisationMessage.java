package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.io.Serializable;

import uk.ac.rhul.cs.dice.starworlds.appearances.Appearance;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.Environment;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment.AmbientRelation;

/**
 * The class of {@link Event} that should be sent during initialisation of
 * connections between {@link Environment}s. This class holds the appearance of
 * the sending {@link Environment} and the {@link AmbientRelation} that
 * describes the relation between the two connected environments. TODO However
 * it may be sub classed to include additional initialisation information.
 * 
 * @author Benedict Wilkins
 */
public class InitialisationMessage extends INetAbstractMessage<Appearance>
		implements Serializable {
	private static final long serialVersionUID = 3308209205449776184L;

	// TODO refactor relation to be in the payload
	private AmbientRelation relation;

	public InitialisationMessage(EnvironmentAppearance appearance,
			AmbientRelation relation) {
		super(appearance);
		this.relation = relation;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " : " + this.getPayload()
				+ " : " + relation;
	}

	public AmbientRelation getRelation() {
		return relation;
	}
}