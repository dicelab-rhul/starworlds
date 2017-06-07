package uk.ac.rhul.cs.dice.starworlds.perception;

/**
 * An abstract implementation of {@link Perception}. All {@link Perception}s
 * should extend this class. </br> Implements: {@link Perception} </br> Known
 * subclasses: {@link DefaultPerception}, {@link CommunicationPerception}
 * 
 * @author Ben
 *
 * @param <T>
 *            the type of the content of this {@link Perception}.
 */
public abstract class AbstractPerception<T> implements Perception<T> {

	private T content;

	/**
	 * Constructor.
	 * 
	 * @param content
	 *            of the perception
	 */
	public AbstractPerception(T content) {
		this.content = content;
	}

	@Override
	public T getPerception() {
		return content;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + content;
	}

}
