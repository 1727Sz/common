package cn.sz1727l.core.util;

import java.io.PrintWriter;
import java.nio.charset.CharsetDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * cn.sz1727l.core.util.FastJsonBinder.java
 *
 * @author Dongd_Zhou
 * @creation Sep 13, 2014
 * 
 */
public abstract class FastJsonBinder {
	
	private static final Logger logger = Logger.getLogger(FastJsonBinder.class);

	private static final SerializerFeature[] serializerFeatures = { SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero,
			SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.SortField, SerializerFeature.SkipTransientField };

	private static final Feature[] deSerializerFeatures = { Feature.AllowUnQuotedFieldNames, Feature.AllowSingleQuotes, Feature.InternFieldNames, Feature.AllowArbitraryCommas, Feature.IgnoreNotMatch };

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private static SerializeConfig mapping = new SerializeConfig();

	static {
		mapping.put(Date.class, new SimpleDateFormatSerializer(DEFAULT_DATE_FORMAT));
	}

	public static String toJSONString(Object obj) {
		return toJSONString(obj, null);
	}

	public static String toJSONString(Object obj, String dateFormat) {
		if (XStringUtil.isBlank(dateFormat)) {
			return JSON.toJSONString(obj, mapping, serializerFeatures);
		}
		return JSON.toJSONStringWithDateFormat(obj, dateFormat, serializerFeatures);
	}

	public static byte[] toJSONStringBytes(Object obj) {
		return toJSONStringBytes(obj, null);
	}

	public static byte[] toJSONStringBytes(Object obj, String dateFormat) {
		if (!XStringUtil.isBlank(dateFormat)) {
			mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
		}
		return JSON.toJSONBytes(obj, mapping, serializerFeatures);
	}

	public static <T> T fromJSONString(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz, deSerializerFeatures);
	}

	public static <T> T fromJSONString(String json, TypeReference<T> ref) {
		return JSON.parseObject(json, ref, deSerializerFeatures);
	}

	public static <T> T fromJSONString(byte[] bytes, Class<T> clazz) {
		return JSON.<T> parseObject(bytes, clazz, deSerializerFeatures);
	}

	public static <T> T fromJSONString(byte[] bytes, int off, int len, CharsetDecoder charsetDecoder, Class<T> clazz) {
		return JSON.<T> parseObject(bytes, off, len, charsetDecoder, clazz, deSerializerFeatures);
	}

	public static String addEntry(String json, String key, String value, boolean addDelimiter) {
		StringBuilder buff = new StringBuilder();
		buff.append("{").append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
		if (addDelimiter) {
			buff.append(",");
		}
		buff.append(json.substring(1));
		return buff.toString();
	}
	
	/**
	 * 以JSON格式输出数据
	 * @param obj
	 * @return
	 */
	public static void outResult(HttpServletResponse response,Object object) {	
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out=response.getWriter();
			out.print(FastJsonBinder.toJSONString(object));
		} catch (Exception e) {
			logger.error("outResult:以JSON格式输出数据异常", e);
		} finally {
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 以JSON格式输出数据,忽略某些属性
	 * @param obj
	 * @return
	 */
	public static void outResultWithIgnore(HttpServletResponse response,Object object) {	
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out=response.getWriter();
			ObjectMapper mapper = new ObjectMapper(); 
			out.print(mapper.writeValueAsString(object));
		} catch (Exception e) {
			logger.error("outResultWithIgnore:以JSON格式输出数据异常", e);
		} finally {
			out.flush();
			out.close();
		}
	}
}
