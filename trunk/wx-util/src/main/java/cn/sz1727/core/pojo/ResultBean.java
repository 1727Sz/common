package cn.sz1727.core.pojo;

import cn.sz1727.core.exception.MallException;
import cn.sz1727l.core.util.XStringUtil;

public class ResultBean {

	protected static final String SUCCESS_RESULT = "1";
	protected static final String FAIL_RESULT = "0";

	private String errorCode = XStringUtil.EMPTY;
	private String errorMessage = XStringUtil.EMPTY;

	/**
	 * 1 成功 0 失败
	 */
	private String resultCode;
	private Object data = XStringUtil.EMPTY;

	private ResultBean() {
	}

	private ResultBean(String resultCode) {
		this.resultCode = resultCode;
	}

	private ResultBean(String errorCode, String errorMessage) {
		this.resultCode = FAIL_RESULT;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	private ResultBean(String errorCode, String errorMessage, Object data) {
		this(errorCode, errorMessage);
		this.data = data;
	}

	private ResultBean(String resultCode, Object data2) {
		this.resultCode = resultCode;
		this.data = data2;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public static ResultBean createSuccessResult() {
		return new ResultBean(SUCCESS_RESULT);
	}

	public static ResultBean createSuccessResult(Object data) {
		return new ResultBean(SUCCESS_RESULT, data);
	}

	public static ResultBean createFailResult(String errorCode, String errorMessage) {
		return new ResultBean(errorCode, errorMessage);
	}

	public static ResultBean createFailResult(String errorCode, String errorMessage, Object data) {
		return new ResultBean(errorCode, errorMessage, data);
	}

	public static ResultBean createFailResult(MallException ex) {
		return ResultBean.createFailResult(ex, XStringUtil.EMPTY);
	}

	public static ResultBean createFailResult(MallException ex, Object data) {
		String errorCode = "" + ex.getI18nErrorMessage().getErrorCode();
		String errorMessage = ex.getI18nErrorMessage().getMessage();
		return ResultBean.createFailResult(errorCode, errorMessage, data);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
