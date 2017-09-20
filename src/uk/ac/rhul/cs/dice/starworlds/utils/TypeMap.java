package uk.ac.rhul.cs.dice.starworlds.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeMap<K, V> {

	private static final long serialVersionUID = -6783640562679645422L;

	private Class<K> basetype;
	private HashMap<Class<?>, Collection<V>> map;
	// a mapping of classes to their subsclasses
	private HashMap<Class<?>, Collection<Class<?>>> superclassmap;
	private HashMap<Class<?>, Collection<Class<?>>> subclassmap;

	public TypeMap(Class<K> basetype) {
		if (basetype.isAnnotation() || basetype.isAnonymousClass()
				|| basetype.isPrimitive() || basetype.isSynthetic()) {
			throw new IllegalArgumentException("The base type of a "
					+ this.getClass().getSimpleName()
					+ " must be an interface, class or abstract class");
		}
		this.basetype = basetype;
		map = new HashMap<>();
		map.put(basetype, new HashSet<>());
		subclassmap = new HashMap<>();
		subclassmap.put(basetype, new HashSet<>());
		superclassmap = new HashMap<>();
		superclassmap.put(basetype, new HashSet<>());
	}

	public void add(Class<? extends K> key, V value) {
		Collection<V> values = new HashSet<>();
		values.add(value);
		add(key, values);
	}

	public void add(Class<? extends K> key, Collection<V> values) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<>());
		}
		updateClassMap(key);
		Collection<V> keyvalues = new HashSet<>();
		keyvalues.addAll(values);
		map.get(key).addAll(keyvalues);
		
		// add all values from super classes to sub classes
		subclassmap.get(key).forEach(c -> map.get(c).addAll(keyvalues));
		superclassmap.get(key).forEach(c -> map.get(key).addAll(map.get(c)));
		//System.out.println(classMapToString("SUPER:", superclassmap));
		//System.out.println(classMapToString("SUB:", subclassmap));
		//System.out.println(this);
	}

	/**
	 * Updates the sub and super class maps when a new key is added, if it does
	 * not already exist.
	 * 
	 * @param key
	 *            : to add
	 */
	private void updateClassMap(Class<? extends K> key) {
		if (!superclassmap.containsKey(key)) {
			Collection<Class<?>> supermap = new HashSet<>();
			subclassmap.put(key, new HashSet<>());
			superclassmap.forEach((c, v) -> {
				if (key.isAssignableFrom(c)) {
					// key is a super class of c
					v.add(key);
					subclassmap.get(key).add(c);
				} else if (c.isAssignableFrom(key)) {
					// c is a super class of key
					supermap.add(c);
					subclassmap.get(c).add(key);
				}
			});
			superclassmap.put(key, supermap);
		}
	}

	public void remove(Class<?> key) {
		// TODO

	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public Collection<V> get(Class<?> key) {
		return map.get(key);
	}

	public void clear() {
		this.map.clear();
		this.superclassmap.clear();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Class<?>> keySet() {
		return map.keySet();
	}

	public int size() {
		return map.size();
	}

	private String classMapToString(String name, Map<Class<?>, ?> map) {
		StringBuilder builder = new StringBuilder(
				(name == null || "".equals(name)) ? "" : name
						+ System.lineSeparator());
		map.forEach((c, o) -> builder.append(c.getSimpleName() + ":" + o
				+ System.lineSeparator()));
		return builder.toString();
	}

	@Override
	public String toString() {
		return classMapToString(null, map);
	}
}
