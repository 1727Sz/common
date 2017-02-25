package cn.sz1727l.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * cn.sz1727l.core.util.XMD5Encrypt.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XMD5Encrypt {

	/**
	 * 使用MD5加密
	 * 
	 * @param signature
	 *            待加密字符串
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String signature) throws RuntimeException {
		return encrypt(signature, "", true);
	}

	/**
	 * 使用MD5加密 - 带编码，用于中文加密
	 * 
	 * @param signature
	 * @param encode
	 * @return
	 * @throws RuntimeException
	 */
	public static String encrypt(String signature, String encode) throws RuntimeException {
		return encrypt(signature, encode, true);
	}

	/**
	 * 使用MD5加密
	 * 
	 * @param signature
	 *            待加密字符串
	 * @param encode
	 *            字符编码(用于中文加密)
	 * @param isUpperCase
	 *            返回加密结果是否转为大写
	 * @return
	 * @throws RuntimeException
	 */
	public static String encrypt(String signature, String encode, boolean isUpperCase) throws RuntimeException {
		if (signature == null) {
			return null;
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("加密算法错误", e);
		}
		String ret = null;
		byte[] plainText = null;
		try {
			if (XStringUtil.isBlank(encode))
				plainText = signature.getBytes();
			else
				plainText = signature.getBytes(encode);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5加密异常", e);
		}

		md5.update(plainText);
		ret = XPacketUtil.hexDump(md5.digest(), isUpperCase);
		return ret;
	}

}