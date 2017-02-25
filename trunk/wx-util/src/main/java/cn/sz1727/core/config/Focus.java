package cn.sz1727.core.config;

import java.util.ArrayList;
import java.util.List;

import cn.sz1727l.core.util.XCollectionUtil;

/**
 * Encapsulates the focus for configuration viewing through
 * {@link ConfigurationFocus}
 * 
 * cn.sz1727.core.config.Focus.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class Focus {

	/**
	 * Elements of the focus
	 */
	private List<String> elements;

	/**
	 * Creates a new focus with the given elements
	 * 
	 * @param focus
	 */
	public Focus(String... focus) {
		if (focus != null) {
			this.elements = new ArrayList<String>(3);
			for (String f : focus) {
				elements.add(f);
			}
		}
	}

	/**
	 * @return the number of elements in this focus
	 */
	public int getNumElements() {
		return (this.elements == null ? 0 : this.elements.size());
	}

	/**
	 * @return the dot separated path of this focus or empty string if there are
	 *         no elements in the focus
	 */
	public String getPath() {
		if (XCollectionUtil.isEmpty(elements)) {
			return ("");
		}
		final StringBuilder b = new StringBuilder();
		for (String s : this.elements) {
			b.append(s).append(".");
		}
		return (b.toString());
	}

	/**
	 * Zoom in on this focus
	 * 
	 * @param zoom
	 */
	public void zoomIn(String zoom) {
		this.elements.add(zoom);
	}

	/**
	 * Zoom out of this focus
	 * 
	 * @return true if the zoom out was successful
	 */
	public boolean zoomOut() {
		return !XCollectionUtil.isEmpty(elements) ? this.elements.remove(this.elements.size() - 1) != null : false;
	}

	@Override
	public String toString() {
		return (this.getPath());
	}
}
