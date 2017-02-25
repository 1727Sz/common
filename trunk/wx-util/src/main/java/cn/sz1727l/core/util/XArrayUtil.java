package cn.sz1727l.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.ArrayUtils;

/**
 * cn.sz1727l.core.util.XArrayUtil.java
 * 
 * Description: 提供并扩展{@link org.apache.commons.lang.ArrayUtils}功能
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XArrayUtil extends ArrayUtils {

	private XArrayUtil() {
	}

	/**
	 * 
	 * @param array
	 *            需转换的数组对象
	 * @param maxElements
	 *            需转换的最大元素数量， 若数组中元素数量大于<code>maxElements</code>， 超出的元素以"[..]"显示
	 * @return
	 */
	public static String toString(Object array, int maxElements) {
		String result;

		Class<?> componentType = array.getClass().getComponentType();
		if (Object.class.isAssignableFrom(componentType)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((Object[]) array, 0, maxElements)));
		} else if (componentType.equals(Boolean.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((boolean[]) array, 0, maxElements)));
		} else if (componentType.equals(Byte.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((byte[]) array, 0, maxElements)));
		} else if (componentType.equals(Character.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((char[]) array, 0, maxElements)));
		} else if (componentType.equals(Short.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((short[]) array, 0, maxElements)));
		} else if (componentType.equals(Integer.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((int[]) array, 0, maxElements)));
		} else if (componentType.equals(Long.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((long[]) array, 0, maxElements)));
		} else if (componentType.equals(Float.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((float[]) array, 0, maxElements)));
		} else if (componentType.equals(Double.TYPE)) {
			result = ArrayUtils.toString((ArrayUtils.subarray((double[]) array, 0, maxElements)));
		} else {
			throw new IllegalArgumentException("Unknown array service type: " + componentType.getName());
		}

		if (Array.getLength(array) > maxElements) {
			StringBuilder buf = new StringBuilder(result);
			buf.insert(buf.length() - 1, " [..]");
			result = buf.toString();
		}

		return result;
	}

	/**
	 * 用指定的<code>Class</code>创建一个给定数组的拷贝. 这个方法常用于，将一个实现了多种接口的类型的数组元素，拷贝一份指定
	 * <code>Class</code>的数组， <code>Class</code>是数组元素的父类型。 Sample: public
	 * interface Person{} public interface User{} public class Man implements
	 * Person, User{}
	 * 
	 * { Man[] men = new Man[]{new Man(), new Man()}; Person[] person = (Person)
	 * ArrayUtil.toArrayOfComponentType(men, Person.class); }
	 * 
	 * @param objects
	 * @param clazz
	 * @return
	 */
	public static Object[] toArrayOfComponentType(Object[] objects, Class<?> clazz) {
		if (objects == null || objects.getClass().getComponentType().equals(clazz)) {
			return objects;
		}

		if (clazz == null) {
			throw new IllegalArgumentException("Array target class must not be null");
		}

		Object[] result = (Object[]) Array.newInstance(clazz, objects.length);
		System.arraycopy(objects, 0, result, 0, objects.length);
		return result;
	}

	public static Object[] setDifference(Object[] a, Object[] b) {
		Collection<Object> aCollecn = new HashSet<Object>(Arrays.asList(a));
		Collection<Object> bCollecn = Arrays.asList(b);
		aCollecn.removeAll(bCollecn);
		return aCollecn.toArray();
	}
}
