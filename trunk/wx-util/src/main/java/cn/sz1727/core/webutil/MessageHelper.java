package cn.sz1727.core.webutil;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * 提供获得国际化消息的快捷方法
 * 
 * cn.sz1727.core.webutil.MessageHelper.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class MessageHelper {

	@Autowired
	private MessageSource messageSource;

	public String getMessage(String key) {
		return _getMessage(key, null, Locale.getDefault());
	}

	public String getMessage(String key, Object[] params) {
		return _getMessage(key, params, Locale.getDefault());
	}

	public String getMessage(String key, Object[] params, Locale locale) {
		return _getMessage(key, params, locale);
	}

	private String _getMessage(String key, Object[] params, Locale locale) {
		return messageSource.getMessage(key, params, locale);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
