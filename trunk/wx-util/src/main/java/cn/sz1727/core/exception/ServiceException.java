package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.ServiceException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ServiceException extends MallException {

	private static final long serialVersionUID = 1372913900530868575L;

	public ServiceException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(ErrorMessage message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}
