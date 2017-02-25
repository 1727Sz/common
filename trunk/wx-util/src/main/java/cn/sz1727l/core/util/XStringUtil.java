package cn.sz1727l.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

/**
 * cn.sz1727l.core.util.XStringUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public final class XStringUtil extends StringUtils {

	// lookup tables needed for toHexString(byte[], boolean)
	private static final String HEX_CHARACTERS = "0123456789abcdef";
	private static final String HEX_CHARACTERS_UC = HEX_CHARACTERS.toUpperCase();

	private XStringUtil() {
	}

	/**
	 * Like {@link cn.xwtec.mall.mao.util.XStringUtil#split(String, String)}, but
	 * additionally trims whitespace from the result tokens.
	 */
	public static String[] splitAndTrim(String string, String delim) {
		if (string == null) {
			return null;
		}

		if (isEmpty(string)) {
			return XArrayUtil.EMPTY_STRING_ARRAY;
		}

		String[] rawTokens = split(string, delim);
		List<String> tokens = new ArrayList<String>();
		String token;
		if (rawTokens != null) {
			for (int i = 0; i < rawTokens.length; i++) {
				token = trim(rawTokens[i]);
				if (isNotEmpty(token)) {
					tokens.add(token);
				}
			}
		}
		return (String[]) XArrayUtil.toArrayOfComponentType(tokens.toArray(new String[] {}), String.class);
	}

	/**
	 * Convert a hexadecimal string into its byte representation.
	 * 
	 * @param hex
	 *            The hexadecimal string.
	 * @return The converted bytes or <code>null</code> if the hex String is
	 *         null.
	 */
	public static byte[] hexStringToByteArray(String hex) {
		if (hex == null) {
			return null;
		}

		int stringLength = hex.length();
		if (stringLength % 2 != 0) {
			throw new IllegalArgumentException("Hex String must have even number of characters!");
		}

		byte[] result = new byte[stringLength / 2];

		int j = 0;
		for (int i = 0; i < result.length; i++) {
			char hi = Character.toLowerCase(hex.charAt(j++));
			char lo = Character.toLowerCase(hex.charAt(j++));
			result[i] = (byte) ((Character.digit(hi, 16) << 4) | Character.digit(lo, 16));
		}

		return result;
	}

	/**
	 * Like {@link #repeat(String, int)} but with a single character as
	 * argument.
	 */
	public static String repeat(char c, int len) {
		return repeat(CharUtils.toString(c), len);
	}

	/**
	 * @see #toHexString(byte[])
	 */
	public static String toHexString(byte[] bytes) {
		return XStringUtil.toHexString(bytes, false);
	}

	/**
	 * Convert a byte array to a hexadecimal string.
	 * 
	 * @param bytes
	 *            The bytes to format.
	 * @param uppercase
	 *            When <code>true</code> creates uppercase hex characters
	 *            instead of lowercase (the default).
	 * @return A hexadecimal representation of the specified bytes.
	 */
	public static String toHexString(byte[] bytes, boolean uppercase) {
		if (bytes == null) {
			return null;
		}

		int numBytes = bytes.length;
		StringBuilder str = new StringBuilder(numBytes * 2);

		String table = (uppercase ? HEX_CHARACTERS_UC : HEX_CHARACTERS);

		for (int i = 0; i < numBytes; i++) {
			str.append(table.charAt(bytes[i] >>> 4 & 0x0f));
			str.append(table.charAt(bytes[i] & 0x0f));
		}

		return str.toString();
	}

}
