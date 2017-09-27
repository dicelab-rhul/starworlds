package uk.ac.rhul.cs.dice.starworlds.utils.inet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import uk.ac.rhul.cs.dice.starworlds.utils.Pair;

public abstract class INetServer extends Observable implements Observer {

	protected ServerSocket socket;
	protected Map<SocketAddress, INetSlave> slaves = new HashMap<>();
	protected List<Thread> slavethreads = new ArrayList<>();

	public INetServer(int port) {
		init(port);
	}

	private void init(int port) {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("SERVER: " + socket + " Waiting for connections...");
		Thread socketthread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = INetServer.this.socket.accept();
						//System.out.println("SERVER ACCEPTED: " + socket);
						INetSlave slave = newSlave(socket);
						initialiseSlave(slave);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		socketthread.start();
	}

	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers(new Pair<SocketAddress, Object>(((INetSlave) o)
				.getSocket().getRemoteSocketAddress(), arg));
	}

	protected void initialiseSlave(INetSlave slave) {
		slave.addObserver(this);
		this.slaves.put(slave.getSocket().getRemoteSocketAddress(), slave);
		Thread slaveThread = new Thread(slave);
		this.slavethreads.add(slaveThread);
		slaveThread.start();
	}

	public abstract INetSlave newSlave(Socket socket);

	public Set<SocketAddress> getClientAddresses() {
		return Collections.unmodifiableSet(slaves.keySet());
	}

	public INetSlave getSlave(SocketAddress addr) {
		return this.slaves.get(addr);
	}

	public void send(SocketAddress addr, Object message) {
		this.slaves.get(addr).send(message);
	}

	public SocketAddress connect(String host, Integer port)
			throws UnknownHostException, IOException {
		System.out.println(this + " INITIATING COMMUNICATION WITH: " + host
				+ ":" + port);
		Socket socket = new Socket(host, port);
		INetSlave slave = newSlave(socket);
		initialiseSlave(slave);
		System.out.println(this + " CONNECTION SUCCESSFUL: " + slave);
		return slave.getSocket().getRemoteSocketAddress();
	}

	@Override
	public String toString() {
		return socket.toString();
	}

	public Integer getLocalPort() {
		return socket.getLocalPort();
	}
}
