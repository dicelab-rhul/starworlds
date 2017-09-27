package uk.ac.rhul.cs.dice.starworlds.utils;

import java.util.Collection;
import java.util.Map;

public class CollectionUtilities {

	public static boolean containsAllKeys(Collection<?> keys, Map<?, ?> map) {
		for (Object key : keys) {
			if (!map.containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsAll(Collection<?> values,
			Collection<?> collection) {
		for (Object value : values) {
			if (!collection.contains(value)) {
				return false;
			}
		}
		return true;
	}
}
