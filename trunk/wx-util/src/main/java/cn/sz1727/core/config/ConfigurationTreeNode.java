package cn.sz1727.core.config;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * cn.sz1727.core.config.ConfigurationTreeNode.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ConfigurationTreeNode {

	private String prefix = "";

	/**
	 * The parent node
	 */
	private ConfigurationTreeNode parent;

	/**
	 * The name of the node.
	 */
	private String name;

	/**
	 * The mapping of child nodes (name -> child).
	 */
	private Map<String, ConfigurationTreeNode> children;

	/**
	 * The mapping of parameter names to values. Note that all parameter values
	 * are stored as strings.
	 */
	private Map<String, String> data;

	/**
	 * @param name
	 *            the name of the node.
	 */
	public ConfigurationTreeNode(String name) {
		this.name = name;
		this.children = new HashMap<String, ConfigurationTreeNode>();
		this.data = new HashMap<String, String>();
	}

	/**
	 * Returns the name of the node.
	 *
	 * @return The name of the node.
	 */
	public String getName() {
		return name;
	}

	private void setParent(ConfigurationTreeNode parent) {
		this.parent = parent;

		if (parent != null) {
			this.prefix = MessageFormat.format("{0}{1}.", parent.getPrefix(), name);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	/**
	 * Adds a node to this node's list of children. Adding two nodes with the
	 * same name is permitted, the last node being remembered.
	 *
	 * @param child
	 *            the child node to be added.
	 */
	public void addChild(ConfigurationTreeNode child) {
		children.put(child.getName(), child);
		child.setParent(this);
	}

	/**
	 * Retrieves the child node with the given name. Note that names are case
	 * sensitive.
	 *
	 * @param name
	 *            the name of the child to be retrieved.
	 * @return the child node with the given name, or null if no child node is
	 *         held by that name.
	 */
	public ConfigurationTreeNode findChild(String name) {
		return children.get(name);
	}

	/**
	 * Returns a set of strings containing the list of names of all stored child
	 * nodes.
	 *
	 * @return The set of child node names.
	 */
	public Set<String> getChildKeys() {
		return children.keySet();
	}

	/**
	 * Returns a collection of nodes containing the list of all stored child
	 * nodes.
	 *
	 * @return The collection of child nodes.
	 */
	public Collection<ConfigurationTreeNode> getChildren() {
		return children.values();
	}

	/**
	 * Sets the value for a parameter in the node.
	 *
	 * @param key
	 *            the name of the parameter to be set.
	 * @param value
	 *            the value to set for the parameter.
	 */
	public void setData(String key, String value) {
		data.put(key, value);
	}

	/**
	 * Retrieves the data value held for the parameter with the given name. Note
	 * that names are case sensitive.
	 *
	 * @param key
	 *            the name of the parameter to be retrieved.
	 * @return the value of the parameter with the given name, or null if no
	 *         parameter is held by that name.
	 */
	public String getData(String key) {
		return (String) data.get(key);
	}

	/**
	 * Returns a set of strings containing the list of names of all stored
	 * parameters.
	 *
	 * @return The set of parameter names.
	 */
	public Set<String> getDataKeys() {
		return data.keySet();
	}

	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (!data.isEmpty()) {
			for (String key : data.keySet()) {
				builder.append(MessageFormat.format("{0}{1}={2}\n", getPrefix(), key, data.get(key)));
			}
		}

		if (!children.isEmpty()) {
			for (ConfigurationTreeNode child : children.values()) {
				builder.append(child.toString());
			}
		}

		return builder.toString();
	}

	public ConfigurationTreeNode getParent() {
		return parent;
	}
}
