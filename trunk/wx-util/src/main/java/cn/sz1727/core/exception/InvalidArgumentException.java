package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.InvalidArgumentException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class InvalidArgumentException extends MallRuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidArgumentException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public InvalidArgumentException(ErrorMessage message) {
		super(message);
	}

	public InvalidArgumentException(Throwable cause) {
		super(cause);
	}

}
