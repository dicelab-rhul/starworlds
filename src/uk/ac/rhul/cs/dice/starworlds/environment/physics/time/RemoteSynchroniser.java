package uk.ac.rhul.cs.dice.starworlds.environment.physics.time;

import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.INetEnvironmentConnection;
import uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet.SynchronisationMessage;

public class RemoteSynchroniser implements Runnable {

	private INetEnvironmentConnection remoteEnvironment;
	private SyncPoint current;
	private volatile SyncPoint received;
	// TODO remove
	//private Logger logger;

	public RemoteSynchroniser(INetEnvironmentConnection remoteEnvironment) {
		this.remoteEnvironment = remoteEnvironment;
		// logger = Logger.getLogger(this.remoteEnvironment.getAppearance()
		// .getId());
		// try {
		// FileHandler fh = new FileHandler("logs/"
		// + this.remoteEnvironment.getAppearance().getId());
		// SimpleFormatter formatter = new SimpleFormatter();
		// fh.setFormatter(formatter);
		// logger.addHandler(fh);
		// logger.setUseParentHandlers(false);
		//
		// } catch (SecurityException | IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void run() {
		if (waitCondition()) {
			System.out.println(localid() + " waiting...");
			while (waitCondition()) {
			}
			System.out.println(localid() + " ...continue");
		}
	}

	public void done(SyncPoint reached) {
		System.out.println(localid() + " done:" + reached);
		remoteEnvironment.send(new SynchronisationMessage(reached));
	}

	public void receiveSyncMessage(SyncPoint point) {
		System.out.println(localid() + " received sync message: " + point);
		received = point;
	}

	private String localid() {
		return this.remoteEnvironment.getAppearance().getId();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public SyncPoint getCurrent() {
		return current;
	}

	public void setCurrent(SyncPoint current) {
		this.current = current;
	}

	public boolean waitCondition() {
		return !current.equals(received);
	}
}
