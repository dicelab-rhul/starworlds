package uk.ac.rhul.cs.dice.starworlds.environment.interfaces;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.EnvironmentConnector;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.LocalEnvironmentConnection;

public enum EnvironmentRelation {

	SUPER() {
		@Override
		public EnvironmentRelation inverse() {
			return SUB;
		}

		@Override
		public void addConnection(EnvironmentConnector connector,
				LocalEnvironmentConnection localConnection) {
			connector.addSubEnvironment(localConnection);
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
				LocalEnvironmentConnection localConnection) {
			connector.setSuperEnvironment(localConnection);
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
				LocalEnvironmentConnection localConnection) {
			connector.addNeighbourEnvironment(localConnection);
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
			LocalEnvironmentConnection localConnection);
}
