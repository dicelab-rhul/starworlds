package uk.ac.rhul.cs.dice.starworlds.utils.datastructure.graph;

public class GEdge<N extends GNode> {

	protected N n1;
	protected N n2;

	public GEdge(N n1, N n2) {
		if (n1 == null || n2 == null) {
			throw new NullPointerException(this.getClass().getSimpleName()
					+ ": Node argument cannot be null");
		}
		this.n1 = n1;
		this.n2 = n2;
	}

	public N getNode1() {
		return n1;
	}

	public N getNode2() {
		return n2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((n1 == null) ? 0 : n1.getId().hashCode());
		result = prime * result + ((n2 == null) ? 0 : n2.getId().hashCode());
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
		@SuppressWarnings("rawtypes")
		GEdge other = (GEdge) obj;
		if (n1 == null) {
			if (other.getNode1() != null)
				return false;
		} else if (!n1.getId().equals(other.getNode1().getId()))
			return false;
		if (n2 == null) {
			if (other.getNode2() != null)
				return false;
		} else if (!n2.getId().equals(other.getNode2().getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "<" + n1 + "," + n2 + ">";
	}

}
