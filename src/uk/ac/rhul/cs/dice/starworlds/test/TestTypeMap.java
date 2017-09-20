package uk.ac.rhul.cs.dice.starworlds.test;

import java.util.ArrayList;

import uk.ac.rhul.cs.dice.starworlds.utils.Identifiable;
import uk.ac.rhul.cs.dice.starworlds.utils.TypeMap;

public class TestTypeMap {

	public static void main(String[] args) {
		TypeMap<BaseInterface, TestValue> map = new TypeMap<>(
				BaseInterface.class);

		ArrayList<TestValue> list = new ArrayList<>();
		list.add(new TestValue("a"));
		list.add(new TestValue("b"));
		map.add(Class1.class, list);
		map.add(AbstractClass.class, new TestValue("c"));
		map.add(AbstractClass.class, new TestValue("d"));
		map.add(Class12.class, new TestValue("d"));
		map.add(Class2.class, new TestValue("f"));
		map.add(Class11.class, new TestValue("g"));

		// map.remove(Class1.class);
		// System.out.println(System.lineSeparator() + map);
	}

	public static void add(TypeMap<AbstractClass, TestValue> map,
			Class<? extends AbstractClass> c) {
		map.add(c, new TestValue(c.getSimpleName()));
	}

	private static interface BaseInterface {

	}

	private static class AbstractClass implements BaseInterface {
		@Override
		public String toString() {
			return this.getClass().getSimpleName();
		}
	}

	private static class Class1 extends AbstractClass {
	}

	private static class Class11 extends Class1 {
	}

	private static class Class2 extends AbstractClass {
	}

	private static class Class12 extends Class1 {
	}

	private static class TestValue implements Identifiable {

		private String id;

		public TestValue(String id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public void setId(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Value:" + id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
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
			TestValue other = (TestValue) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
}
