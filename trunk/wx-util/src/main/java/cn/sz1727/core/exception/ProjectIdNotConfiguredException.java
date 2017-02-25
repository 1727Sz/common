package cn.sz1727.core.exception;

import cn.sz1727.core.i18n.ErrorMessage;

/**
 * cn.sz1727.core.exception.ProjectIdNotConfiguredException.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class ProjectIdNotConfiguredException extends MallRuntimeException {

	private static final long serialVersionUID = 1L;

	public ProjectIdNotConfiguredException(ErrorMessage message, Throwable cause) {
		super(message, cause);
	}

	public ProjectIdNotConfiguredException(ErrorMessage message) {
		super(message);
	}

	public ProjectIdNotConfiguredException(Throwable cause) {
		super(cause);
	}

}