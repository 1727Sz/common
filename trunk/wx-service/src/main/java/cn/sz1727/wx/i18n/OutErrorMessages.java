package cn.sz1727.wx.i18n;

import cn.sz1727.core.i18n.AbstractErrorMessageFactory;
import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.service.i18n.MUMErrorMessages.java
 * 
 * @author Dongd_Zhou
 * @creation Sep 4, 2014
 * 
 */

public class OutErrorMessages extends AbstractErrorMessageFactory {

	public static final String ERROR_BUNDLE_PREFIX = "out";

	private static class MUMErrorMessagesFactoryHolder {
		public final static OutErrorMessages INSTANCE = new OutErrorMessages();
	}

	private OutErrorMessages() {
	}

	public static OutErrorMessages instance() {
		return MUMErrorMessagesFactoryHolder.INSTANCE;
	}

	@Override
	protected String bundlePrefix() {
		return ERROR_BUNDLE_PREFIX;
	}

	/**
	 * 1001 : 参数[{0}]是必须的
	 * 
	 * @return
	 */
	public ErrorMessage required(String paramName) {
		return createErrorMessage(1001, paramName);
	}

	/**
	 * 9001 : 无效的sql语句 [ {0} ]
	 * 
	 * @param sql
	 * @return
	 */
	public ErrorMessage invalidSql(String sql) {
		return createErrorMessage(9001, sql);
	}

}
