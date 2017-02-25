package cn.sz1727l.core.util;

import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;

public class XRandomUtil extends RandomUtils {

	private static final char[] CHARS = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'c', 'q', 'z', 'v', 'k' };

	private XRandomUtil() {
	}
	/**
	 * 获得20位的订单号码：yyyyMMddHHmmss + 6位随机码
	 * 
	 * @return
	 */
	public static String getOrderNum20() {
		return new XRandomUtil().getOrderNum(6);
	}

	/**
	 * 获得订单批次号ContextId
	 * 
	 * @return
	 */
	public static String getContextId() {
		return new XRandomUtil().getUUID();
	}
	
	/**
	 * 获得UUID
	 * 
	 * @return
	 */
	public static String getUUIDStr() {
		return new XRandomUtil().getUUID();
	}

	/**
	 * 获得20位的订单号码：yyyyMMddHHmmss + randomCount位随机码
	 * 
	 * @return
	 */
	private final String getOrderNum(int randomCount) {
		StringBuilder orderNum = new StringBuilder();
		orderNum.append(XDateUtil.getCurrentTimestamp("yyMMddHHmmss"));
		for (int i = 0; i < randomCount; i++) {
			orderNum.append(CHARS[(int) Math.round((Math.random() * 15))]);
		}
		return orderNum.toString();
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	private final String getUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}

}
