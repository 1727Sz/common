package cn.sz1727.core.tools.filter;

/**
 * cn.sz1727.core.tools.filter.Filter.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public interface Filter<T> {

	/**
	 * Check a given object against this filter.
	 * 
	 * @param obj
	 *            a non null object to filter.
	 * @return <code>true</code> if the object matches the filter
	 */
	boolean accept(T t);
}
