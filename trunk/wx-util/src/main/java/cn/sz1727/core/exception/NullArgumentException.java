package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.NullArgumentException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class NullArgumentException extends InvalidArgumentException {

	private static final long serialVersionUID = 685645626441322597L;

	public NullArgumentException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public NullArgumentException(ErrorMessage message) {
		super(message);
	}

	public NullArgumentException(Throwable cause) {
		super(cause);
	}

}
