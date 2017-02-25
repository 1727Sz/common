package cn.sz1727l.core.util;

/**
 * 校验类中所有方法,如果参数为空和null,则返回false
 * 
 * cn.sz1727l.core.util.XRegexUtil.java
 * 
 * @author Dongd_Zhou
 * @creation 2014年8月20日
 * 
 */
public class XRegexUtil {
	// 手机号码
	private static String MOBILE = "^((\\d{3}))?1[3,5,8,7,4][0-9]\\d{8}";

	// 座机
	private static String TELEPHONE = "0\\d{2,3}-\\d{7,8}|0\\d{4}-\\d{7,8}";

	// 移动手机号码
	private static String MOBILEPHONE = "^(13[4-9]|14[7]|15[012789]|18[23478])\\d{8}$";

	// 邮件
	private static String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	// IP地址
	private static String IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

	// 日期校验[年份从1900-2099 该方法只能校验简单的日期.无法校验闰年、平年，也无法校验大月和小月以及二月的月末]
	private static String DATE = "(19|20)\\d\\d-(0[1-9]|1[0-2])-([0][1-9]|[1,2][0-9]|3[0-1])";

	// 日期校验[年份从1800-9999 该方法支持闰年平年的检验.以及各月的月末]
	private static String FULL_DATE = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))(-)(10|12|0?[13578])(-)(3[01]|[12][0-9]|0?[1-9])$)"
			+ "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))(-)(11|0?[469])(-)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]" + "\\d{2})|([2-9]\\d{3}))(-)(0?2)(-)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(-)(0?2)(-)"
			+ "(29)$)|(^([3579][26]00)(-)(0?2)(-)(29)$)|(^([1][89][0][48])(-)(0?2)(-)(29)$)|(^([2-9][0-9]"
			+ "[0][48])(-)(0?2)(-)(29)$)|(^([1][89][2468][048])(-)(0?2)(-)(29)$)|(^([2-9][0-9][2468][048])"
			+ "(-)(0?2)(-)(29)$)|(^([1][89][13579][26])(-)(0?2)(-)(29)$)|(^([2-9][0-9][13579][26])(-)(0?2)" + "(-)(29)$))";

	/**
	 * 验证的格式有："yyyyMM","yyyyMMdd","yyyyMMdd HH:mm:ss", "yyyyMMddHHmmss",
	 * "yyyy-MM","yyyy-MM-dd","yyyy-MM-dd HH:mm:ss",
	 * "yyyy.MM","yyyy.MM.dd","yyyy.MM.dd HH:mm:ss",
	 * "yyyy/MM","yyyy/MM/dd","yyyy/MM/dd HH:mm:ss",
	 * "yyyy_MM","yyyy_MM_dd","yyyy_MM_dd HH:mm:ss"
	 */
	// 日期时间校验
	private static String DATE_TIME = "("
			+
			// 第一种情况为月份是大月的有31天。
			"(^\\d{3}[1-9]|\\d{2}[1-9]\\d{1}|\\d{1}[1-9]\\d{2}|[1-9]\\d{3}"
			+ // 年
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"(10|12|0?[13578])"
			+ // 大月
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"((3[01]|[12][0-9]|0?[1-9])?)"
			+ // 日(31)要验证年月因此出现0/1次
			"([\\s]?)"
			+ // 空格
			"((([0-1]?[0-9]|2[0-3])(:)?([0-5]?[0-9])(:)?([0-5]?[0-9]))?))$"
			+ // 时分秒
			"|"
			+ // 或
				// 第二种情况为月份是小月的有30天，不包含2月。
			"(^\\d{3}[1-9]|\\d{2}[1-9]\\d{1}|\\d{1}[1-9]\\d{2}|[1-9]\\d{3}"
			+ // 年
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"(11|0?[469])"
			+ // 小月不含2月
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"(30|[12][0-9]|0?[1-9])"
			+ // 日(30)
			"([\\s]?)"
			+ // 空格
			"((([0-1]?[0-9]|2[0-3])(:)?([0-5]?[0-9])(:)?([0-5]?[0-9]))?))$"
			+ // 时分秒
			"|"
			+ // 或
				// 第三种情况为平年月份是2月28天的。
			"(^\\d{3}[1-9]|\\d{2}[1-9]\\d{1}|\\d{1}[1-9]\\d{2}|[1-9]\\d{3}"
			+ // 年
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"(0?2)"
			+ // 平年2月
			"([-/\\._]?)"
			+ // 时间间隔符(-,/,.,_)
			"(2[0-8]|1[0-9]|0?[1-9])"
			+ // 日(28)
			"([\\s]?)"
			+ // 空格
			"((([0-1]?[0-9]|2[0-3])(:)?([0-5]?[0-9])(:)?([0-5]?[0-9]))?))$"
			+ // 时分秒
			"|"
			+ // 或
				// 第四种情况为闰年月份是2月29天的。
				// 可以被4整除但不能被100整除的年份。
				// 可以被400整除的数亦是能被100整除，因此后两位是00，所以只要保证前两位能被4整除即可。
			"(^((\\d{2})(0[48]|[2468][048]|[13579][26]))|((0[48]|[2468][048]|[13579][26])00)" + "([-/\\._]?)" + "(0?2)" + "([-/\\._]?)" + "(29)" + "([\\s]?)"
			+ "((([0-1]?\\d|2[0-3])(:)?([0-5]?\\d)(:)?([0-5]?\\d))?))$" + // 时分秒
			")";

	// 年龄
	private static String AGE = "120|((1[0-1]|\\d)?\\d)";

	// 身份证简单校验[格式检查，不区分平闰年]
	private static String CERT = "[\\d]{6}(19)?[\\d]{2}((0[1-9])|(10|11|12))([012][\\d]|(30|31))[\\d]{3}[xX\\d]*";

	// 闰年出生日期的合法性正则表达式
	private static final String cardRegex15R = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
	// 平年出生日期的合法性正则表达式
	private static final String cardRegex15P = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$";

	// 闰年出生日期的合法性正则表达式
	private static final String cardRegex18R = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";
	// 平年出生日期的合法性正则表达式
	private static final String cardRegex18P = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$";

	// 金钱
	private static String MONEY = "^(([1-9]\\d*)|0)(\\.\\d{1,2})?$";

	/**
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean emailMatch(String email) {
		if (XStringUtil.isNotEmpty(email)) {
			return email.matches(EMAIL);
		}
		return false;
	}

	/**
	 * 校验手机.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean mobileMacth(String mobile) {
		if (XStringUtil.isNotEmpty(mobile)) {
			return mobile.matches(MOBILE);
		}
		return false;
	}

	/**
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean ipMacth(String ip) {
		if (XStringUtil.isNotEmpty(ip)) {
			return ip.matches(IP);
		}
		return false;
	}

	/**
	 * 校验固定电话号码.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean telMacth(String tel) {
		if (XStringUtil.isNotEmpty(tel)) {
			return tel.matches(TELEPHONE);
		}
		return false;
	}

	/**
	 * 年份从1900-2099 该方法只能校验简单的日期.无法校验闰年,平年. 也无法校验大月和小月以及二月的月末
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean simpleDateMatch(String date) {
		if (XStringUtil.isNotEmpty(date)) {
			return date.matches(DATE);
		}
		return false;
	}

	/**
	 * 年份从1800-9999 该方法支持闰年平年的检验.以及各月的月末
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean dateMatch(String date) {
		if (XStringUtil.isNotEmpty(date)) {
			return date.matches(FULL_DATE);
		}
		return false;
	}

	/**
	 * 各年份时间匹配
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean dateTimeMatch(String date) {
		if (XStringUtil.isNotEmpty(date)) {
			return date.matches(DATE_TIME);
		}
		return false;
	}

	/**
	 * 年龄范围0-120
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean ageMatch(String age) {
		if (XStringUtil.isNotEmpty(age)) {
			return age.matches(AGE);
		}
		return false;
	}

	/**
	 * 身份证为15或18位,且生日在1900-1999年之间,支持最后位是x的.大小写都可以
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean simpleCertMatch(String cert) {
		if (XStringUtil.isNotEmpty(cert)) {
			return cert.matches(CERT);
		}
		return false;
	}

	/**
	 * 校验金额.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean moneyMatch(String money) {
		if (XStringUtil.isNotEmpty(money)) {
			return money.matches(MONEY);
		}
		return false;
	}

	/**
	 * 校验是否为移动号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean mobilePhoneMatch(final String phone) {
		// 判断是否中国移动手机号码
		if (XStringUtil.isNotEmpty(phone)) {
			return phone.matches(MOBILEPHONE);
		}
		return false;
	}
	
	/**
	 * 校验身份证号码
	 * @param PhoneNum
	 * @return
	 */
	public static boolean idCardMatch(String idCard){
		if(XStringUtil.isNotEmpty(idCard)){
			return false;
		}
		int len = idCard.length();
		if(len==15){
			if((Integer.parseInt(idCard.substring(6,8))+1900) % 4 == 0 || ((Integer.parseInt(idCard.substring(6,8))+1900) % 100 == 0 && (Integer.parseInt(idCard.substring(6,8))+1900) % 4 == 0 )){
				return idCard.matches(cardRegex15R);
			}else{
				return idCard.matches(cardRegex15P);
			}
		}else if(len==18){
			if(Integer.parseInt(idCard.substring(6,10)) % 4 == 0 || ( Integer.parseInt(idCard.substring(6,10)) % 100 == 0 && Integer.parseInt(idCard.substring(6,10))%4 == 0 )){
				return idCard.matches(cardRegex18R);
			}else{
				return idCard.matches(cardRegex18P);
			}
		}
		return false;
	}
}