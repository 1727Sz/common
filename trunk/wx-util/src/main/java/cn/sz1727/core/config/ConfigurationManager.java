package cn.sz1727.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.exception.MallRuntimeException;
import cn.sz1727.core.exception.UncheckedException;
import cn.sz1727.core.i18n.CoreErrorMessages;
import cn.sz1727l.core.util.XIOUtil;
import cn.sz1727l.core.util.XStringUtil;

/**
 * cn.sz1727.core.config.ConfigurationManager.java
 * 
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ConfigurationManager {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

	public static final String ROOT_CONFIG_NAME = "ocp.config";

	private static ConfigurationManager singleton;

	/**
	 * The path/parameter delimiter
	 */
	private static String SEPARATOR = ".";

	/**
	 * The root node. All other nodes are held in some child under this node.
	 */
	private ConfigurationTreeNode rootNode;

	/**
	 * The token start delimiter for property references
	 */
	private static String TOKEN_START = "${";

	/**
	 * The token end delimiter for property references
	 */
	private static String TOKEN_END = "}";

	static {
		singleton = new ConfigurationManager();
		ConfigurationManager.singleton.reload();
	}

	/**
	 * Private constructor
	 */
	private ConfigurationManager() {
		this.reload();
	}

	static public ConfigurationManager getRef() {
		return singleton;
	}

	public ConfigurationTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * Loads this configuration manager with properties
	 */
	public synchronized void reload() {

		ConfigurationTreeNode newRootNode = new ConfigurationTreeNode("root");

		String list = System.getProperty(ROOT_CONFIG_NAME);

		PropertyLoader loader = new PropertyLoader();

		if (list != null) {
			// See if the resources are configured as system properties
			String[] resources = splitStringList(list);
			for (String resource : resources) {
				LOG.debug("loading resource [{}]", resource);
				loader.load(resource);
			}
		} else {
			BufferedReader br = null;
			try {
				// Load the resources from classpath
				InputStream in = ConfigurationManager.class.getClassLoader().getResourceAsStream(ROOT_CONFIG_NAME);

				if (in != null) {
					br = new BufferedReader(new InputStreamReader(in));
					String resource = null;
					for (resource = br.readLine(); resource != null; resource = br.readLine()) {
						resource = resource.trim();
						if (XStringUtil.isNotBlank(resource)) {
							LOG.debug("loading resource [{}]", resource);
							loader.load(resource);
						}
					}
				}
			} catch (IOException e) {
				LOG.error("Cannot load ocp.properties", e);
				throw new MallRuntimeException(CoreErrorMessages.create().loadOCPResourceFailed(), e);
			} finally {
				if (br != null) {
					XIOUtil.closeQuietly(br);
				}
			}

		}

		// Load all properties
		loadProperties(newRootNode, loader.getProperties());

		ConfigurationTreeNode oldRootNode = rootNode;
		rootNode = newRootNode;
		// Check if mandatory properties are set
		boolean errorFound = false;
		try {
			list = getString(null, "ocp.config.mandatory", null);

			if (list != null) {
				loader = new PropertyLoader(false);

				String[] resources = splitStringList(list);
				for (String resource : resources) {
					LOG.debug("loading resource [{}]", resource);
					loader.load(resource);
				}

				Properties man = loader.getProperties();

				Focus focus = new Focus(getString(null, ConfigurationFocus.HOSTNAME, "*"), getString(null, ConfigurationFocus.SERVERNAME, "*"), getString(null, ConfigurationFocus.RUNNABLENAME, "*"));

				for (Enumeration<?> e = man.propertyNames(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					if (getString(focus, key, null) == null) {
						LOG.error("Mandatory property [" + focus + key + "] not set. " + man.getProperty(key));
						errorFound = true;
					}
				}
				if (errorFound) {
					throw new MallRuntimeException(CoreErrorMessages.create().mandatoryPropsOfResource());
				}
			}
		} finally {
			if (errorFound) {
				// restore old configuration
				rootNode = oldRootNode;
			}
		}
	}

	/**
	 * Allows properties to be loaded. These will be loaded on top of whatever
	 * properties are already loaded.
	 *
	 * @param properties
	 *            a standard java properities object.
	 * @see java.util.Properties
	 */
	private static void loadProperties(ConfigurationTreeNode newRootNode, Properties properties) {
		final Enumeration<?> list = properties.propertyNames();
		while (list.hasMoreElements()) {
			final String propertyName = (String) list.nextElement();
			// Let the parent class do all the hard work of actually generating
			// the tree nodes and setting the parameter/value pair.
			setString(null, propertyName, properties.getProperty(propertyName), newRootNode);
		}
	}

	/**
	 * Returns the data held against the parameter as a string. If it is
	 * possible that a parameter does not exist, rather than catch the
	 * UncheckedException, it would be better to use the alternative signature
	 * getString ("parameterName", null) and then check whether a null is
	 * returned. The returned string will have all tokens of the form %...%
	 * replaced with the result of looking up the contained token within the
	 * current hierarchy.
	 *
	 * @param focus
	 *            the focus of where to look in the property tree.
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter as a string.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 */
	public String getString(Focus focus, String propertyName) {
		final String retVal = getString(focus, propertyName, null);
		if (retVal == null) {
			throw new UncheckedException(CoreErrorMessages.create().propNotFoundOfResource(propertyName, focus));
		}
		return retVal;
	}

	/**
	 * Returns the data held against the parameter as a string, with
	 * defaultValue being returned if the parameter was not found. The returned
	 * string will have all tokens of the form %...% replaced with the result of
	 * looking up the contained token within the current hierarchy.
	 *
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as a string.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 */
	public String getString(Focus focus, String propertyName, String defaultValue) {
		return expandString(focus, propertyName, getStringInternal(focus, propertyName, defaultValue));
	}

	/**
	 * Returns the data held against the parameter as a string array. The data
	 * held must be comma seperated.
	 *
	 * @param propertyName
	 *            the wanted property.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 * @see ConfigurationManager#getString(Focus, String)
	 */
	public String[] getStringList(Focus focus, String propertyName) {
		String[] retVal;
		retVal = splitStringList(getString(focus, propertyName));
		return retVal;
	}

	/**
	 * Returns the data held against the parameter on the path as a string
	 * array, with defaultValue being returned if the parameter was found. The
	 * data held must be comma seperated.
	 *
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if parameter could not be
	 *            located.
	 * @return The data held against the parameter on the path as a string
	 *         array.
	 */
	public String[] getStringList(Focus focus, String propertyName, String[] defaultValue) {
		String[] retVal = defaultValue;
		String str = getString(focus, propertyName, null);
		if (str != null) {
			retVal = splitStringList(str);
		} else if (defaultValue != null) {
			for (int i = 0; i < defaultValue.length; i++) {
				defaultValue[i] = expandString(focus, propertyName, defaultValue[i]);
			}
		}
		return retVal;
	}

	/**
	 * Returns the data held against the parameter on the path as a string. The
	 * entire stored hierarchy will be searched for matching paths (in
	 * descreasing order of "exactness") until a path is found containing the
	 * parameter. The returned string will have all tokens of the form %...%
	 * replaced with the result of looking up the contained token within the
	 * current hierarchy.
	 *
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as a string.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 * @see ConfigurationManager#findMatchingNode
	 */
	public String getExpandedString(Focus focus, String propertyName) {
		return getString(focus, propertyName);
	}

	/**
	 * Returns the data held against the parameter on the path as a short, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as a short.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             short.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 */
	public short getShort(Focus focus, String propertyName, short defaultValue) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			return defaultValue;
		} else {
			return Short.parseShort(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a short.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as a short.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             short.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 */
	public short getShort(Focus focus, String propertyName) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			throw new UncheckedException(CoreErrorMessages.create().propNotFoundOfResource(propertyName, focus));
		} else {
			return Short.parseShort(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as an int, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as an int.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into an
	 *             int.
	 */
	public int getInt(Focus focus, String propertyName, int defaultValue) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as an int.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as an int.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into an
	 *             int.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter
	 */
	public int getInt(Focus focus, String propertyName) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			throw new UncheckedException(CoreErrorMessages.create().propNotFoundOfResource(propertyName, focus));
		} else {
			return Integer.parseInt(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a boolean,
	 * with defaultValue being returned if the parameter was not stored on the
	 * path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as a boolean.
	 * @throws UncheckedException
	 *             if no data could be found for the given parameter or
	 *             parameter value is not 'true' or 'false'.
	 */
	public boolean getBoolean(Focus focus, String propertyName) {
		return getBoolean(focus, propertyName, "true", "false");
	}

	/**
	 * Returns the data held against the parameter on the path as a boolean,
	 * with defaultValue being returned if the parameter was not found.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the parameter could not be
	 *            located.
	 * @return The data held against the parameter on the path as a boolean.
	 */
	public boolean getBoolean(Focus focus, String propertyName, boolean defaultValue) {
		return getBoolean(focus, propertyName, "true", "false", defaultValue);
	}

	/**
	 * Returns the data held against the parameter on the path as a boolean set
	 * to true if the parameter value matches, whilst ignoring case, the
	 * trueValue. False is returned if the falseValue is matched. The
	 * defaultValue is returned if the parameter was not found.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param trueValue
	 *            String for a case ignoring match against to get true returned
	 *            by the method.
	 * @param falseValue
	 *            String for a case ignoring match against to get true returned
	 *            by the method.
	 * @param defaultValue
	 *            the default value to be used if the parameter could not be
	 *            located.
	 * @return The data held against the parameter on the path as a boolean.
	 * @throws UncheckedException
	 *             if the parameter could be found the parameter value was not
	 *             matched.
	 */
	public boolean getBoolean(Focus focus, String propertyName, String trueValue, String falseValue, boolean defaultValue) {
		String sValue = getString(focus, propertyName, null);
		// if the property wasn't provided, return the default value
		if (XStringUtil.isBlank(sValue)) {
			return defaultValue;
		}
		sValue = sValue.trim();
		if (sValue.equalsIgnoreCase(trueValue)) {
			return true;
		}
		if (sValue.equalsIgnoreCase(falseValue)) {
			return false;
		}

		throw new UncheckedException(CoreErrorMessages.create().nonReadablePropErrorOfResource(propertyName));
	}

	/**
	 * Returns the data held against the parameter on the path as a boolean set
	 * to true if the parameter value matches, whilst ignoring case, the
	 * trueValue. False is returned if the falseValue is matched. The
	 * defaultValue is returned if the parameter was not found or the parameter
	 * did not match either the trueValue or falseValue.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param trueValue
	 *            String for a case ignoring match against to get true returned
	 *            by the method.
	 * @param falseValue
	 *            String for a case ignoring match against to get true returned
	 *            by the method.
	 * @return The data held against the parameter on the path as a boolean.
	 * @throws UncheckedException
	 *             if the parameter could not be found or the parameter value
	 *             could not be matched.
	 */
	public boolean getBoolean(Focus focus, String propertyName, String trueValue, String falseValue) {
		String sValue = getString(focus, propertyName).trim();
		if (sValue.equalsIgnoreCase(trueValue)) {
			return true;
		}

		if (sValue.equalsIgnoreCase(falseValue)) {
			return false;
		}

		LOG.error("non-readable boolean value for property {}", propertyName);
		throw new UncheckedException(CoreErrorMessages.create().nonReadablePropErrorOfResource(propertyName));
	}

	/**
	 * Returns the data held against the parameter on the path as a long, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as a long.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             long.
	 */
	public long getLong(Focus focus, String propertyName) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			throw new UncheckedException(CoreErrorMessages.create().propNotFoundOfResource(propertyName, focus));
		} else {
			return Long.parseLong(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a long, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as a long.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             long.
	 */
	public long getLong(Focus focus, String propertyName, long defaultValue) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			return defaultValue;
		} else {
			return Long.parseLong(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a float, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as a float.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             float.
	 */
	public float getFloat(Focus focus, String propertyName, float defaultValue) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			return defaultValue;
		} else {
			return Float.valueOf(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a double
	 *
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if the path/parameter could not
	 *            be located.
	 * @return The data held against the parameter on the path as a double.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             double.
	 */
	public double getDouble(Focus focus, String propertyName, double defaultValue) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			return defaultValue;
		} else {
			return Double.valueOf(str.trim());
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a double, with
	 * defaultValue being returned if the parameter was not stored on the path.
	 *
	 * @param propertyName
	 *            the wanted configuration parameter.
	 * @return The data held against the parameter on the path as a double.
	 * @throws NumberFormatException
	 *             if the parameter could be found but not translated into a
	 *             double.
	 */
	public double getDouble(Focus focus, String propertyName) {
		String str = getString(focus, propertyName, null);
		if (str == null) {
			throw new UncheckedException(CoreErrorMessages.create().propNotFoundOfResource(propertyName, focus));
		} else {
			return Double.valueOf(str.trim());
		}
	}

	/**
	 * Returns an Enumeration of strings listing the names of any sub-paths for
	 * the given path. Note that the default paths will also be returned (as
	 * "*") so that users of the configuration can determine what responses will
	 * be generated for any requested path.
	 *
	 * @param path
	 *            the path to be examined.
	 * @return An Enumeration of strings listing the names of any sub-paths for
	 *         the given path.
	 */
	public Iterator<String> iterateSubPaths(Focus focus, String path) {
		final Map<String, String> results = new HashMap<String, String>();
		traverseMatchingNodes(rootNode, getFullPath(focus, path), new KeyEnumerator(results));
		return results.keySet().iterator();
	}

	/**
	 * Returns an Enumeration of strings listing the names of any parameters
	 * held on the given path.
	 *
	 * @param path
	 *            the path to be examined.
	 * @return An Enumeration of strings listing the names of any parameters
	 *         held on the given path.
	 */
	public Iterator<String> iterateParameters(Focus focus, String path) {
		final Map<String, String> results = new HashMap<String, String>();
		traverseMatchingNodes(rootNode, getFullPath(focus, path), new DataEnumerator(results));
		return results.keySet().iterator();
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setShort(Focus focus, String propertyName, short value) {
		setString(focus, propertyName, Short.toString(value));
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setInt(Focus focus, String propertyName, int value) {
		setString(focus, propertyName, Integer.toString(value));
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setLong(Focus focus, String propertyName, long value) {
		setString(focus, propertyName, Long.toString(value));
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setFloat(Focus focus, String propertyName, float value) {
		setString(focus, propertyName, Float.toString(value));
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setDouble(Focus focus, String propertyName, double value) {
		setString(focus, propertyName, Double.toString(value));
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param focus
	 *            focus
	 * @param propertyName
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	public void setString(Focus focus, String propertyName, String value) {
		setString(focus, getParameter(propertyName), getPath(propertyName), value, getRootNode());
	}

	private static void setString(Focus focus, String propertyName, String value, ConfigurationTreeNode newRootNode) {
		setString(focus, getParameter(propertyName), getPath(propertyName), value, newRootNode);
	}

	/**
	 * Sets the value of a parameter on the path given. This method will create
	 * any new nodes required by the path, and will move any listeners that
	 * registered an interest in a path that is matched better by the new path
	 * than by the path on which they were held previously to the new node. All
	 * listeners registered against the node which contains the updated
	 * parameter will be informed of the addition/change.
	 *
	 * @param path
	 *            the path to the configuration parameter.
	 * @param parameter
	 *            the configuration parameter to be set.
	 * @param value
	 *            the value to be stored for the parameter.
	 */
	private static void setString(Focus focus, String parameter, String path, String value, ConfigurationTreeNode newRootNode) {

		final String fullPath = getFullPath(focus, path);

		ConfigurationTreeNode exactNode = findExactNode(newRootNode, fullPath, parameter);

		if (exactNode == null) {
			// Create the (new) path to the given parameter
			exactNode = findCreateExactNode(newRootNode, fullPath);
			if (exactNode == null) {
				// Unknown failure setting parameter in path
				throw new UncheckedException(CoreErrorMessages.create().unknowFailureSettingsForProps(fullPath));
			}
		}

		// Set the value for the given parameter so that listeners currently
		// held against "matchingNode" can potentially match against "exactNode"
		exactNode.setData(parameter, value);
	}

	/**
	 * @return String representation of the configuration.
	 */

	public String toString() {
		final StringBuilder output = new StringBuilder();
		for (ConfigurationTreeNode child : rootNode.getChildren()) {
			output.append(child);
		}
		return output.toString();
	}

	/**
	 * Finds the tree node whose route matches exactly the path given, and which
	 * contains the given parameter.
	 *
	 * @param startNode
	 *            the point from which to start searching.
	 * @param path
	 *            the path to follow from the starting point.
	 * @return The tree node whose route matches exactly the path given.
	 */
	protected static ConfigurationTreeNode findExactNode(ConfigurationTreeNode startNode, String path, String parameter) {
		// Arrived at the final destination? It's only valid if it holds the
		// requested parameter (if the parameter is not null)
		if (path == null || path.length() == 0) {
			if ((parameter == null) || (startNode.getData(parameter) != null)) {
				return startNode;
			} else {
				return null;
			}
		}

		ConfigurationTreeNode nextNode;
		ConfigurationTreeNode returnNode = null;

		StringTokenizer pathList = new StringTokenizer(path, SEPARATOR);
		String name = pathList.nextToken();
		String remainder = (pathList.hasMoreTokens()) ? path.substring(name.length() + 1) : null;

		nextNode = startNode.findChild(name);
		if (nextNode != null) {
			returnNode = findExactNode(nextNode, remainder, parameter);
		}

		return returnNode;
	}

	/**
	 * Finds the tree node whose route matches exactly the path given. If, at
	 * any point, no suitable sub-path can be found then new nodes are created
	 * so as to be able to satisfy the request.
	 *
	 * @param startNode
	 *            the point from which to start searching.
	 * @param path
	 *            the path to follow from the starting point.
	 * @return The tree node whose route matches exactly the path given.
	 */
	private static ConfigurationTreeNode findCreateExactNode(ConfigurationTreeNode startNode, String path) {
		// Arrived at the final destination?
		if (path == null || path.length() == 0) {
			return startNode;
		}

		ConfigurationTreeNode nextNode;
		StringTokenizer pathList = new StringTokenizer(path, SEPARATOR);
		String name = pathList.nextToken();
		String remainder = (pathList.hasMoreTokens()) ? path.substring(name.length() + 1) : null;

		nextNode = startNode.findChild(name);
		if (nextNode == null) {
			nextNode = new ConfigurationTreeNode(name);
			startNode.addChild(nextNode);
		}

		return findCreateExactNode(nextNode, remainder);
	}

	protected ConfigurationTreeNode findMatchingNode(ConfigurationTreeNode startNode, Focus focus, String propertyName) {
		return findMatchingNode(startNode, getPath(getFullPath(focus, propertyName)), getParameter(propertyName));
	}

	private static String getPath(String propertyName) {
		int split = propertyName.lastIndexOf('.');
		if (split != -1) {
			return propertyName.substring(0, split);
		} else {
			return null;
		}
	}

	private static String getParameter(String propertyName) {
		int split = propertyName.lastIndexOf('.');
		if (split != -1) {
			return propertyName.substring(split + 1);
		} else {
			return propertyName;
		}
	}

	/**
	 * Returns the data held against the parameter on the path as a string, with
	 * defaultValue being returned if the parameter was found.
	 *
	 * @param focus
	 *            focus
	 * @param propertyPath
	 *            the wanted configuration parameter.
	 * @param defaultValue
	 *            the default value to be used if parameter could not be
	 *            located.
	 * @return The data held against the parameter on the path as a string.
	 * @see ConfigurationManager#getString(Focus, String)
	 */
	private String getStringInternal(Focus focus, String propertyPath, String defaultValue) {
		ConfigurationTreeNode node = findMatchingNode(rootNode, focus, propertyPath);
		if (node != null) {
			return node.getData(getParameter(propertyPath));
		} else {
			return defaultValue;
		}
	}

	/**
	 * Finds the tree node whose route matches most closely the path given. If,
	 * at any point, no sub-path can be found of the correct name then the
	 * default sub-path (held as "*") is checked.
	 * <p>
	 * <p/>
	 * i.e. for the hierarchy:
	 * <p>
	 * <ul>
	 * <code>root ----- * ----- * ---- EventMessageReporter</code> (1)<br>
	 * <code>&nbsp&nbsp&nbsp&nbsp&nbsp|---- Spice - * ---- EventMessageReporter</code>
	 * (2)<br>
	 * <code>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp|------ BOM -- EventMessageReporter</code>
	 * (3)<br>
	 * </ul>
	 * <p>
	 * A request for <code>Spice.ASM.EventMessageReporter</code> translates into
	 * the same node (2) as <code>Spice.UTIL.EventMessageReporter</code>.
	 * Whereas <code>Sugar.BOM.EventMessageReporter</code> translates to node
	 * (1), and <code>Spice.BOM.EventMEssageReporter</code> to node (3).
	 *
	 * @param startNode
	 *            the point from which to start searching.
	 * @param path
	 *            the path to follow from the starting point.
	 * @param parameter
	 *            the parameter to be found
	 * @return The tree node whose route best matches the path and contains the
	 *         parameter - or if parameter is null, the route that best matches
	 *         the path.
	 */
	protected ConfigurationTreeNode findMatchingNode(ConfigurationTreeNode startNode, String path, String parameter) {
		if (path == null || path.length() == 0) {
			// Arrived at the final destination? It's only valid if it holds the
			// requested parameter (if the parameter is not null)
			if ((parameter == null) || (startNode.getData(parameter) != null)) {
				return startNode;
			} else {
				return null;
			}
		}

		ConfigurationTreeNode returnNode = null;

		StringTokenizer pathList = new StringTokenizer(path, SEPARATOR);
		String name = pathList.nextToken();
		String remainder = (pathList.hasMoreTokens()) ? path.substring(name.length() + 1) : null;

		ConfigurationTreeNode specificNode = startNode.findChild(name);
		ConfigurationTreeNode defaultNode = startNode.findChild("*");

		if (specificNode != null) {
			// Try to find the path/parameter, propogating exceptions only if
			// there is no default path to try

			returnNode = findMatchingNode(specificNode, remainder, parameter);

			if (returnNode == null && defaultNode == null) {
				return null;
			}
		}

		if ((returnNode == null) && (defaultNode != null)) {
			// Try to find the path/parameter, propogating exceptions always
			returnNode = findMatchingNode(defaultNode, remainder, parameter);
		}

		// Potential InvalidConfigurationPath iff no matching sub-node could be
		// found but the path has not been followed to the end
		if ((specificNode == null) && (defaultNode == null) && (path.length() > 0)) {
			return null;
		}

		return returnNode;
	}

	/**
	 * Visits all tree nodes whose route matches the path given. At each
	 * destination node, the method "operate" is invoked on the given Traversor.
	 *
	 * @param startNode
	 *            the point from which to start searching.
	 * @param path
	 *            the path to follow from the starting point.
	 * @param traversor
	 *            the object that "knows" how to operate on each matching
	 *            destination
	 */
	protected void traverseMatchingNodes(ConfigurationTreeNode startNode, String path, Traversor traversor) {
		if (path == null || path.length() == 0) {
			traversor.operate(startNode);
		} else {
			ConfigurationTreeNode nextNode;

			StringTokenizer pathList = new StringTokenizer(path, SEPARATOR);
			String name = pathList.nextToken();
			String remainder = (pathList.hasMoreTokens()) ? path.substring(name.length() + 1) : null;

			nextNode = startNode.findChild(name);

			if (nextNode != null) {
				traverseMatchingNodes(nextNode, remainder, traversor);
			}

			nextNode = startNode.findChild("*");

			if (nextNode != null) {
				traverseMatchingNodes(nextNode, remainder, traversor);
			}
		}
	}

	/**
	 * Replaces all occurrences of TOKEN-delimited tokens in the input string
	 * with the value held within the current configuration manager instance.
	 *
	 * @param focus
	 *            focus
	 * @param propertyPath
	 *            the "default" path to be searched when expanding.
	 * @param input
	 *            the string to be expanded.
	 * @return The input string with all TOKEN-delimited tokens replaced by
	 *         their value within the current hierarchy.
	 */
	protected String expandString(Focus focus, String propertyPath, String input) {
		if (input == null) {
			return null;
		}

		String expandedInput = input;

		int indexStart = expandedInput.indexOf(TOKEN_START);
		int indexEnd = expandedInput.indexOf(TOKEN_END, indexStart + 1);

		while ((indexStart != -1) && (indexEnd != -1)) {
			String token = expandedInput.substring(indexStart + TOKEN_START.length(), indexEnd);
			String replacement;

			if (token.length() == 0) {
				replacement = "";
			} else {
				int parameterStart = token.lastIndexOf(SEPARATOR);
				String tokenPath = "", tokenParameter = "";

				if (parameterStart != -1) {
					tokenPath = token.substring(0, parameterStart);
					tokenParameter = token.substring(parameterStart + 1);
				}

				replacement = getString(focus, tokenPath + "." + tokenParameter);
			}

			expandedInput = expandedInput.substring(0, indexStart) + replacement + expandedInput.substring(indexEnd + 1);

			indexStart = expandedInput.indexOf(TOKEN_START);
			indexEnd = expandedInput.indexOf(TOKEN_END, indexStart + 1);

		}

		return expandedInput;
	}

	/**
	 * Gets the full path for a focus and child path
	 *
	 * @return the full path
	 */
	private static String getFullPath(Focus focus, String path) {
		final String fullPath;
		if (focus == null) {
			fullPath = path;
		} else {
			fullPath = focus.getPath() + path;
		}
		return (fullPath);
	}

	protected static String[] splitStringList(String list) {
		return XStringUtil.splitAndTrim(list, ",");
	}
}

/**
 * The interface to be implemented by classes wishing to operate on traversed
 * nodes
 */
interface Traversor {
	public void operate(ConfigurationTreeNode node);
}

/**
 * Used to add the enumeration of sub-paths to a stack
 */
class KeyEnumerator implements Traversor {
	private Map<String, String> table;

	public KeyEnumerator(Map<String, String> table) {
		this.table = table;
	}

	public void operate(ConfigurationTreeNode node) {
		for (String childName : node.getChildKeys()) {
			table.put(childName, "");
		}
	}
}

/**
 * Used to add the enumeration of parameters to a stack
 */
class DataEnumerator implements Traversor {
	private Map<String, String> table;

	public DataEnumerator(Map<String, String> table) {
		this.table = table;
	}

	public void operate(ConfigurationTreeNode node) {
		for (String dataKey : node.getDataKeys()) {
			table.put(dataKey, "");
		}
	}
}