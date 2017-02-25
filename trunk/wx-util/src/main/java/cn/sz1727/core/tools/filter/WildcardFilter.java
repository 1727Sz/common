package cn.sz1727.core.tools.filter;

import static cn.sz1727l.core.util.XClassUtil.equal;
import static cn.sz1727l.core.util.XClassUtil.hash;
import cn.sz1727l.core.util.XStringUtil;

/**
 * <p>
 * Title: WildcardFilter.java
 * </p>
 * <p>
 * Description: <code>WildcardFilter</code> is used to match Strings against
 * wildcards. It performs matches with "*", i.e. "spring.events.*" would catch
 * "spring.events.systemevent" and "jms.events.userevent". This filter accepts a
 * comma-separated list of patterns, so more than one filter pattern can be
 * matched for a given argument: "spring.events.*, spring.actions.*" will match
 * "spring.events.system" and "spring.actions.system" but not "spring.service".
 * </p>
 * 
 * @version 1.0
 */
/**
 * cn.sz1727.core.tools.filter.WildcardFilter.java
 * <p>
 * Description: <code>WildcardFilter</code> is used to match Strings against
 * wildcards. It performs matches with "*", i.e. "spring.events.*" would catch
 * "spring.events.systemevent" and "jms.events.userevent". This filter accepts a
 * comma-separated list of patterns, so more than one filter pattern can be
 * matched for a given argument: "spring.events.*, spring.actions.*" will match
 * "spring.events.system" and "spring.actions.system" but not "spring.service".
 * </p>
 * 
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class WildcardFilter implements Filter<String> {

	protected volatile String pattern;
	protected volatile String[] patterns;
	private volatile boolean caseSensitive = true;

	public WildcardFilter() {
		super();
	}

	public WildcardFilter(String pattern) {
		this.setPattern(pattern);
	}

	@Override
	public boolean accept(String str) {
		if (str == null || pattern == null) {
			return false;
		}

		if (this.pattern.equals(str)) {
			return true;
		}

		String[] currentPatterns = this.patterns;
		if (currentPatterns != null) {
			for (String pattern : currentPatterns) {
				boolean foundMatch;

				if ("*".equals(pattern) || "**".equals(pattern)) {
					return true;
				}

				String candidate = str;

				if (!isCaseSensitive()) {
					pattern = pattern.toLowerCase();
					candidate = candidate.toLowerCase();
				}

				int i = pattern.indexOf('*');
				if (i == -1) {
					foundMatch = pattern.equals(candidate);
				} else {
					int i2 = pattern.indexOf('*', i + 1);
					if (i2 > 1) {
						foundMatch = candidate.indexOf(pattern.substring(1, i2)) > -1;
					} else if (i == 0) {
						foundMatch = candidate.endsWith(pattern.substring(1));
					} else {
						foundMatch = candidate.startsWith(pattern.substring(0, i));
					}
				}

				if (foundMatch) {
					return true;
				}
			}
		}

		return false;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
		this.patterns = XStringUtil.splitAndTrim(pattern, ",");
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final WildcardFilter other = (WildcardFilter) obj;
		return equal(pattern, other.pattern) && equal(patterns, other.patterns) && caseSensitive == other.caseSensitive;
	}

	@Override
	public int hashCode() {
		return hash(new Object[] { this.getClass(), pattern, patterns, caseSensitive });
	}

}
