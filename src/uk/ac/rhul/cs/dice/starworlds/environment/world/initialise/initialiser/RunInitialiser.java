package uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.initialiser;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.rhul.cs.dice.starworlds.environment.world.initialise.WorldNodeInitialiser;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeLocal;
import uk.ac.rhul.cs.dice.starworlds.environment.world.node.WorldNodeRemote;

public class RunInitialiser implements WorldNodeInitialiser {

	Collection<Thread> threads = new ArrayList<>();

	@Override
	public void initialise(WorldNodeRemote node) {
		// the remote environment will be run on the other machine!
	}

	@Override
	public void initialise(WorldNodeLocal node) {
		Thread t = new Thread(node.getEnvironment().getPhysics()
				.getSynchroniser());
		System.out.println("****Run: " + node);
		t.start();
	}

	public Collection<Thread> getThreads() {
		return threads;
	}
}
