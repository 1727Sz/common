package cn.sz1727l.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cn.sz1727l.core.util.XIOUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XIOUtil extends IOUtils {
	private static final Logger LOG = LoggerFactory.getLogger(XIOUtil.class);

	private XIOUtil() {
	}

	/**
	 * Attempts to load a resource from the file system, from a URL, or from the
	 * classpath, in that order.
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 * @return the requested resource as a string
	 * @throws java.io.IOException
	 *             IO error
	 */
	public static String getResourceAsString(final String resourceName, final Class<?> callingClass) throws IOException {
		InputStream is = getResourceAsStream(resourceName, callingClass);
		if (is != null) {
			return toString(is);
		} else {
			throw new IOException("Unable to load resource " + resourceName);
		}
	}

	/**
	 * Attempts to load a resource from the file system, from a URL, or from the
	 * classpath, in that order.
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 * @return an InputStream to the resource or null if resource not found
	 * @throws java.io.IOException
	 *             IO error
	 */
	public static InputStream getResourceAsStream(final String resourceName, final Class<?> callingClass) throws IOException {
		return getResourceAsStream(parseResourceName(resourceName), callingClass, true, true);
	}

	/**
	 * Attempts to load a resource from the file system, from a URL, or from the
	 * classpath, in that order.
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 * @param tryAsFile
	 *            - try to load the resource from the local file system
	 * @param tryAsUrl
	 *            - try to load the resource as a URL
	 * @return an InputStream to the resource or null if resource not found
	 * @throws java.io.IOException
	 *             IO error
	 */
	public static InputStream getResourceAsStream(final String resourceName, final Class<?> callingClass, boolean tryAsFile, boolean tryAsUrl) throws IOException {

		URL url = getResourceAsUrl(resourceName, callingClass, tryAsFile, tryAsUrl);

		if (url == null) {
			return null;
		} else {
			return url.openStream();
		}
	}

	/**
	 * Attempts to load a resource from the file system or from the classpath,
	 * in that order.
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 * @return an URL to the resource or null if resource not found
	 */
	public static URL getResourceAsUrl(final String resourceName, final Class<?> callingClass) {
		return getResourceAsUrl(resourceName, callingClass, true, true);
	}

	/**
	 * Attempts to load a resource from the file system or from the classpath,
	 * in that order.
	 * 
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 * @param tryAsFile
	 *            - try to load the resource from the local file system
	 * @param tryAsUrl
	 *            - try to load the resource as a Url string
	 * @return an URL to the resource or null if resource not found
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static URL getResourceAsUrl(final String resourceName, final Class<?> callingClass, boolean tryAsFile, boolean tryAsUrl) {
		if (resourceName == null) {
			throw new IllegalArgumentException("Resource name is null");
		}
		URL url = null;

		// Try to load the resource from the file system.
		if (tryAsFile) {
			try {
				File file = XFileUtil.newFile(resourceName);
				if (file.exists()) {
					url = file.getAbsoluteFile().toURI().toURL();
				} else {
					LOG.debug("Unable to load resource from the file system: {}", file.getAbsolutePath());
				}
			} catch (Exception e) {
				LOG.warn("Unable to load resource from the file system: " + e.getMessage());
			}
		}

		// Try to load the resource from the classpath.
		if (url == null) {
			try {
				url = (URL) AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return XClassUtil.getResource(resourceName, callingClass);
					}
				});
				if (url == null) {
					LOG.debug("Unable to load resource {} from the classpath", resourceName);
				}
			} catch (Exception e) {
				LOG.warn("Unable to load resource " + resourceName + " from the classpath: " + e.getMessage());
			}
		}

		if (url == null) {
			try {
				url = new URL(resourceName);
			} catch (MalformedURLException e) {
				LOG.warn(e.getMessage());
			}
		}
		return url;
	}

	/**
	 * This method checks whether the name of the resource needs to be parsed.
	 * If it is, it parses the name and tries to get the variable from the
	 * Environmental Variables configured on the system.
	 * 
	 * @param src
	 */
	private static String parseResourceName(String src) {
		String var;
		String[] split;
		String ps = File.separator;

		if (src.indexOf('$') > -1) {
			split = src.split("}");
		} else {
			return src;
		}

		var = split[0].substring(2);
		var = XSystemUtil.getenv(var);
		if (split.length > 1) {
			if (var == null) {
				var = System.getProperty(split[0].substring(2));
				if (var == null) {
					return split[1].substring(1);
				} else {
					return var + ps + split[1].substring(1);
				}
			} else {
				return var + ps + split[1].substring(1);
			}
		} else {
			if (var == null) {
				return "";
			} else {
				return var;
			}
		}
	}

	/**
	 * This method wraps {@link org.apache.commons.io.IOUtils}'
	 * <code>toString(InputStream)</code> method but catches any
	 * {@link IOException} and wraps it into a {@link RuntimeException}.
	 */
	public static String toString(InputStream input) {
		try {
			return IOUtils.toString(input);
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}

	/**
	 * This method wraps {@link org.apache.commons.io.IOUtils}'
	 * <code>toByteArray(InputStream)</code> method but catches any
	 * {@link IOException} and wraps it into a {@link RuntimeException}.
	 */
	public static byte[] toByteArray(InputStream input) {
		try {
			return IOUtils.toByteArray(input);
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}
}
