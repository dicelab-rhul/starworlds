package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import java.util.ArrayList;
import java.util.List;

import uk.ac.rhul.cs.dice.starworlds.environment.interfaces.AbstractConnectedEnvironment;
import uk.ac.rhul.cs.dice.starworlds.environment.world.World;
import uk.ac.rhul.cs.dice.starworlds.environment.world.WorldDeployer;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdge;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.edge.WorldEdgeRemote;
import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldEdgeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNode;

/**
 * A {@link InitialisationVisitor} class that {@link Visitor#visit(Acceptor)
 * visits} {@link WorldNode}s and calls the
 * {@link AbstractConnectedEnvironment#initialiseEnvironmentConnections(java.util.Collection, java.util.Collection)}
 * method. See this method for details. This {@link InitialisationVisitor} is
 * used in the {@link WorldDeployer} to initialise the {@link World World}.
 * 
 * @author Ben Wilkins
 *
 */
public class ConnectionInitialiser implements WorldEdgeInitialiser {

	private List<Thread> waitingThreads = new ArrayList<>();

	public void waitForRemoteConnections() throws InterruptedException {
		if (!waitingThreads.isEmpty()) {
			System.out.println("WAITING FOR EXPECTED REMOTE CONNECTIONS...");
			for (Thread t : waitingThreads) {
				t.join();
			}
			System.out.println("...DONE");
		}
	}

	// @Override
	// public void visitWorldNode(WorldNode acceptor) {
	// acceptor.getValue().initialiseEnvironmentConnections(
	// Node.getValuesFrom(acceptor.getChildren()),
	// Node.getValuesFrom(acceptor.getNeighbours()));
	// if (acceptor.getValue().getConnectedEnvironmentManager()
	// .expectingRemoteConnections()) {
	// System.out.println("CREATING WAITING THREAD");
	// Thread t = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// EnvironmentConnectionManager m = acceptor.getValue()
	// .getConnectedEnvironmentManager();
	// System.out.println("WAITING...");
	// while (!m.reachedExpectedRemoteConnections())
	// ;
	// System.out.println("...DONE WAITING");
	// }
	// });
	// waitingThreads.add(t);
	// t.start();
	// }
	// }

	@Override
	public void initialise(WorldEdgeRemote node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialise(WorldEdgeLocal edge) {
		edge.getNode1()
				.getEnvironment()
				.getConnectedEnvironmentManager()
				.initialiseConnection(edge.getNode2().getEnvironment(),
						edge.getRelation());
		System.out.println("***Initialised Connection: " + edge);
	}
}
