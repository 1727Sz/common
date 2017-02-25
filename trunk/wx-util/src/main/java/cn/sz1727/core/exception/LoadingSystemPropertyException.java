package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.LoadingSystemPropertyException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class LoadingSystemPropertyException extends MallRuntimeException {

	private static final long serialVersionUID = 1L;

	public LoadingSystemPropertyException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public LoadingSystemPropertyException(ErrorMessage message) {
		super(message);
	}

	public LoadingSystemPropertyException(Throwable cause) {
		super(cause);
	}

}