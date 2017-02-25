package cn.sz1727.core.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: MessageFactory.java</p>
 * <p>Description: <code>MessageFactory</code> is used to create {@link Message} instance.
 * Subclass should extends this class to customize the bundle reload control.
 * Implementations must save the instance in a field for stateful reload control. 
 * Return null to fallback to default JVM behavior (permanent cache).
 * </p>
 * @version 1.0
 */
/**
 * cn.sz1727.core.i18n.MessageFactory.java
 * 
 * <p>
 * Description: <code>MessageFactory</code> is used to create {@link Message}
 * instance. Subclass should extends this class to customize the bundle reload
 * control. Implementations must save the instance in a field for stateful
 * reload control. Return null to fallback to default JVM behavior (permanent
 * cache).
 * </p>
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class MessageFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MessageFactory.class);

	/**
	 * Default is {@link ReloadControl.Always}.
	 */
	public static final ResourceBundle.Control DEFAULT_RELOAD_CONTROL = new ReloadControl.Always();

	/**
	 * This message key is used for {@link Message} instances that are not read
	 * from a resource bundles but are created only with a string.
	 */
	private static final String STATIC_MESSAGE_KEY = "-1";

	private static final transient Object[] EMPTY_ARGS = new Object[] {};

	protected ResourceBundle.Control reloadControl = null;

	protected MessageFactory() {
	}

	/**
	 * Computes the bundle's full path (
	 * <code>META-INF/messages/&lt;bundleName&gt;-messages.properties</code>)
	 * from <code>bundleName</code>.
	 * 
	 * @param bundleName
	 *            Name of the bundle without the &quot;messages&quot; suffix and
	 *            without file extension.
	 */
	protected static String getBundlePath(String bundleName) {
		return "META-INF.messages." + bundleName + "-messages";
	}

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from the
	 * resource bundle <code>bundlePath</code>.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arg
	 * @see #getBundlePath(String)
	 */
	protected Message createMessage(String bundlePath, String messageKey, Object arg) {
		return createMessage(bundlePath, messageKey, new Object[] { arg });
	}

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from the
	 * resource bundle <code>bundlePath</code>.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arg1
	 * @param arg2
	 * @see #getBundlePath(String)
	 */
	protected Message createMessage(String bundlePath, String messageKey, Object arg1, Object arg2) {
		return createMessage(bundlePath, messageKey, new Object[] { arg1, arg2 });
	}

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from the
	 * resource bundle <code>bundlePath</code>.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @see #getBundlePath(String)
	 */
	protected Message createMessage(String bundlePath, String messageKey, Object arg1, Object arg2, Object arg3) {
		return createMessage(bundlePath, messageKey, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from the
	 * resource bundle <code>bundlePath</code>.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arguments
	 * @see #getBundlePath(String)
	 */
	protected Message createMessage(String bundlePath, String messageKey, Object... arguments) {
		String messageString = getString(bundlePath, messageKey, arguments);
		return new Message(messageString, messageKey, arguments);
	}

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from the
	 * resource bundle <code>bundlePath</code>.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 */
	protected Message createMessage(String bundlePath, String messageKey) {
		String messageString = getString(bundlePath, messageKey, null);
		return new Message(messageString, messageKey, EMPTY_ARGS);
	}

	/**
	 * Factory method to create a {@link Message} instance that is not read from
	 * a resource bundle.
	 * 
	 * @param message
	 *            Message's message text
	 * @return a Messsage instance that has a message key of -1 and no
	 *         arguments.
	 */
	public static Message createStaticMessage(String message) {
		return new Message(message, STATIC_MESSAGE_KEY, EMPTY_ARGS);
	}

	/**
	 * Factory method to read the message with id <code>messageKey</code> from
	 * the resource bundle.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @return formatted message as {@link String}
	 */
	protected String getString(String bundlePath, String messageKey) {
		return getString(bundlePath, messageKey, null);
	}

	/**
	 * Factory method to read the message with id <code>messageKey</code> from
	 * the resource bundle.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arg
	 * @return formatted message as {@link String}
	 */
	protected String getString(String bundlePath, String messageKey, Object arg) {
		Object[] arguments = new Object[] { arg };
		return getString(bundlePath, messageKey, arguments);
	}

	/**
	 * Factory method to read the message with id <code>messageKey</code> from
	 * the resource bundle.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param arg1
	 * @param arg2
	 * @return formatted message as {@link String}
	 */
	protected String getString(String bundlePath, String messageKey, Object arg1, Object arg2) {
		Object[] arguments = new Object[] { arg1, arg2 };
		return getString(bundlePath, messageKey, arguments);
	}

	/**
	 * Factory method to read the message with id <code>messageKey</code> from
	 * the resource bundle.
	 * 
	 * @param bundlePath
	 *            complete path to the resource bundle for lookup
	 * @param messageKey
	 *            message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param args
	 * @return formatted message as {@link String}
	 */
	protected String getString(String bundlePath, String messageKey, Object[] args) {
		// We will throw a MissingResourceException if the bundle name is
		// invalid
		// This happens if the code references a bundle name that just doesn't
		// exist
		ResourceBundle bundle = getBundle(bundlePath);

		try {
			String m = bundle.getString(messageKey);
			return MessageFormat.format(m, args);
		} catch (MissingResourceException e) {
			LOG.error("Failed to find message for id {} in resource bundle {}", messageKey, bundlePath);
			return "";
		}
	}

	/**
	 * @throws MissingResourceException
	 *             if resource is missing
	 */
	private ResourceBundle getBundle(String bundlePath) {
		Locale locale = Locale.getDefault();
		LOG.trace("Loading resource bundle: {} for locale {}", bundlePath, locale);
		final ResourceBundle.Control control = getReloadControl();
		ResourceBundle bundle = control != null ? ResourceBundle.getBundle(bundlePath, locale, getClassLoader(), control) : ResourceBundle.getBundle(bundlePath, locale, getClassLoader());

		return bundle;
	}

	/**
	 * Override this method to return the classloader for the bundle/module
	 * which contains the needed resource files.
	 */
	protected ClassLoader getClassLoader() {
		final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		// if there's a deployment classloader present, use it for finding
		// resources
		return ccl == null ? getClass().getClassLoader() : ccl;
	}

	/**
	 * Subclasses should override to customize the bundle reload control.
	 * Implementations must save the instance in a field for stateful reload
	 * control. Return null to fallback to default JVM behavior (permanent
	 * cache).
	 *
	 * @see #DEFAULT_RELOAD_CONTROL
	 */
	protected ResourceBundle.Control getReloadControl() {
		return reloadControl;
	}
}
