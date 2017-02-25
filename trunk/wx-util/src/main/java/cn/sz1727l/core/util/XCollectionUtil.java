package cn.sz1727l.core.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * cn.sz1727l.core.util.XCollectionUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public final class XCollectionUtil extends CollectionUtils {

	private XCollectionUtil() {
	}

	/**
	 * Creates an array of the given Collection's elements, but with the given
	 * <code>Class</code> as element type. Useful for arrays of objects that
	 * implement multiple interfaces and a "typed view" onto these objects is
	 * required.
	 * 
	 * @param objects
	 *            a Collection of objects
	 * @param clazz
	 *            the desired service type of the new array
	 * @return <code>null</code> when objects is <code>null</code>, or a new
	 *         array containing the elements of the source array which is typed
	 *         to the given <code>clazz</code> parameter.
	 * @throws IllegalArgumentException
	 *             if the <code>clazz</code> argument is <code>null</code>.
	 * @throws ArrayStoreException
	 *             if the elements in <code>objects</code> cannot be cast to
	 *             <code>clazz</code>.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T[] toArrayOfComponentType(Collection objects, Class<T> clazz) {
		if (objects == null) {
			return null;
		}

		if (clazz == null) {
			throw new IllegalArgumentException("Array target class must not be null");
		}

		if (objects.isEmpty()) {
			return (T[]) Array.newInstance(clazz, 0);
		}

		int i = 0, size = objects.size();
		T[] result = (T[]) Array.newInstance(clazz, size);
		Iterator iter = objects.iterator();

		while (i < size && iter.hasNext()) {
			result[i++] = (T) iter.next();
		}

		return result;
	}

	/**
	 * Creates a String representation of the given Collection, with optional
	 * newlines between elements. Class objects are represented by their full
	 * names.
	 * 
	 * @param c
	 *            the Collection to format
	 * @param newline
	 *            indicates whether elements are to be split across lines
	 * @return the formatted String
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Collection c, boolean newline) {
		if (c == null || c.isEmpty()) {
			return "[]";
		}

		return toString(c, c.size(), newline);
	}

	/**
	 * Calls {@link #toString(Collection, int, boolean)} with <code>false</code>
	 * for newline.
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Collection c, int maxElements) {
		return toString(c, maxElements, false);
	}

	/**
	 * Creates a String representation of the given Collection, with optional
	 * newlines between elements. Class objects are represented by their full
	 * names. Considers at most <code>maxElements</code> values; overflow is
	 * indicated by an appended "[..]" ellipsis.
	 * 
	 * @param c
	 *            the Collection to format
	 * @param maxElements
	 *            the maximum number of elements to take into account
	 * @param newline
	 *            indicates whether elements are to be split across lines
	 * @return the formatted String
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Collection c, int maxElements, boolean newline) {
		if (c == null || c.isEmpty()) {
			return "[]";
		}

		int origNumElements = c.size();
		int numElements = Math.min(origNumElements, maxElements);
		boolean tooManyElements = (origNumElements > maxElements);

		StringBuilder buf = new StringBuilder(numElements * 32);
		buf.append('[');

		if (newline) {
			buf.append(XSystemUtil.LINE_SEPARATOR);
		}

		Iterator items = c.iterator();
		for (int i = 0; i < numElements - 1; i++) {
			Object item = items.next();

			if (item instanceof Class) {
				buf.append(((Class) item).getName());
			} else {
				buf.append(item);
			}

			if (newline) {
				buf.append(XSystemUtil.LINE_SEPARATOR);
			} else {
				buf.append(',').append(' ');
			}
		}

		// don't forget the last one
		Object lastItem = items.next();
		if (lastItem instanceof Class) {
			buf.append(((Class) lastItem).getName());
		} else {
			buf.append(lastItem);
		}

		if (newline) {
			buf.append(XSystemUtil.LINE_SEPARATOR);
		}

		if (tooManyElements) {
			buf.append(" [..]");
		}

		buf.append(']');
		return buf.toString();
	}

	/**
	 * Some code uses null to indicate "unset", which makes appending items
	 * complex.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List addCreate(List list, Object value) {
		if (null == list) {
			return singletonList(value);
		} else {
			list.add(value);
			return list;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List singletonList(Object value) {
		List list = new LinkedList();
		list.add(value);
		return list;
	}

	public static boolean containsType(Collection<?> collection, final Class<?> type) {
		if (type == null) {
			return false;
		}
		return exists(collection, new Predicate() {
			public boolean evaluate(Object object) {
				return object != null && type.isAssignableFrom(object.getClass());
			}
		});
	}

	public static void removeType(Collection<?> collection, final Class<?> type) {
		if (type == null) {
			return;
		}
		filter(collection, new Predicate() {
			public boolean evaluate(Object object) {
				return object != null && type.isAssignableFrom(object.getClass());
			}
		});
	}
	
	public static void removeNull(Collection<?> collection) {
		filter(collection, new Predicate() {
			public boolean evaluate(Object object) {
				return object != null;
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map){
		boolean flag = true;
		if (map == null) {
			return flag;
		}
		if(!map.isEmpty() && map.size() > 0){
			flag = false;
		}
		return flag;
	}
}