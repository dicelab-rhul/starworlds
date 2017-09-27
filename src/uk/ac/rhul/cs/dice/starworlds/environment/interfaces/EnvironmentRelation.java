package uk.ac.rhul.cs.dice.starworlds.environment.interfaces;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.AbstractEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.EnvironmentConnector;

public enum EnvironmentRelation {

	SUPER() {
		@Override
		public EnvironmentRelation inverse() {
			return SUB;
		}

		@Override
		public void addConnection(EnvironmentConnector connector,
				AbstractEnvironmentConnection connection) {
			connector.addSubEnvironment(connection);
		}

		@Override
		public String toString() {
			return SUPERSTRING;
		}
	},
	SUB() {
		@Override
		public EnvironmentRelation inverse() {
			return SUPER;
		}

		@Override
		public void addConnection(EnvironmentConnector connector,
				AbstractEnvironmentConnection connection) {
			connector.setSuperEnvironment(connection);
		}

		@Override
		public String toString() {
			return SUBSTRING;
		}
	},
	NEIGHBOUR() {
		@Override
		public EnvironmentRelation inverse() {
			return NEIGHBOUR;
		}

		@Override
		public void addConnection(EnvironmentConnector connector,
				AbstractEnvironmentConnection connection) {
			connector.addNeighbourEnvironment(connection);
		}

		@Override
		public String toString() {
			return NEIGHBOURSTRING;
		}
	};

	public static final String NEIGHBOURSTRING = "-";
	public static final String SUPERSTRING = ">";
	public static final String SUBSTRING = "<";

	public abstract String toString();

	public abstract EnvironmentRelation inverse();

	public abstract void addConnection(EnvironmentConnector connector,
			AbstractEnvironmentConnection connection);
}
