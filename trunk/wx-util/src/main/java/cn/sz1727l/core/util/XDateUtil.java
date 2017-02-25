package cn.sz1727l.core.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;

/**
 * cn.sz1727l.core.util.XDateUtil.java
 * 
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public final class XDateUtil extends DateUtils {

	public static final String DATE_FORMAT_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";

	/** 默认显示日期时间的格式 */
	public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

	/** 日期字段 - 年 */
	public static final String DATE_FIELD_YEAR = "1";
	/** 日期字段 - 月 */
	public static final String DATE_FIELD_MONTH = "2";
	/** 日期字段 - 日 */
	public static final String DATE_FIELD_DATE = "3";
	/** 日期字段 - 小时 */
	public static final String DATE_FIELD_HOUR = "4";
	/** 日期字段 - 分钟 */
	public static final String DATE_FIELD_MINUTE = "5";
	/** 日期字段 - 秒 */
	public static final String DATE_FIELD_SECOND = "6";

	/** 默认显示日期的格式 */
	public static final String DATE_FORMAT_YEARMONTH = "yyyy-MM";

	/** 默认显示日期的紧凑格式 */
	public static final String DATE_FORMAT_COMPACT = "yyyyMMdd";

	/** 默认显示日期时间的紧凑格式 */
	public static final String DATE_FORMAT_DATETIME_COMPACT = "yyyyMMddHHmmss";
	
	/** 默认显示日期时间的紧凑格式(MS) */
	public static final String DATE_FORMAT_DATETIME_MS_COMPACT = "yyyyMMddHHmmssS";

	/** 默认日期格式 */
	public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

	/** 默认显示简体中文日期的格式 */
	public static final String DATE_FORMAT_DEFAULT_ZHCN = "yyyy年MM月dd日";

	/** 默认显示简体中文日期时间的格式 */
	public static final String DATE_FORMAT_DATETIME_ZHCN = "yyyy年MM月dd日HH时mm分ss秒";

	private static DateFormat dateFormatDatetimeCompact = new SimpleDateFormat(DATE_FORMAT_DATETIME_COMPACT);
	
	private static DateFormat dateFormatDefault = new SimpleDateFormat(DATE_FORMAT_DEFAULT);

	private static DateFormat dateFormatDatetime = new SimpleDateFormat(DATE_FORMAT_DATETIME);

	private static DateFormat dateFormatDefaultZhcn = new SimpleDateFormat(DATE_FORMAT_DEFAULT_ZHCN);

	private static DateFormat dateFormatDatetimeZhcn = new SimpleDateFormat(DATE_FORMAT_DATETIME_ZHCN);

	private XDateUtil() {
	}

	public static Date getCurrentDate() {
		return new Date();
	}

	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static String getCurrentDate(String format) {
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date currentTime = new Date();
		return formatter.format(currentTime);
	}

	public static String getCurrentTimestamp(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Timestamp currentTime = new Timestamp(new Date().getTime());
		return formatter.format(currentTime);
	}

	public static String formatDate(Date dateTime, String format) {
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(dateTime);
	}

	public static String formatTimestamp(Timestamp dateTime, String format) {
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(dateTime);
	}

	public static Date getDateFromString(String date, String format) {
		// The date must always be in the format of TIME_STAMP_FORMAT
		// i.e. JAN 29 2001 22:50:40 GMT
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);

		// Parse the string back into a Time Stamp.
		return formatter.parse(date, pos);
	}

	public static Timestamp getTimestampFromString(String date, String format) {
		Date d = getDateFromString(date, format);
		return new Timestamp(d.getTime());
	}

	public static String getFormattedDuration(long mills) {
		long days = mills / 86400000;
		mills = mills - (days * 86400000);
		long hours = mills / 3600000;
		mills = mills - (hours * 3600000);
		long mins = mills / 60000;
		mills = mills - (mins * 60000);
		long secs = mills / 1000;
		mills = mills - (secs * 1000);

		StringBuilder bf = new StringBuilder(60);
		bf.append(days).append(" ").append("days, ");
		bf.append(hours).append(" ").append("hours, ");
		bf.append(mins).append(" ").append("mins, ");
		bf.append(secs).append(".").append(mills).append(" ").append("sec");
		return bf.toString();
	}

	/**
	 * 两个日期的差值是否在给定的时间内
	 * 
	 * @param start
	 *            开始
	 * @param end
	 *            结束
	 * @param time
	 *            阈值(单位:ms)
	 * @return
	 */
	public static boolean isAmong(Date start, Date end, long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		long time1 = cal.getTimeInMillis();
		cal.setTime(end);
		long time2 = cal.getTimeInMillis();
		long interval = (time2 - time1);

		return interval <= time;
	}

	private static DateFormat getDateFormat(String format) {
		if (DATE_FORMAT_DEFAULT.equalsIgnoreCase(format)) {
			return dateFormatDefault;
		} else if (DATE_FORMAT_DATETIME.equalsIgnoreCase(format)) {
			return dateFormatDatetime;
		} else if (DATE_FORMAT_DEFAULT_ZHCN.equalsIgnoreCase(format)) {
			return dateFormatDefaultZhcn;
		} else if (DATE_FORMAT_DATETIME_ZHCN.equalsIgnoreCase(format)) {
			return dateFormatDatetimeZhcn;
		} else if (DATE_FORMAT_DATETIME_COMPACT.equalsIgnoreCase(format)) {
			return dateFormatDatetimeCompact;
		} else if (format == null || XStringUtil.isEmpty(format)) {
			return DateFormat.getDateInstance();
		} else {
			return new SimpleDateFormat(format);
		}
	}

	/**
	 * 解析format格式的日期字符串dateTime为Date日期对象
	 * 
	 * @param date
	 *            日期字符串
	 * @param format
	 *            日期时间格式字符串
	 * 
	 * @return Date类型的日期对象, 如果源为空, 则返回<code>null</code>
	 * @throws RuntimeException
	 *             当解析日期字符串失败时，抛出此异常
	 */
	public static Date parseStr2Date(String date, String format) {
		if (date == null || XStringUtil.isEmpty(date)) {
			return null;
		}
		try {
			return getDateFormat(format.replace("hh", "HH")).parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 解析Date日期对象为format格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * @param format
	 *            日期格式字符串
	 * 
	 * @return format格式日期字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String parseDate2Str(Date date, String format) {
		if (date == null) {
			return null;
		}
		return getDateFormat(format.replace("hh", "HH")).format(date);
	}

	/**
	 * 获得指定日期字符串的的日期格式, 目前支持5种: [0-9]{4}-[0-9]{1,2}-[0-9]{1,2} - yyyy-MM-dd
	 * [0-9]{4}-[0-9]{1,2}-[0-9]{1,2}\\s[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2} -
	 * yyyy-MM-dd hh:mm:ss [0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日 - yyyy年MM月dd日
	 * [0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日[0-9]{1,2}时[0-9]{1,2}分[0-9]{1,2}秒 -
	 * yyyy年MM月dd日HH时mm分ss秒
	 * [0-9]{4}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2} -
	 * yyyyMMddhhmmss 如果当前日期字符串格式不在以上五种格式范围内，则返回null
	 * 
	 * @param dateStr
	 *            日期字符串
	 * 
	 * @return 日期格式字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateFormatStr(String dateStr) {
		if (dateStr == null || XStringUtil.isEmpty(dateStr)) {
			return null;
		}
		String patternStr1 = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}"; // "yyyy-MM-dd"
		String patternStr2 = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}\\s[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}"; // "yyyy-MM-dd HH:mm:ss";
		String patternStr3 = "[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日";// "yyyy年MM月dd日"
		String patternStr4 = "[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日[0-9]{1,2}时[0-9]{1,2}分[0-9]{1,2}秒";// "yyyy年MM月dd日HH时mm分ss秒"
		String patternStr5 = "[0-9]{4}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2}[0-9]{1,2}"; // "yyyyMMddHHmmss";

		Pattern p = Pattern.compile(patternStr1);
		if (p.matcher(dateStr).matches()) {
			return DATE_FORMAT_DEFAULT;
		}
		p = Pattern.compile(patternStr2);
		if (p.matcher(dateStr).matches()) {
			return DATE_FORMAT_DATETIME;
		}
		p = Pattern.compile(patternStr3);
		if (p.matcher(dateStr).matches()) {
			return DATE_FORMAT_DEFAULT_ZHCN;
		}
		p = Pattern.compile(patternStr4);
		if (p.matcher(dateStr).matches()) {
			return DATE_FORMAT_DATETIME_ZHCN;
		}
		p = Pattern.compile(patternStr5);
		if (p.matcher(dateStr).matches()) {
			return DATE_FORMAT_DATETIME_COMPACT;
		}
		return null;
	}

	/**
	 * 将dateStr从fmtFrom格式转换到fmtTo的格式
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param fmtFrom
	 *            源日期格式字符串
	 * @param fmtTo
	 *            目标日期格式字符串
	 * 
	 * @return 日期字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String changeDateStrFormat(String dateStr, String fmtFrom, String fmtTo) {
		if (dateStr == null || XStringUtil.isEmpty(dateStr)) {
			return null;
		}
		return parseDate2Str(parseStr2Date(dateStr, fmtFrom), fmtTo);
	}

	/**
	 * 获得指定日期对象中的时分秒, 格式为: hh:mm:ss
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return "hh:mm:ss"格式的时间字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String getTimeStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_DATETIME).substring(DATE_FORMAT_DATETIME.indexOf('h'));
	}

	/**
	 * 获得指定格式日期字符串中的时分秒, 格式为: hh:mm:ss
	 * 
	 * @param dateTime
	 *            日期字符串
	 * @param format
	 *            日期格式字符串
	 * 
	 * @return "HH:mm:ss"格式的时间字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String getTimeStr(String dateTime, String format) {
		return getTimeStr(parseStr2Date(dateTime, format));
	}

	/**
	 * 获得系统当前日期的yyyy-MM-dd格式日期字符串
	 * 
	 * @return "yyyy-MM-dd"格式的日期字符串
	 */
	public static String getCurDateStr() {
		return parseDate2Str(Calendar.getInstance().getTime(), DATE_FORMAT_DEFAULT);
	}

	/**
	 * 获得系统当前日期的yyyy-MM-dd HH:mm:ss格式日期字符串
	 * 
	 * @return "yyyy-MM-dd HH:mm:ss"格式的日期字符串
	 */
	public static String getCurDateTimeStr() {
		return parseDate2Str(Calendar.getInstance().getTime(), DATE_FORMAT_DATETIME);
	}

	/**
	 * 获得系统当前日期的yyyy年MM月dd日格式日期字符串
	 * 
	 * @return "yyyy-MM-dd"格式的日期字符串
	 */
	public static String getCurZhCNDateStr() {
		return parseDate2Str(Calendar.getInstance().getTime(), DATE_FORMAT_DEFAULT_ZHCN);
	}

	/**
	 * 获得系统当前日期的yyyy年MM月dd日HH时mm分ss秒格式日期字符串
	 * 
	 * @return "yyyy年MM月dd日HH时mm分ss秒"格式的日期字符串
	 */
	public static String getCurZhCNDateTimeStr() {
		return parseDate2Str(Calendar.getInstance().getTime(), DATE_FORMAT_DATETIME_ZHCN);
	}

	/**
	 * 获得指定日期对象的years年后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param years
	 *            年数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterYears(Date date, int years) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.YEAR, years);
		return now.getTime();
	}

	/**
	 * 获得指定日期对象的months月后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param months
	 *            月数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterMonths(Date date, int months) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.MONTH, months);
		return now.getTime();
	}

	/**
	 * 获得指定日期对象的days天后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param days
	 *            天数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterDays(Date date, int days) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.DATE, days);
		return now.getTime();
	}

	/**
	 * 获得指定日期对象的hours小时后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param hours
	 *            小时数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterHours(Date date, int hours) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.HOUR_OF_DAY, hours);
		return now.getTime();
	}

	/**
	 * 获得指定日期对象的minutes分钟后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param minutes
	 *            年数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterMinutes(Date date, int minutes) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.MINUTE, minutes);
		return now.getTime();
	}

	/**
	 * 获得指定日期对象的seconds秒后的日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param seconds
	 *            秒数
	 * 
	 * @return 日期对象, 如果源为空, 则返回<code>null</code>
	 */
	public static Date getDateAfterSeconds(Date date, int seconds) {
		if (date == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.SECOND, seconds);
		return now.getTime();
	}

	/**
	 * 转换小时数为毫秒数
	 * 
	 * @param hours
	 *            小时数
	 * 
	 * @return 毫秒数
	 */
	public static long getMillisecondOfHour(double hours) {
		return new BigDecimal(hours).multiply(new BigDecimal(3600 * 1000)).longValue();
	}

	/**
	 * 获取指定日期对象中的毫秒
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 毫秒数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getMillisecondOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * 获取指定日期对象中的秒
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 秒数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getSecondOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * 获取指定日期对象中的分钟
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 分钟数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getMinuteOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		return now.get(Calendar.MINUTE);
	}

	/**
	 * 获取指定日期对象中的小时(24小时制)
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 小时数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getHourOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取指定日期对象中的天(月中的某天)
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 天数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getDayOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取指定日期对象中的月份
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 月份数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getMonthOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取指定日期对象中的年份
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 年份数 @ , 如果源为空, 则抛出此异常
	 */
	public static int getYearOfDate(Date date) {
		if (date == null) {
			throw new RuntimeException("参数为空");
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 返回指定日期对象的上个月的年和月, 格式为：yyyyMM
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return yyyyMM格式的日期字符串, 如果源为空, 则返回<code>null</code>
	 */
	public static String getYearMonthOfLastMonth(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		sdf.setTimeZone(TimeZone.getDefault());
		return (sdf.format(calendar.getTime()));
	}

	/**
	 * 返回系统当前日期的年和月, 格式为：yyyyMM
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return yyyyMM格式的日期字符串
	 */
	public static String getCurYearMonth() {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		sdf.setTimeZone(TimeZone.getDefault());
		return (sdf.format(now.getTime()));
	}

	/**
	 * 返回在当前系统日期的指定字段上加减偏移量后的格式为"yyyy-MM-dd hh:mm:ss"的日期字符串
	 * 
	 * @param field
	 *            需要加的字段: 1 - DATE_FIELD_YEAR - 年 2 - DATE_FIELD_MONTH - 月 3 -
	 *            DATE_FIELD_DATE - 日 4 - DATE_FIELD_HOUR - 时 5 -
	 *            DATE_FIELD_MINUTE - 分 6 - DATE_FIELD_SECOND - 秒
	 * @param amount
	 *            偏移量: amount < 0 表示减 amount > 0 表示加
	 * 
	 * @return 日期/时间字符串, 格式为: "yyyy-MM-dd hh:mm:ss", 如果参数不正确, 则返回
	 *         <code>null</code>
	 */
	public static String add(String field, int amount) {
		if (field == null || XStringUtil.isEmpty(field)) {
			return null;
		}
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		if (DATE_FIELD_YEAR.equals(field)) {
			cal.add(Calendar.YEAR, amount);
		} else if (DATE_FIELD_MONTH.equals(field)) {
			cal.add(Calendar.MONTH, amount);
		} else if (DATE_FIELD_DATE.equals(field)) {
			cal.add(Calendar.DATE, amount);
		} else if (DATE_FIELD_HOUR.equals(field)) {
			cal.add(Calendar.HOUR, amount);
		} else if (DATE_FIELD_MINUTE.equals(field)) {
			cal.add(Calendar.MINUTE, amount);
		} else if (DATE_FIELD_SECOND.equals(field)) {
			cal.add(Calendar.SECOND, amount);
		}
		return parseDate2Str(cal.getTime(), DATE_FORMAT_DATETIME);
	}

	/**
	 * 获得系统当前日期对象中月份的天数
	 * 
	 * @return 天数
	 */
	public static int getCurMonthDays() {
		return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得指定日期对象中月份的天数
	 * 
	 * @param date
	 *            日期对象
	 * @return 天数
	 * @, 如果源为空, 则抛出此异常
	 */
	public static int getMonthDays(Date date) {
		if (date == null) {
			throw new RuntimeException("错误的参数");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得"yyyy-MM-dd"格式日期字符串中月份的天数
	 * 
	 * @param date
	 *            "yyyy-MM-dd"格式日期字符串
	 * 
	 * @return 天数
	 */
	public static int getMonthDays(String date) {
		return getMonthDays(parseStr2Date(date, DATE_FORMAT_DEFAULT));
	}

	/**
	 * 从指定日历对象获得格式为："yyyy-MM-dd"格式的日期字符串
	 * 
	 * @param calendar
	 *            日历对象
	 * 
	 * @return 日期字符串, 格式为："yyyy-MM-dd", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateStr(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return parseDate2Str(calendar.getTime(), DATE_FORMAT_DEFAULT);
	}

	/**
	 * 从指定的日期对象获得"yyyy-MM-dd"格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 日期字符串, 格式为："yyyy-MM-dd", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_DEFAULT);
	}

	/**
	 * 从指定的日期对象获得"yyyy年MM月dd日"格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 日期字符串, 格式为："yyyy年MM月dd日", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateZhCNStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_DEFAULT_ZHCN);
	}

	/**
	 * 从指定的日期对象获得"yyyyMMdd"紧凑格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 日期字符串, 格式为："yyyyMMdd", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateCompactStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_COMPACT);
	}

	/**
	 * 从指定的日期对象获得"yyyy-MM-dd HH:mm:ss"格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 日期字符串, 格式为："yyyy-MM-dd HH:mm:ss", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateTimeStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_DATETIME);
	}

	/**
	 * 从指定的日期对象获得"yyyy年MM月dd日 HH时mm分ss秒"格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * 
	 * @return 日期字符串, 格式为："yyyy年MM月dd日 HH时mm分ss秒", 如果源为空, 则返回<code>null</code>
	 */
	public static String getDateTimeZhCNStr(Date date) {
		if (date == null) {
			return null;
		}
		return parseDate2Str(date, DATE_FORMAT_DATETIME_ZHCN);
	}

	/**
	 * 获得某年某月的第一天日期对象
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * 
	 * @return 日期对象 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static Date getFirstDay(String year, String month) {
		try {
			return getFirstDay(XNumberUtil.toInt(year), XNumberUtil.toInt(month));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得某年某月的第一天日期对象
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * 
	 * @return 日期对象 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static Date getFirstDay(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		try {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}
		return calendar.getTime();
	}

	/**
	 * 获得某年某月的最后一天日期对象
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * 
	 * @return 日期对象 @ , 如果参数错误, 则抛出此异常
	 */
	public static Date getLastDay(String year, String month) {
		try {
			return getLastDay(XNumberUtil.toInt(year), XNumberUtil.toInt(month));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得某年某月的最后一天日期对象
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * 
	 * @return 日期对象 @ , 如果参数错误, 则抛出此异常
	 */
	public static Date getLastDay(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		try {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	/**
	 * 获得当前系统日期的默认格式日期对象
	 * 
	 * @return 默认格式的日期对象
	 */
	public static Date getCurDate() {
		return Calendar.getInstance(TimeZone.getDefault()).getTime();
	}

	/**
	 * 获得两个日期字符串的相隔月数
	 * 
	 * @param startDate
	 *            开始日期字符串
	 * @param endDate
	 *            结束日期字符串
	 * 
	 * @return 相隔月数 @ , 如果解析日期字符串出错, 则抛出此异常
	 */
	public static long getDistanceMonths(String startDate, String endDate) {
		return getDistanceMonths(parseStr2Date(startDate, null), parseStr2Date(endDate, null));
	}

	/**
	 * 获得两个日期对象之间的相隔月数
	 * 
	 * @param startDate
	 *            开始日期对象
	 * @param endDate
	 *            结束日期对象
	 * 
	 * @return 相隔月数 @ , 如果源为空, 则抛出此异常
	 */
	public static long getDistanceMonths(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			throw new RuntimeException("错误的参数");
		}
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		return Math.abs((endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 + (endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)));
	}

	/**
	 * 获得两个日期字符串的相隔天数
	 * 
	 * @param startDate
	 *            开始日期字符串
	 * @param endDate
	 *            结束日期字符串
	 * 
	 * @return 相隔天数 @ , 如果解析日期字符串出错, 则抛出此异常
	 */
	public static long getDistanceDays(String startDate, String endDate) {
		return getDistanceDays(parseStr2Date(startDate, null), parseStr2Date(endDate, null));
	}

	/**
	 * 获得两个日期对象的相隔天数
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * 
	 * @return 相隔天数 @ , 如果源为空, 则抛出此异常
	 */
	public static long getDistanceDays(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			throw new RuntimeException("错误的参数");
		}
		return Math.abs((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
	}

	/**
	 * 获得指定日期字符串与当前系统时间的相隔天数
	 * 
	 * @param startDate
	 *            开始时间
	 * 
	 * @return 相隔天数 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static long getDistanceDays(String dateStr) {
		return getDistanceDays(parseStr2Date(dateStr, null), Calendar.getInstance().getTime());
	}

	/**
	 * 获得指定日期对象与当前系统时间的相隔天数
	 * 
	 * @param date
	 *            开始时间
	 * 
	 * @return 相隔天数 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static long getDistanceDays(Date date) {
		return getDistanceDays(date, Calendar.getInstance().getTime());
	}

	/**
	 * 获得两个日期字符串的相隔毫秒数
	 * 
	 * @param startDateTime
	 *            开始时间, 格式必须是:yyyy-MM-dd hh:mm:ss
	 * @param endDateTime
	 *            结束时间, 格式必须是:yyyy-MM-dd hh:mm:ss
	 * 
	 * @return 毫秒数 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static long getDistanceMillis(String startDateTime, String endDateTime) {
		DateFormat d = getDateFormat(DATE_FORMAT_DATETIME);
		java.util.Date d1;
		java.util.Date d2;
		try {
			d1 = d.parse(startDateTime);
			d2 = d.parse(endDateTime);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return getDistanceMillis(d1, d2);
	}

	/**
	 * 获得两个日期对象之间的相隔毫秒数
	 * 
	 * @param startDateTime
	 *            开始时间
	 * @param endDateTime
	 *            结束时间
	 * 
	 * @return 毫秒数
	 */
	public static long getDistanceMillis(Date startDateTime, Date endDateTime) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDateTime);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDateTime);
		return Math.abs(endCal.getTimeInMillis() - startCal.getTimeInMillis());
	}

	/**
	 * 获得指定日期字符串与当前系统时间的相隔时间数
	 * 
	 * @param startDateTime
	 *            开始时间, 格式必须是:yyyy-MM-dd hh:mm:ss
	 * 
	 * @return 相隔时间数 @ , 如果解析日期字符串失败, 则抛出此异常
	 */
	public static long getDistanceMillis(String startDateTime) {
		return getDistanceMillis(parseStr2Date(startDateTime, DATE_FORMAT_DATETIME), Calendar.getInstance().getTime());
	}

	/**
	 * 校验日期字符串是否是有效日期格式 yyyy-MM-dd hh:mm:ss
	 * 
	 * @param patternString
	 * @return
	 */
	public static boolean isTimeLegal(String patternString) {

		Pattern a = Pattern
				.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$");

		Matcher b = a.matcher(patternString);
		if (b.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取给定时间字符串的几个月后1号
	 * 
	 * @param date
	 * @param format
	 * @param returnFormat
	 * @return
	 * @throws Exception
	 */
	public static String getFirstDayOfAfewMonth(String date, String format, int month, String returnFormat) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFormat(format).parse(date));

		// 几个月后1号
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + month, 1, 0, 0, 0);
		return getDateFormat(returnFormat).format(cal.getTime());
	}
	
	/**
	 * 获取给定时间几个月之后的时间
	 * 
	 * @param date
	 * @param format
	 * @param month
	 * @param returnFormat
	 * @return
	 * @throws Exception
	 */
	public static String getAfewMonthAfterTime(String date, String format, int month, String returnFormat) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFormat(format).parse(date));
		cal.add(Calendar.MONTH, month);
		return getDateFormat(returnFormat).format(cal.getTime());
	}

}
