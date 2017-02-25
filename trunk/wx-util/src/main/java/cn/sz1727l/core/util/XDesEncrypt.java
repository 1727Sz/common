package cn.sz1727l.core.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * DES加解密
 * 
 * @author Dongd_Zhou
 * @date 2015-01-21
 */
@SuppressWarnings("restriction")
public class XDesEncrypt {

	private static XDesEncrypt instance = null;
	private Key key;

	private XDesEncrypt() {
	}

	private XDesEncrypt(String keyStr) throws RuntimeException {
		setKey(keyStr);
	}

	/**
	 * 默认密钥
	 */
	private static byte[] BOSS_SECRET_KEY = { 0x0b, 0x13, (byte) 0xe7, (byte) 0xb2, 0x51, 0x0d, 0x75, (byte) 0xc2, 0x4e, (byte) 0xdd, (byte) 0x4b, (byte) 0x51, 0x24, 0x36, (byte) 0xa8, (byte) 0x28,
			0x0b, 0x13, (byte) 0xe2, (byte) 0xb2, 0x31, 0x0d, 0x75, (byte) 0xc1 };

	/**
	 * 根据参数生成KEY
	 */
	private void setKey(String keyStr) throws RuntimeException {
		DESKeySpec dks = null;
		try {
			if (XStringUtil.isNotEmpty(keyStr)) {
				dks = new DESKeySpec(keyStr.getBytes("UTF-8"));
			} else {
				dks = new DESKeySpec(BOSS_SECRET_KEY);
			}
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			this.key = keyFactory.generateSecret(dks);
		} catch (Exception e) {
			throw new RuntimeException("DESkey生成失败", e);
		}
	}

	public static XDesEncrypt getInstace() throws RuntimeException {
        if (instance == null) {
            instance = new XDesEncrypt(null);
        }
        return instance;
    }
	
	public static XDesEncrypt getInstace(String keyStr) throws RuntimeException {
		if (instance == null) {
			instance = new XDesEncrypt(keyStr);
		}
		return instance;
	}

	/**
	 * 加密String明文输入,String密文输出
	 * 
	 * @param strMing
	 * @return
	 * @throws Exception
	 */
	public String getEncStr(String strMing) throws RuntimeException {
		if (XStringUtil.isEmpty(strMing)) {
			return null;
		}
		BASE64Encoder base64en = new BASE64Encoder();
		try {
			byte[] byteMing = strMing.getBytes("UTF-8");
			byte[] byteMi = this.getEncCode(byteMing);
			return base64en.encode(byteMi);
		} catch (Exception e) {
			throw new RuntimeException("DES加密异常", e);
		}
	}

	/**
	 * 加密以byte[]明文输入,byte[]密文输出
	 * 
	 * @param byteS
	 * @return
	 */
	private byte[] getEncCode(byte[] byteS) throws RuntimeException {
		byte[] byteFina = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			throw new RuntimeException("DES加密失败", e);
		}
		return byteFina;
	}

	/**
	 * 解密 以String密文输入,String明文输出
	 * 
	 * @param strMi
	 * @return
	 * @throws Exception
	 */
	public String getDesStr(String strMi) throws RuntimeException {
		if (XStringUtil.isEmpty(strMi)) {
			return null;
		}
		BASE64Decoder base64De = new BASE64Decoder();
		try {
			byte[] byteMi = base64De.decodeBuffer(strMi);
			byte[] byteMing = this.getDesCode(byteMi);
			return new String(byteMing, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("DES解密异常", e);
		}
	}

	/**
	 * 解密以byte[]密文输入,以byte[]明文输出
	 * 
	 * @param byteD
	 * @return
	 * @throws Exception
	 */
	private byte[] getDesCode(byte[] byteD) throws RuntimeException {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(byteD);
		} catch (Exception e) {
			throw new RuntimeException("DES解密失败", e);
		}
	}

	private byte[] doEncrypt(byte[] plainText, byte[] bytekey) throws RuntimeException {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 加密后的字符串
		byte[] encryptedData = null;
		try {
			// 从原始密匙数据创建DESKeySpec对象
			DESedeKeySpec dks = new DESedeKeySpec(bytekey);

			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			// 一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey key = keyFactory.generateSecret(dks);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, key, sr);
			// 现在，获取数据并加密
			byte data[] = plainText;/* 用某种方法获取数据 */
			// 正式执行加密操作

			// data长度不够8的整数倍，后面补零
			int a = (8 - (data.length % 8));

			if (a > 0) {
				byte[] arr = new byte[a];

				while (a != 0) {
					arr[--a] = (byte) 0;
				}
				data = XArrayUtil.addAll(data, arr);
			}
			encryptedData = cipher.doFinal(data);
		} catch (Exception e) {
			throw new RuntimeException("DES加密失败", e);
		}
		return encryptedData;
	}
	
	private String toHexString(byte[] value) {
        String newString = "";
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            String str = Integer.toHexString(b);
            if (str.length() > 2) {
                str = str.substring(str.length() - 2);
            }
            if (str.length() < 2) {
                str = "0" + str;
            }
            newString += str;
        }
        return newString.toUpperCase();
    }

	 /**
     * 对字符串进行DESede加密
     * 
     * @param val
     * @param bytekey
     * @return
     */
	public static String getDesString(String val, byte[] byteKey) {
		XDesEncrypt des = new XDesEncrypt();
		byte[] result = des.doEncrypt(val.getBytes(), byteKey);
		return des.toHexString(result);
	}
	
	
	public static void main(String[] args) {
	    System.out.println(XDesEncrypt.getInstace().getDesStr("6B4NrMCTL5M="));
    }

}
