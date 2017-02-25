package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.AbstractErrorMessageFactory;
import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.MAORuntimeException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class MallRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 6440564328906969283L;

	private String message = null;
	private ErrorMessage i18nErrorMessage;

	/**
	 * @param message
	 *            the exception message
	 */
	public MallRuntimeException(ErrorMessage message) {
		super();
		setErrorMessage(message);
	}

	/**
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the exception that triggered this exception
	 */
	public MallRuntimeException(ErrorMessage message, Throwable cause) {
		super(ExceptionHelper.unwrap(cause));
		setErrorMessage(message);
	}

	/**
	 * @param cause
	 *            the exception that triggered this exception
	 */
	public MallRuntimeException(Throwable cause) {
		super(ExceptionHelper.unwrap(cause));
		if (cause != null) {
			setErrorMessage(AbstractErrorMessageFactory.createStaticErrorMessage(cause.getMessage() + " (" + cause.getClass().getName() + ")", -1));
		}
	}

	public ErrorMessage getI18nErrorMessage() {
		return i18nErrorMessage;
	}

	public int getErrorMessageCode() {
		return i18nErrorMessage == null ? -1 : i18nErrorMessage.getErrorCode();
	}

	protected void appendMessage(String s) {
		message += s;
	}

	protected void prependMessage(String s) {
		message = message + ". " + s;
	}

	@Override
	public String getMessage() {
		return message;
	}

	protected void setErrorMessage(ErrorMessage errorMessage) {
		this.message = errorMessage.getMessage();
		this.i18nErrorMessage = errorMessage;
	}
}
