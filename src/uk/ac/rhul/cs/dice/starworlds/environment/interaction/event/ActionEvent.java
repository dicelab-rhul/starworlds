package uk.ac.rhul.cs.dice.starworlds.environment.interaction.event;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;

public class ActionEvent extends AbstractEvent {

	private static final long serialVersionUID = -8162223557555042616L;

	private AbstractEnvironmentalAction action;

	public ActionEvent(EnvironmentAppearance origin,
			AbstractEnvironmentalAction action) {
		super(origin);
		this.action = action;
	}

	@Override
	public EnvironmentAppearance getOrigin() {
		return (EnvironmentAppearance) super.getOrigin();
	}

	public AbstractEnvironmentalAction getAction() {
		return action;
	}
}
