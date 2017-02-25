package cn.sz1727.core.i18n;

/**
 * cn.sz1727.core.i18n.AbstractErrorMessageFactory.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public abstract class AbstractErrorMessageFactory extends AbstractMessageFactory {

	@Override
	protected String getBundleName() {
		return bundlePrefix() + "-error";
	}

	public static ErrorMessage createStaticErrorMessage(String message, int errorCode) {
		return new ErrorMessage(message, errorCode);
	}

	protected ErrorMessage createErrorMessage(int errorCode, Object... args) {
		Message msg = createMessage(String.valueOf(errorCode), args);
		ErrorMessage errorMessage = new ErrorMessage(msg.getMessage(), errorCode, args);
		return errorMessage;
	}

	protected abstract String bundlePrefix();

}
