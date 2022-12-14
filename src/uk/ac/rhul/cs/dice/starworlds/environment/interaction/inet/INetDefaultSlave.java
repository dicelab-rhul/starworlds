package uk.ac.rhul.cs.dice.starworlds.environment.interaction.inet;

import java.io.IOException;
import java.net.Socket;

import uk.ac.rhul.cs.dice.starworlds.utils.inet.INetSlave;

public class INetDefaultSlave extends INetSlave {

	public INetDefaultSlave(Socket socket)
			throws IOException {
		super(socket, new INetDefaultSender(socket), new INetDefaultReceiver(
				socket));
	}
}
