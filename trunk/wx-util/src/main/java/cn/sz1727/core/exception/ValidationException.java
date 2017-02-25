package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.ValidationException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ValidationException extends MallException {

	private static final long serialVersionUID = -8750019326263290188L;

	public ValidationException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(ErrorMessage message) {
		super(message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

}