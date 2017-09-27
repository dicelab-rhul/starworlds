package uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class INetObjectSender implements INetSender {

	protected ObjectOutputStream out;

	public INetObjectSender() {
	}

	public INetObjectSender(Socket socket) {
		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void send(Object message) {
		try {
			//System.out.println("send: " + message);
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setOutputStream(OutputStream out) {
		try {
			this.out = new ObjectOutputStream(out);
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
