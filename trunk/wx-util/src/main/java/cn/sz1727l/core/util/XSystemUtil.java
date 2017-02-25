package cn.sz1727l.core.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cn.sz1727l.core.util.XSystemUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XSystemUtil extends SystemUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SystemUtils.class);

	// bash prepends: declare -x
	// zsh prepends: typeset -x
	private static final String[] UNIX_ENV_PREFIXES = new String[] { "declare -", "typeset -" };

	// the environment of the VM process
	private static Map<String, String> environment = null;

	private XSystemUtil() {
	}

	/**
	 * Get the operating system environment variables. This should work for
	 * Windows and Linux.
	 *
	 * @return Map<String, String> or an empty map if there was an error.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Map<String, String> getenv() {
		if (environment == null) {
			try {
				if (SystemUtils.IS_JAVA_1_4) {
					// fallback to external process
					environment = Collections.unmodifiableMap(getenvJDK14());
				} else {
					// the following runaround is necessary since we still want
					// to
					// compile on JDK 1.4
					Class<?> target = System.class;
					Method envMethod = target.getMethod("getenv", XArrayUtil.EMPTY_CLASS_ARRAY);
					environment = Collections.unmodifiableMap((Map<String, String>) envMethod.invoke(target, (Object[]) null));
				}
			} catch (Exception ex) {
				LOG.error("Could not access OS environment: ", ex);
				environment = Collections.emptyMap();
			}
		}

		return environment;
	}

	private static Map<String, String> getenvJDK14() throws Exception {
		Map<String, String> env = new HashMap<String, String>();
		Process process = null;
		BufferedReader br = null;

		try {
			boolean isUnix = true;
			String command;

			if (SystemUtils.IS_OS_WINDOWS) {
				command = "cmd /c set";
				isUnix = false;
			} else {
				command = "env";
			}

			process = Runtime.getRuntime().exec(command);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			for (line = br.readLine(); line != null; line = br.readLine()) {
				for (int prefix = 0; prefix < UNIX_ENV_PREFIXES.length; prefix++) {
					if (line.startsWith(UNIX_ENV_PREFIXES[prefix])) {
						line = line.substring(UNIX_ENV_PREFIXES[prefix].length());
					}
				}

				int index = line.indexOf('=');
				if (index > -1) {
					String key = line.substring(0, index).trim();
					String value = line.substring(index + 1).trim();
					// remove quotes, if any
					if (isUnix && value.length() > 1 && (value.startsWith("\"") || value.startsWith("'"))) {
						value = value.substring(1, value.length() - 1);
					}
					env.put(key, value);
				} else {
					env.put(line, XStringUtil.EMPTY);
				}
			}
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (br != null) {
				XIOUtil.closeQuietly(br);
			}
		}

		return env;
	}

	public static String getenv(String name) {
		return (String) getenv().get(name);
	}

	public static boolean isSunJDK() {
		return SystemUtils.JAVA_VM_VENDOR.toUpperCase().indexOf("SUN") != -1 || SystemUtils.JAVA_VM_VENDOR.toUpperCase().indexOf("ORACLE") != -1;
	}

	public static boolean isAppleJDK() {
		return SystemUtils.JAVA_VM_VENDOR.toUpperCase().indexOf("APPLE") != -1;
	}

	public static boolean isIbmJDK() {
		return SystemUtils.JAVA_VM_VENDOR.toUpperCase().indexOf("IBM") != -1;
	}

	public static final boolean IS_JAVA_1_7 = (JAVA_VERSION_TRIMMED != null) && JAVA_VERSION_TRIMMED.startsWith("1.7");

	private static CommandLine parseCommandLine(String[] args, String[] opts[]) throws SystemUtilException {
		Options options = new Options();
		for (int i = 0; i < opts.length; i++) {
			options.addOption(opts[i][0], opts[i][1].equals("true") ? true : false, opts[i][2]);
		}

		BasicParser parser = new BasicParser();

		try {
			CommandLine line = parser.parse(options, args, true);
			if (line == null) {
				throw new SystemUtilException("Unknown error parsing the command line");
			}

			return line;
		} catch (ParseException p) {
			throw new SystemUtilException("Unable to parse the command line because of: " + p.toString(), p);
		}
	}

	/**
	 * Returns the value corresponding to the given option from the command
	 * line, for example if the options are "-config ocp-config.xml"
	 * getCommandLineOption("config") would return "ocp-config.xml"
	 */
	public static String getCommandLineOption(String option, String args[], String opts[][]) throws SystemUtilException {
		CommandLine line = parseCommandLine(args, opts);
		return line.getOptionValue(option);
	}

	/**
	 * Checks whether a command line option is set. This is useful for command
	 * line options that don't have an argument
	 */
	public static boolean hasCommandLineOption(String option, String args[], String opts[][]) throws SystemUtilException {
		CommandLine line = parseCommandLine(args, opts);
		return line.hasOption(option);
	}

	/**
	 * Returns a Map of all options in the command line. The Map is keyed off
	 * the option name. The value will be whatever is present on the command
	 * line. Options that don't have an argument will have the String "true".
	 */
	public static Map<String, Object> getCommandLineOptions(String args[], String opts[][]) throws SystemUtilException {
		CommandLine line = parseCommandLine(args, opts);
		Map<String, Object> ret = new HashMap<String, Object>();
		Option[] options = line.getOptions();

		for (int i = 0; i < options.length; i++) {
			Option option = options[i];
			ret.put(option.getOpt(), option.getValue("true"));
		}

		return ret;
	}

	/**
	 * Returns a Map of all valid property definitions in
	 * <code>-Dkey=value</code> format. <code>-Dkey</code> is interpreted as
	 * <code>-Dkey=true</code>, everything else is ignored. Whitespace in values
	 * is properly handled but needs to be quoted properly:
	 * <code>-Dkey="some value"</code>.
	 *
	 * @param input
	 *            String with property definitionn
	 * @return a {@link Map} of property String keys with their defined values
	 *         (Strings). If no valid key-value pairs can be parsed, the map is
	 *         empty.
	 */
	public static Map<String, String> parsePropertyDefinitions(String input) {
		if (XStringUtil.isEmpty(input)) {
			return Collections.emptyMap();
		}

		// the result map of property key/value pairs
		final Map<String, String> result = new HashMap<String, String>();

		// where to begin looking for key/value tokens
		int tokenStart = 0;

		// this is the main loop that scans for all tokens
		findtoken: while (tokenStart < input.length()) {
			// find first definition or bail
			tokenStart = XStringUtil.indexOf(input, "-D", tokenStart);
			if (tokenStart == XStringUtil.INDEX_NOT_FOUND) {
				break findtoken;
			} else {
				// skip leading -D
				tokenStart += 2;
			}

			// find key
			int keyStart = tokenStart;
			int keyEnd = keyStart;

			if (keyStart == input.length()) {
				// short input: '-D' only
				break;
			}

			// let's check out what we have next
			char cursor = input.charAt(keyStart);

			// '-D xxx'
			if (cursor == ' ') {
				continue findtoken;
			}

			// '-D='
			if (cursor == '=') {
				// skip over garbage to next potential definition
				tokenStart = XStringUtil.indexOf(input, ' ', tokenStart);
				if (tokenStart != XStringUtil.INDEX_NOT_FOUND) {
					// '-D= ..' - continue with next token
					continue findtoken;
				} else {
					// '-D=' - get out of here
					break findtoken;
				}
			}

			// apparently there's a key, so find the end
			findkey: while (keyEnd < input.length()) {
				cursor = input.charAt(keyEnd);

				// '-Dkey ..'
				if (cursor == ' ') {
					tokenStart = keyEnd;
					break findkey;
				}

				// '-Dkey=..'
				if (cursor == '=') {
					break findkey;
				}

				// keep looking
				keyEnd++;
			}

			// yay, finally a key
			String key = XStringUtil.substring(input, keyStart, keyEnd);

			// assume that there is no value following
			int valueStart = keyEnd;
			int valueEnd = keyEnd;

			// default value
			String value = "true";

			// now find the value, but only if the current cursor is not a space
			if (keyEnd < input.length() && cursor != ' ') {
				// bump value start/end
				valueStart = keyEnd + 1;
				valueEnd = valueStart;

				// '-Dkey="..'
				cursor = input.charAt(valueStart);
				if (cursor == '"') {
					// opening "
					valueEnd = XStringUtil.indexOf(input, '"', ++valueStart);
				} else {
					// unquoted value
					valueEnd = XStringUtil.indexOf(input, ' ', valueStart);
				}

				// no '"' or ' ' delimiter found - use the rest of the string
				if (valueEnd == XStringUtil.INDEX_NOT_FOUND) {
					valueEnd = input.length();
				}

				// create value
				value = XStringUtil.substring(input, valueStart, valueEnd);
			}

			// finally create key and value && loop again for next token
			result.put(key, value);

			// start next search at end of value
			tokenStart = valueEnd;
		}

		return result;
	}

	/**
	 * Ensure a generated file name is legal.
	 */
	public static String legalizeFileName(String name) {
		if (!SystemUtils.IS_OS_WINDOWS) {
			return name;
		}

		// Assume slashes are deliberate. Change other illegal characters
		return name.replaceAll("[:\\\\]", "_");
	}

	public static class SystemUtilException extends Exception {
		private static final long serialVersionUID = 8441256160321237728L;

		public SystemUtilException() {
			super();
		}

		public SystemUtilException(String message, Throwable cause) {
			super(message, cause);
		}

		public SystemUtilException(String message) {
			super(message);
		}

		public SystemUtilException(Throwable cause) {
			super(cause);
		}

	}
}
