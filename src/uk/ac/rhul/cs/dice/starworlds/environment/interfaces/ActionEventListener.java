package uk.ac.rhul.cs.dice.starworlds.environment.interfaces;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import uk.ac.rhul.cs.dice.starworlds.actions.environmental.AbstractEnvironmentalAction;
import uk.ac.rhul.cs.dice.starworlds.appearances.EnvironmentAppearance;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.ActionEvent;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.Event;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventHandler;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.event.EventListener;

public class ActionEventListener implements EventListener {

	// all of the actions received after action propagation
	private Map<String, ActionEvent> events;
	private Collection<AbstractEnvironmentalAction> actions;

	public ActionEventListener() {
		actions = new HashSet<>();
	}

	@Override
	public void update(Object origin, Event event) {
		ActionEvent e = (ActionEvent) event;
		System.out.println("Received action: " + e.getAction() + " from: "
				+ e.getOrigin());
		actions.add(e.getAction());
		events.put(e.getAction().getId(), e);
	}

	public EnvironmentAppearance getSender(AbstractEnvironmentalAction action) {
		return events.get(action.getId()).getOrigin();
	}

	public void clearEvents() {
		events.clear();
		actions.clear();
	}

	public Collection<AbstractEnvironmentalAction> getActions() {
		return actions;
	}
}