package uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class INetObjectSender implements INetSender {

	protected ObjectOutputStream out;

	public INetObjectSender(Socket socket) {
		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(Object message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
