package uk.ac.rhul.cs.dice.starworlds.environment.world;

public class WorldAddress {

	private String name;
	private String address;
	private Integer port;

	public WorldAddress(String name, String address, Integer port) {
		super();
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public Integer getPort() {
		return port;
	}

}
