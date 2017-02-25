package cn.sz1727.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.exception.UncheckedException;
import cn.sz1727.core.i18n.CoreErrorMessages;

/**
 * cn.sz1727.core.config.PropertyLoader.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class PropertyLoader {
	private static final Logger LOG = LoggerFactory.getLogger(PropertyLoader.class);

	private Vector<Properties> propertyList;

	public PropertyLoader(boolean loadSystemProperties) {
		Properties systemProperties = null;
		propertyList = new Vector<Properties>();
		if (loadSystemProperties) {
			try {
				systemProperties = System.getProperties();
				propertyList.addElement(systemProperties);
			} catch (SecurityException se) {
				// Don't worry just can't include system properties
				LOG.debug("Warning from {} cannot load system properties", this.getClass().getName());
			}
		}
	}

	public PropertyLoader() {
		this(true);
	}

	public PropertyLoader(Properties properties) {
		this();
		load(properties);
	}

	public PropertyLoader(URL url) {
		this();
		load(url);
	}

	public PropertyLoader(String resourceName) {
		this();
		load(resourceName);
	}

	public PropertyLoader(String fileName, String documentBase) {
		this();
		load(fileName, documentBase);
	}

	public void load(Properties properties) {
		propertyList.addElement(properties);
	}

	public void load(URL url) {
		propertyList.addElement(getProperties(url));
	}

	public void load(String resourceName) {
		URL url = null;

		// First see if the named resource is an URL
		try {
			url = new URL(resourceName);
			propertyList.addElement(getProperties(url));
		} catch (MalformedURLException mue) {

			// Ok so not a URL lets see if its a class name
			try {
				Class.forName(resourceName);
				return;
			} catch (ClassNotFoundException cnfe) {
				LOG.error("PropertyLoader.load error1 << : {}", cnfe.getMessage(), cnfe);
			} catch (ClassCastException cnfe) {
				LOG.error("PropertyLoader.load error2 << : {}", cnfe.getMessage(), cnfe);
			}

			// Ok so not a URL lets see if its a file
			// in the class path
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourceName);

			if (in != null) {
				try {
					Properties p = new Properties();
					p.load(in);
					propertyList.addElement(p);
				} catch (IOException ioe) {
					LOG.error("error reading from URL from name [" + url + "]", ioe);
					throw new UncheckedException(CoreErrorMessages.create().loadResourceFromUrlFailed(url, ioe.toString()), ioe);
				}
			} else {
				try {
					url = new URL("file", "", resourceName);
					propertyList.addElement(getProperties(url));
				} catch (MalformedURLException me) {
					LOG.error(" cannot resolve name [" + resourceName + "]", me);
					throw new UncheckedException(CoreErrorMessages.create().resolveResourceNameFailed(resourceName, mue.toString()), mue);
				}
			}
		}
	}

	public void load(String fileName, String documentBase) {
		try {
			URL url = new URL(documentBase + fileName);
			propertyList.addElement(getProperties(url));
		} catch (MalformedURLException mue) {
			LOG.error(" cannot resolve properties file URL from name [" + fileName + "]", mue);
			throw new UncheckedException(CoreErrorMessages.create().resolveResourceNameFailed(fileName, mue.toString()), mue);
		}
	}

	public Properties getProperties() {
		if (propertyList.isEmpty()) {
			return new Properties();
		} else if (propertyList.size() == 1) {
			return (Properties) propertyList.firstElement();
		} else {
			Properties defaults = (Properties) propertyList.lastElement();
			Properties allProps = null;

			for (int i = propertyList.size() - 2; i >= 0; i--) {
				allProps = new Properties(defaults);

				Properties current = (Properties) propertyList.elementAt(i);

				for (Enumeration<?> e = current.propertyNames(); e.hasMoreElements();) {
					String name = (String) e.nextElement();
					allProps.put(name, current.getProperty(name));
				}
				defaults = allProps;
			}
			return allProps;
		}
	}

	private Properties getProperties(URL url) {
		try {
			InputStream in = url.openStream();
			Properties p = new Properties();
			p.load(in);
			return p;
		} catch (IOException ioe) {
			LOG.error("error reading from URL from name [" + url + "]", ioe);
			throw new UncheckedException(CoreErrorMessages.create().loadResourceFromUrlFailed(url, ioe.toString()), ioe);
		}
	}
}