package uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph;

public class InvalidGraphConfigurationException extends RuntimeException {

	public static String EDGECONSTRUCTERROR = "An edge cannot be connected to a null node";

	private static final long serialVersionUID = -2519402375950524944L;

	public InvalidGraphConfigurationException(String message) {
		super(message);
	}

}
