package uk.ac.rhul.cs.dice.starworlds.environment.world;

import java.io.Serializable;

import uk.ac.rhul.cs.dice.starworlds.utils.Identifiable;

public class WorldAddress implements Identifiable, Serializable {

	private static final long serialVersionUID = -8513476253985063288L;

	private String name;
	private String address;
	private Integer port;

	public WorldAddress(String name, String address, Integer port) {
		super();
		if (name == null || name == "") {
			throw new IllegalArgumentException(
					"Name of world cannot be null or empty");
		}
		this.name = name;
		this.address = (address != null) ? address : "localhost";
		this.port = (port != null) ? port : 0;
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

	@Override
	public String getId() {
		return this.name;
	}

	@Override
	public void setId(String id) {
		// do nothing
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.name + "@"
				+ this.address + ":" + this.port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorldAddress other = (WorldAddress) obj;
		if (!name.equals(other.name))
			return false;
		if (!address.equals(other.address))
			return false;
		if (!port.equals(other.port))
			return false;
		return true;
	}
}
