package uk.ac.rhul.cs.dice.starworlds.environment.inet;

import java.net.Socket;

import uk.ac.rhul.cs.dice.starworlds.utils.inet.sendreceive.INetObjectReceiver;

public class INetDefaultReceiver extends INetObjectReceiver {

	public INetDefaultReceiver(Socket socket, Class<?> expectedinclass) {
		super(socket, expectedinclass);
	}

}
