package cn.sz1727.core.webutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727l.core.util.XStringUtil;

/**
 * 转码
 * 
 * @author Dongd_Zhou
 * @date 2015-05-10
 */
public class XURLCodeUtil {
	private static final Logger LOG = LoggerFactory.getLogger(XURLCodeUtil.class);
	
	private XURLCodeUtil(){
		
	}

	public static String encode(String str, String enc) {
		if (XStringUtil.isEmpty(str)) {
			return null;
		}

		try {
			return URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			LOG.error("转码失败", e);
		}
		return str;
	}
	
	public static String decode(String str, String enc) {
		if (XStringUtil.isEmpty(str)) {
			return null;
		}

		try {
			return URLDecoder.decode(str, enc);
		} catch (UnsupportedEncodingException e) {
			LOG.error("转码失败", e);
		}
		return str;
	}
	
	public static String encode(String str) {
		return encode(str, "UTF-8");
	}
	
	public static String decode(String str) {
		return decode(str, "UTF-8");
	}

}
