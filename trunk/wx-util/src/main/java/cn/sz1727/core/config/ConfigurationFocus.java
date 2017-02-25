package cn.sz1727.core.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * cn.sz1727.core.config.ConfigurationFocus.java
 * 
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ConfigurationFocus {

	public static final String HOSTNAME = "hostName";
	public static final String SERVERNAME = "serverName";
	public static final String RUNNABLENAME = "runnableName";

	private static final ConfigurationManager CONFIGURATION = ConfigurationManager.getRef();

	private Focus focus;

	/**
	 * Create a configuration focus for the hostName, serverName and
	 * runnableName system properties, defaulting to '*' if not specified.
	 */
	public ConfigurationFocus() {
		this.focus = new Focus(CONFIGURATION.getString(null, HOSTNAME, "*"), CONFIGURATION.getString(null, SERVERNAME, "*"), CONFIGURATION.getString(null, RUNNABLENAME, "*"));
	}

	/**
	 * Construct a new focus to the specified level. If used with *.*.* notated
	 * properties the full focus, including the host, server, runnable prefix
	 * must be used. E.g., in a property file where "*.*.*.a.b.c.d.x=value" and
	 * "*.*.*.*.a.b.c.d.y=value" to create a focus to the "x" property a
	 * construcion is required of new ConfigurationFocus("*.*.*.a.b.c.d"). This
	 * constructor also allows a focus to be created that does not involve the
	 * host/server/runnable prefix level.
	 *
	 * @param focus
	 *            focus level.
	 */
	public ConfigurationFocus(String focus) {
		this.focus = new Focus(focus);
	}

	/**
	 * Create a configuration focus for the specified host, server and runnable
	 * level.
	 *
	 * @param host
	 * @param server
	 * @param runnable
	 */
	public ConfigurationFocus(String host, String server, String runnable) {
		this.focus = new Focus(CONFIGURATION.getString(null, host), CONFIGURATION.getString(null, server), CONFIGURATION.getString(null, runnable));
	}

	/**
	 * Zoom in to the specified property focus level. In an example property
	 * file are the values:<br>
	 * a.b.c.d.x=value<br>
	 * a.b.c.d.y=value<br>
	 * Normally accessing the values you would call getString("a.b.c.d.x"), etc.
	 * However, calling zoomIn("a.b.c.d") allows the properties to be retrieved
	 * with getString("x"), etc. Subsequent calls to zoomIn append to the
	 * current focus, e.g. zoomIn("a.b.c") followed by zoomIn("d") would allow
	 * getString("x").
	 *
	 * @param zoom
	 *            level to zoom to.
	 */
	public void zoomIn(String zoom) {
		this.focus.zoomIn(zoom);
	}

	/**
	 * Zoom out one level. For a property of "system.debug.file=abc.txt" current
	 * zoom in levels of "system" and "system.debug" will return true when the
	 * call moves the level to the root and "system' levels respectively.
	 * Calling zoomOut when the focus is at the root level will return false.
	 *
	 * @return Boolean indicating if zoom out has suceeded
	 */
	public boolean zoomOut() {
		return this.focus.zoomOut();
	}

	public String getFocus() {
		return focus.getPath();
	}

	/*********************************************************************************
	 * Wrapper methods which delegate to the ConfigurationManager *
	 *********************************************************************************/

	/**
	 * Retrieve a property value for the given property name
	 *
	 * @return String containing property
	 */
	public String getString(String property) {
		return CONFIGURATION.getString(focus, property);
	}

	/**
	 * Retrieve a String or return the supplied defaultValue if no property is
	 * found.
	 *
	 * @return String of property value or default.
	 */
	public String getString(String property, String defaultValue) {
		return CONFIGURATION.getString(focus, property, defaultValue);
	}

	/**
	 * Retrieve a string array of property values from a property containing
	 * comma delimited values.
	 *
	 * @return String array of property values.
	 */
	public String[] getStringList(String property) {
		return CONFIGURATION.getStringList(focus, property);
	}

	/**
	 * Retrieve a string list of trimmed property values from a property
	 * containing comma delimited values.
	 *
	 * @return String list of trimmed property values.
	 */
	public List<String> getStringListWithTrimmedValues(String property) {
		List<String> res = null;
		String[] items = CONFIGURATION.getStringList(focus, property);
		if (items != null) {
			res = new LinkedList<String>();
			for (String item : items) {
				res.add(item.trim());
			}
		}
		return res;
	}

	/**
	 * Retrieve a string array of property values from a property containing
	 * comma delimited values. If property is not found, the supplied
	 * defaultValue array is returned.
	 *
	 * @return String array of property values or default.
	 */
	public String[] getStringList(String property, String[] defaultValue) {
		return CONFIGURATION.getStringList(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property value for the given property name
	 *
	 * @return Value of the property
	 */
	public int getInt(String property) {
		return CONFIGURATION.getInt(focus, property);
	}

	/**
	 * Retrieve an int value or return the supplied defaultValue if no property
	 * is found.
	 *
	 * @return Value of property or default.
	 */
	public int getInt(String property, int defaultValue) {
		return CONFIGURATION.getInt(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property value for the given property name
	 *
	 * @return Value of the property
	 */
	public long getLong(String property) {
		return CONFIGURATION.getLong(focus, property);
	}

	/**
	 * Retrieve an int value or return the supplied defaultValue if no property
	 * is found.
	 *
	 * @return Value of property or default.
	 */
	public long getLong(String property, long defaultValue) {
		return CONFIGURATION.getLong(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property value for the given property name
	 *
	 * @return Value of the property
	 */
	public short getShort(String property) {
		return CONFIGURATION.getShort(focus, property);
	}

	/**
	 * Retrieve a property and return as a short or return the defaultValue if
	 * no property is found.
	 *
	 * @return Value of property or default.
	 */
	public short getShort(String property, short defaultValue) {
		return CONFIGURATION.getShort(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property and return as a float or return the defaultValue if
	 * no property is found.
	 *
	 * @return Value of property or default.
	 */
	public float getFloat(String property, float defaultValue) {
		return CONFIGURATION.getFloat(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property and return as a double.
	 *
	 * @return Value of property or default.
	 */
	public double getDouble(String property) {
		return CONFIGURATION.getDouble(focus, property);
	}

	/**
	 * Retrieve a property and return as a double or return the defaultValue if
	 * no property is found.
	 *
	 * @return Value of property or default.
	 */
	public double getDouble(String property, double defaultValue) {
		return CONFIGURATION.getDouble(focus, property, defaultValue);
	}

	/**
	 * Retrieve a property value for the given property name
	 *
	 * @return Value of the property
	 */
	public boolean getBoolean(String property) {
		return CONFIGURATION.getBoolean(focus, property);
	}

	/**
	 * Retrieve a property and return as a boolean or return the defaultValue if
	 * no property is found.
	 *
	 * @return Value of property or default.
	 */
	public boolean getBoolean(String property, boolean defaultValue) {
		return CONFIGURATION.getBoolean(focus, property, defaultValue);
	}

	/**
	 * Retrieve a boolean property comparing with case insensitivity the
	 * retrieved value against the true and false matches. If no property is
	 * found the default value is returned. For a property of "debugEnabled=yes"
	 * the following examples all return a boolean value of 'true':<br>
	 * getBoolean("debugEnabled", "YES", "NO", false);<br>
	 * getBoolean("debugEnabled", "oui", "non", true);<br>
	 *
	 * @return Boolean of the comparisson.
	 */
	public boolean getBoolean(String property, String trueValue, String falseValue, boolean defaultValue) {
		return CONFIGURATION.getBoolean(focus, property, trueValue, falseValue, defaultValue);
	}

	/**
	 * Retrieve a boolean property comparing, and ignoring case, the retrieved
	 * value against the true and false matches. E.g. for a property
	 * "debugEnabled=yes" then 'true' would be the return from the call
	 * getBoolean("debugEnabled", "Yes", "No");
	 *
	 * @return Boolean of the comparisson.
	 */
	public boolean getBoolean(String property, String trueValue, String falseValue) {
		return CONFIGURATION.getBoolean(focus, property, trueValue, falseValue);
	}

	/*********************************************************************************
	 * Set property values *
	 *********************************************************************************/

	/**
	 * Set property at current focus level with value.
	 */
	public void setString(String property, String value) {
		CONFIGURATION.setString(focus, property, value);
	}

	/**
	 * Set property at current focus level with value.
	 */
	public void setInt(String property, int value) {
		CONFIGURATION.setInt(focus, property, value);
	}

	/**
	 * Set property at current focus level with value.
	 */
	public void setShort(String property, short value) {
		CONFIGURATION.setShort(focus, property, value);
	}

	/**
	 * Set property at current focus level with value.
	 */
	public void setFloat(String property, float value) {
		CONFIGURATION.setFloat(focus, property, value);
	}

	/**
	 * Set property at current focus level with value.
	 */
	public void setDouble(String property, double value) {
		CONFIGURATION.setDouble(focus, property, value);
	}

	/**
	 * Return an enumeration of the sub-paths. Given the example properties:
	 * *.*.*.a.b.w=value<br>
	 * *.*.*.a.b.c.x=value<br>
	 * *.*.*.a.d.y=value<br>
	 * *.*.*.a.e.z=value<br>
	 * A call to iterateSubPaths("a") will return a three string enumeration
	 * "b", "d" and "e". The call enumerateSubPaths("a.b") will return the
	 * enumeration of just the string "c" (Ie, a path, not a parameter node).
	 */
	public Iterator<String> iterateSubPaths(String path) {
		return CONFIGURATION.iterateSubPaths(focus, path);
	}

	/**
	 * Enumerate through parameters at a specified focus zoom level. Given the
	 * example properties:<br>
	 * *.*.*.a.b.c.x=value<br>
	 * *.*.*.a.b.c.y=value<br>
	 * *.*.*.a.b.c.z=value<br>
	 * For a default focus, calling <i>iterateParameters("a.b.c")</i> will
	 * return an enumeration of the strings "x", "y" and "z". Likewise, calling
	 * <i>zoomIn("a.b.c")</i> followed by <i>iterateParameters("")</i> will
	 * return the same enumeration.
	 */
	public Iterator<String> iterateParameters(String path) {
		return CONFIGURATION.iterateParameters(focus, path);
	}
}
