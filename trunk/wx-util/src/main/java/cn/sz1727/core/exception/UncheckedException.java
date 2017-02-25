package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.UncheckedException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class UncheckedException extends MallRuntimeException {

	private static final long serialVersionUID = 137573235013939104L;

	/**
	 * @param message
	 * @param cause
	 */
	public UncheckedException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UncheckedException(ErrorMessage message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UncheckedException(Throwable cause) {
		super(cause);
	}

}
