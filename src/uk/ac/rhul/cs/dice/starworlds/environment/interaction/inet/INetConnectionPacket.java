package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.io.Serializable;

import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.EnvironmentRelation;

public class INetConnectionPacket implements Serializable {

	private static final long serialVersionUID = -570578364717153856L;
	public EnvironmentAppearance appearance;
	public EnvironmentRelation relation;

	public INetConnectionPacket(EnvironmentAppearance appearance,
			EnvironmentRelation relation) {
		super();
		this.appearance = appearance;
		this.relation = relation;
	}
}
