package cn.sz1727.core.i18n;

import cn.sz1727.core.exception.NullArgumentException;
import cn.sz1727.core.util.XStringUtil;

/**
 * cn.sz1727.core.i18n.AbstractMessageFactory.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public abstract class AbstractMessageFactory extends MessageFactory {

	/**
	 * Computes the bundle's full path (
	 * <code>META-INF/messages/&lt;bundleName&gt;-messages.properties</code>)
	 * from <code>bundleName</code>.
	 * 
	 * BundleName is the name of the bundle without the &quot;messages&quot;
	 * suffix and without file extension.
	 * 
	 * @return
	 */
	protected abstract String getBundleName();

	/**
	 * Factory method to create a new {@link Message} instance that is filled
	 * with the formatted message with id <code>messageKey</code> from
	 * error-messages.properties.
	 * 
	 * @param messageKey
	 *            Message key i.e. a.b.c.description=This is a sample message
	 *            from {0} and {1} {0}, {1} are the arguments placeholder
	 * @param args
	 *            The parameters for formating message
	 * @return
	 */
	public Message createMessage(String messageKey, Object... args) {
		String bundleName = getBundleName();
		if (XStringUtil.isBlank(bundleName)) {
			throw new NullArgumentException(CoreErrorMessages.create().nullArguments("resource bundle name"));
		}
		return createMessage(getBundlePath(bundleName), messageKey, args);
	}
}
