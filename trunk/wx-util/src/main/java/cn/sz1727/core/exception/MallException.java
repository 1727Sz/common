package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.AbstractErrorMessageFactory;
import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.MAOException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public abstract class MallException extends Exception {
	private static final long serialVersionUID = 5332991293396666676L;
	private String message = null;
	private ErrorMessage i18nErrorMessage;

	public MallException(ErrorMessage message) {
		super();
		setErrorMessage(message);
	}

	public MallException(ErrorMessage message, Throwable cause) {
		super(ExceptionHelper.unwrap(cause));
		setErrorMessage(message);
	}

	public MallException(Throwable cause) {
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
