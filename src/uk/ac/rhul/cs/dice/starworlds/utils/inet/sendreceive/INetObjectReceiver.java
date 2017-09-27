package uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class INetObjectReceiver implements INetReceiver {

	protected ObjectInputStream in;

	public INetObjectReceiver(Socket socket) {
		try {
			INetObjectReceiver.this.in = new ObjectInputStream(
					socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized Object receive() {
		try {
			return this.in.readObject();
		} catch (ClassNotFoundException | IOException | NullPointerException e) {
			
			System.err.println(this + " RECEIVED INVALID DATA");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	@Override
	public void setInputStream(InputStream in) {
		try {
			this.in = new ObjectInputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
