package cn.sz1727.core.i18n;

/**
 * cn.sz1727.core.i18n.ErrorMessage.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ErrorMessage extends Message {
	private static final long serialVersionUID = 271369796444140386L;
	private int errorCode;

	protected ErrorMessage(String message, int errorCode, Object... args) {
		super(message, String.valueOf(errorCode), args);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "ErrorMessage [errorCode=" + errorCode + ", errorMessage=" + getMessage() + "]";
	}

}
