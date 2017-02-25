package cn.sz1727l.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

/**
 * cn.sz1727l.core.util.XMapUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XMapUtil extends MapUtils {

	private XMapUtil() {
	}

	/**
	 * Convenience method for CollectionUtil#mapWithKeysAndValues(Class,
	 * Iterator, Iterator); keys and values can be null or empty.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> mapWithKeysAndValues(Class<? extends Map> mapClass, K[] keys, V[] values) {
		Collection<K> keyCollection = (Collection<K>) (keys != null ? Arrays.asList(keys) : Collections.emptyList());
		Collection<V> valuesCollection = (Collection<V>) (values != null ? Arrays.asList(values) : Collections.emptyList());
		return mapWithKeysAndValues(mapClass, keyCollection.iterator(), valuesCollection.iterator());
	}

	/**
	 * Convenience method for CollectionUtil#mapWithKeysAndValues(Class,
	 * Iterator, Iterator); keys and values can be null or empty.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> mapWithKeysAndValues(Class<? extends Map> mapClass, Collection<K> keys, Collection<V> values) {
		keys = (Collection<K>) (keys != null ? keys : Collections.emptyList());
		values = (Collection<V>) (values != null ? values : Collections.emptyList());
		return mapWithKeysAndValues(mapClass, keys.iterator(), values.iterator());
	}

	/**
	 * Create & populate a Map of arbitrary class. Populating stops when either
	 * the keys or values iterator is null or exhausted.
	 * 
	 * @param mapClass
	 *            the Class of the Map to instantiate
	 * @param keys
	 *            iterator for Objects ued as keys
	 * @param values
	 *            iterator for Objects used as values
	 * @return the instantiated Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> mapWithKeysAndValues(Class<? extends Map> mapClass, Iterator<K> keys, Iterator<V> values) {
		Map<K, V> m = null;

		if (mapClass == null) {
			throw new IllegalArgumentException("Map class must not be null!");
		}

		try {
			m = mapClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		if (keys != null && values != null) {
			while (keys.hasNext() && values.hasNext()) {
				m.put(keys.next(), values.next());
			}
		}

		return m;
	}

	/**
	 * Creates a String representation of the given Map, with optional newlines
	 * between elements.
	 * 
	 * @param props
	 *            the map to format
	 * @param newline
	 *            indicates whether elements are to be split across lines
	 * @return the formatted String
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map props, boolean newline) {
		if (props == null || props.isEmpty()) {
			return "{}";
		}

		StringBuilder buf = new StringBuilder(props.size() * 32);
		buf.append('{');

		if (newline) {
			buf.append(XSystemUtil.LINE_SEPARATOR);
		}

		Object[] entries = props.entrySet().toArray();
		int i;

		for (i = 0; i < entries.length - 1; i++) {
			Map.Entry property = (Map.Entry) entries[i];
			buf.append(property.getKey());
			buf.append('=');
			buf.append(XPropertiesUtil.maskedPropertyValue(property));

			if (newline) {
				buf.append(XSystemUtil.LINE_SEPARATOR);
			} else {
				buf.append(',').append(' ');
			}
		}

		// don't forget the last one
		Map.Entry lastProperty = (Map.Entry) entries[i];
		buf.append(lastProperty.getKey().toString());
		buf.append('=');
		buf.append(XPropertiesUtil.maskedPropertyValue(lastProperty));

		if (newline) {
			buf.append(XSystemUtil.LINE_SEPARATOR);
		}

		buf.append('}');
		return buf.toString();
	}
}
