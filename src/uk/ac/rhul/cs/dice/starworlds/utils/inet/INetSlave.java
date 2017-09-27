package uk.ac.rhul.cs.dice.starworlds.utils.inet;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Observable;

import uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive.INetReceiver;
import uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive.INetSender;

public abstract class INetSlave extends Observable implements Runnable {

	private Socket socket;
	private INetSender sender;
	private INetReceiver receiver;
	private volatile boolean waiting = false;
	private Object waitReceive = null;

	public INetSlave(String host, int port, INetSender sender,
			INetReceiver receiver) {
		try {
			Socket socket = new Socket(host, port);
			sender.setOutputStream(socket.getOutputStream());
			receiver.setInputStream(socket.getInputStream());
			init(socket, receiver, sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public INetSlave(Socket socket, INetSender sender, INetReceiver receiver) {
		init(socket, receiver, sender);
	}

	private void init(Socket socket, INetReceiver receiver, INetSender sender) {
		this.socket = socket;
		this.receiver = receiver;
		this.sender = sender;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public SocketAddress getRemoteSocketAddress() {
		return this.socket.getRemoteSocketAddress();
	}

	@Override
	public void run() {
		//System.out.println(this.socket + " LISTENING...");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Object received = receiver.receive();
					// System.out.println("RECEIVED: " + received);
					if (!waiting) {
						if(received != null) {
							setChanged();
							notifyObservers(received);
						}
					} else {
						waitReceive = received;
						waiting = false;
					}
				}
			}
		});
		thread.start();
	}

	/**
	 * Sends the given messages and blocks until a reply is given. A default
	 * time out of approximately 10 seconds is used.
	 * 
	 * @param message
	 *            : to send
	 * @return reply received
	 */
	public Object sendAndWaitForReply(Object message) {
		return sendAndWaitForReply(message, 10000L);
	}

	/**
	 * Sends the given messages and blocks until a reply is given or until the
	 * given timeout.
	 * 
	 * @param message
	 *            : to send
	 * @param timeout
	 *            : time to wait for reply
	 * @return
	 */
	public Object sendAndWaitForReply(Object message, Long timeout) {
		waiting = true;
		this.send(message);
		Long o = System.currentTimeMillis();
		Long c = System.currentTimeMillis();
		while (c - o < timeout) {
			c = System.currentTimeMillis();
			if (!waiting) {
				return waitReceive;
			}
		}
		System.out.println("Timeout: " + timeout
				+ " while waiting for reply from: "
				+ this.socket.getRemoteSocketAddress());
		return null;
	}

	public void send(Object message) {
		// System.out.println("SENDING: " + message);
		sender.send(message);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.socket.toString();
	}
}
