package cn.sz1727.core.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * cn.sz1727.core.exception.ExceptionHelper.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public final class ExceptionHelper {

	private ExceptionHelper() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> T unwrap(T t) {
		if (t instanceof InvocationTargetException) {
			return ((T) ((InvocationTargetException) t).getTargetException());
		}
		return t;
	}
}
