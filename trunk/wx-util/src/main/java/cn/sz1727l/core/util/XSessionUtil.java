package cn.sz1727l.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * Session操作的工具
 *
 */
public class XSessionUtil {
	/**
	 * 通过名称获取Session里的对象
	 * @param session		HttpSession实例
	 * @param key	Session键名称
	 * @return				Object
	 */
	public static Object getAttribute(HttpSession session, String key) {
		return session.getAttribute(key);
	}
	
	/**
	 * 通过名称获取Session里的对象
	 * @param request		HttpServletRequest实例
	 * @param key	Session键名称
	 * @return				Object
	 */
	public static Object getAttribute(HttpServletRequest request, String key) {
		return request.getSession().getAttribute(key);
	}

	/**
	 * 通过名称设置Session里的对象
	 * @param session		HttpSession实例
	 * @param key	Session键名称
	 * @param value		存入Session的对象
	 */
	public static void setAttribute(HttpSession session, String key, Object value) {
		session.setAttribute(key, value);
	}
	
	/**
	 * 通过名称设置Session里的对象
	 * @param request		HttpServletRequest实例
	 * @param key	Session键名称
	 * @param value		存入Session的对象
	 */
	public static void setAttribute(HttpServletRequest request, String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

	/**
	 * 通过名称删除Session里的对象
	 * @param session		HttpSession实例
	 * @param key	Session键名称
	 */
	public static void removeAttribute(HttpSession session, String key) {
		session.removeAttribute(key);
	}
	
	/**
	 * 通过名称删除Session里的对象
	 * @param request		HttpServletRequest实例
	 * @param key	Session键名称
	 */
	public static void removeAttribute(HttpServletRequest request, String key) {
		request.getSession().removeAttribute(key);
	}
	
	/**
	 * 使Session失效
	 * @param session		HttpSession实例
	 */
	public static void invalidateSession(HttpSession session) {
		session.invalidate();
	}
	
	/**
	 * 使Session失效
	 * @param request		HttpServletRequest实例
	 */
	public static void invalidateSession(HttpServletRequest request) {
		request.getSession().invalidate();
	}
}
