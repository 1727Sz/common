package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.DaoException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class DaoException extends MallException {

	private static final long serialVersionUID = 1372913900530868575L;

	public DaoException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(ErrorMessage message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}
}
