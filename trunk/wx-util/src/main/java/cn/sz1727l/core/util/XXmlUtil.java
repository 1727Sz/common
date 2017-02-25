package cn.sz1727l.core.util;

import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 * XML报文组装及解析 
 * 
 * @author Dongd_Zhou
 * @date 2015年9月8日 上午10:51:32   
 */
public class XXmlUtil {
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static String XML_CONTENT = "<?xml version=\"1.0\" encoding=\"%s\"?>%s";

	private XXmlUtil() {

	}

	// 将对象转为XML,默认字符集UTF-8
	public static String simpleObject2Xml(Object obj) {
		return simpleObject2Xml(DEFAULT_ENCODING, obj.getClass().getSimpleName(), obj);
	}

	// 将XML转为对象,默认字符集UTF-8
	public static Object simpleXml2Object(String xml, Object obj) {
		return simpleXml2Object(DEFAULT_ENCODING, obj.getClass().getSimpleName(), xml, obj);
	}

	// 将对象转为XML,自定义字符集
	public static String simpleObject2Xml(String encoding, Object obj) {
		return simpleObject2Xml(encoding, obj.getClass().getSimpleName(), obj);
	}

	// 将XML转为对象,自定义字符集
	public static Object simpleXml2Object(String encoding, String xml, Object obj) {
		return simpleXml2Object(encoding, obj.getClass().getSimpleName(), xml, obj);
	}

	// 将对象转为XML,自定义字符集,自定义别名
	public static String simpleObject2Xml(String encoding, String alias, Object obj) {
		XStream xStream = new XStream(new DomDriver(XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding, new XmlFriendlyReplacer("$", "_")));
		xStream.alias(alias, obj.getClass());
		String xml = xStream.toXML(obj);
		String enc = XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding;
		return String.format(XML_CONTENT, enc, xml);
	}

	// 将XML转为对象,自定义字符集,自定义别名
	public static Object simpleXml2Object(String encoding, String alias, String xml, Object obj) {
		XStream xStream = new XStream(new DomDriver(XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding));
		xStream.alias(alias, obj.getClass());
		Object reObj = xStream.fromXML(xml);
		return reObj;
	}

	// 将对象转为XML,默认字符集UTF-8
	public static String simpleObject2Xml(Map<String, Class<?>> map, Object obj) {
		return simpleObject2Xml(DEFAULT_ENCODING, map, obj);
	}

	// 将XML转为对象,默认字符集UTF-8
	public static Object simpleXml2Object(Map<String, Class<?>> map, String xml) {
		return simpleXml2Object(DEFAULT_ENCODING, map, xml);
	}

	// 将对象转为XML
	public static String simpleObject2Xml(String encoding, Map<String, Class<?>> map, Object obj) {
		XStream xStream = new XStream(new DomDriver(XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding, new XmlFriendlyReplacer("$", "_")));
		for (Entry<String, Class<?>> entry : map.entrySet()) {
			xStream.alias(entry.getKey(), entry.getValue());
		}
		String xml = xStream.toXML(obj);
		String enc = XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding;
		return String.format(XML_CONTENT, enc, xml);
	}

	// 将XML转为对象
	public static Object simpleXml2Object(String encoding, Map<String, Class<?>> map, String xml) {
		XStream xStream = new XStream(new DomDriver(XStringUtil.isEmpty(encoding) ? DEFAULT_ENCODING : encoding));
		for (Entry<String, Class<?>> entry : map.entrySet()) {
			xStream.alias(entry.getKey(), entry.getValue());
		}
		Object reObj = xStream.fromXML(xml);
		return reObj;
	}

}
