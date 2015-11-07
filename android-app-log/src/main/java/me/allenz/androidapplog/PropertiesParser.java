package me.allenz.androidapplog;

import java.io.File;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.text.TextUtils;

public class PropertiesParser {

	private static final Logger internalLogger = LoggerFactory
			.getInternalLogger();

	private static final String DEBUG_KEY = "debug";

	private static final String ROOT_KEY = "root";

	private static final String HANDLE_EXCEPTION_KEY = "handleex";

	private static final String LOGCAT_KEY = "logcat";

	private static final String FILE_KEY = "file";

	private static final String FILE_INTERNAL = "${internal}";

	private static final String FILE_EXTERNAL = "${external}";

	private static final Pattern FILE_VALUE_PATTERN = Pattern
			.compile("^(.+?)(,(.+?))?(,(.+?))?(,(.*))*$");

	private static final String TEXTVIEW_KEY = "textview";

	private static final String LOGGER_PREFIX = "logger.";

	private static final Pattern LOGGER_VALUE_PATTERN = Pattern
			.compile("^(.+?)(,(.+?))?(,(.+?))?(,(.*))*$");

	private static final Pattern PACKAGE_NAME_PATTERN = Pattern
			.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");

	private Properties properties;

	public PropertiesParser(final Properties properties) {
		this.properties = properties;
	}

	public Configure parse() {
		final Configure configure = new Configure();
		parseDebug(configure);
		parseRoot(configure);
		parseLoggers(configure);
		parseHandleEx(configure);
		parseLogCat(configure);
		parseFile(configure);
		parseTextView(configure);
		return configure;
	}

	private void parseDebug(final Configure configure) {
		final String value = (String) properties.get(DEBUG_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final Boolean debug = booleanValueOf(value.trim());
		if (debug == true) {
			configure.setDebug(true);
			internalLogger.verbose("properties: enable aal debug log");
		} else if (debug == false) {
			configure.setDebug(false);
			internalLogger.verbose("properties: disable aal debug log");
		}
	}

	private void parseRoot(final Configure configure) {
		final String value = (String) properties.get(ROOT_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final LoggerConfig loggerConfig = parseLogger(ROOT_KEY, value.trim());
		if (loggerConfig != null) {
			configure.setRootLogLevel(loggerConfig.getLevel());
			configure.setRootTag(loggerConfig.getTag());
			configure.setRootShowThread(loggerConfig.isShowThreadName());
			internalLogger
			.verbose("properties: logger root : %s", loggerConfig);
		} else {
			internalLogger.verbose("properties: parse logger root failed : %s",
					value);
		}
	}

	private void parseLoggers(final Configure configure) {
		final int loggerPrefixLength = LOGGER_PREFIX.length();
		for (final Enumeration<?> names = properties.propertyNames(); names
				.hasMoreElements();) {
			final String propertyName = (String) names.nextElement();
			if (TextUtils.isEmpty(propertyName)
					|| propertyName.length() <= loggerPrefixLength) {
				continue;
			}
			if (propertyName.startsWith(LOGGER_PREFIX)) {
				final String name = propertyName.substring(
						LOGGER_PREFIX.length(), propertyName.length());
				if (PACKAGE_NAME_PATTERN.matcher(name).matches()) {
					final String propertyValue = properties
							.getProperty(propertyName);
					final LoggerConfig loggerConfig = parseLogger(name,
							propertyValue.trim());
					if (loggerConfig != null) {
						configure.addLoggerConfig(name,
								loggerConfig.getLevel(), loggerConfig.getTag(),
								loggerConfig.isShowThreadName());
						internalLogger.verbose("properties: logger '%s': %s",
								name, loggerConfig);
					} else {
						internalLogger.verbose(
								"properties: parse logger %s failed : %s",
								name, propertyValue);
					}
				} else {
					internalLogger
					.verbose(
							"properties: name '%s' is illegal, it should be package or class fullname",
							name);
				}
			}
		}
	}

	private LoggerConfig parseLogger(final String name,
			final String propertyValue) {
		if (TextUtils.isEmpty(propertyValue)) {
			internalLogger.verbose(
					"properties: property value of logger '%s' is empty, skip",
					name);
			return null;
		}
		LogLevel level = null;
		String tag = null;
		boolean thread = false;
		final Matcher matcher = LOGGER_VALUE_PATTERN.matcher(propertyValue);
		if (matcher.matches()) {
			level = logLevelValueOf(matcher.group(1));
			tag = tagValueOf(name, matcher.group(3));
			thread = Boolean.valueOf(matcher.group(5));
		}
		return level == null ? null
				: new LoggerConfig(name, tag, level, thread);
	}

	private LogLevel logLevelValueOf(final String str) {
		try {
			return LogLevel.valueOf(str.toUpperCase(Locale.ENGLISH));
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}

	private String tagValueOf(final String name, final String str) {
		return TextUtils.isEmpty(str) ? null : str;
	}

	private void parseHandleEx(final Configure configure) {
		final String value = (String) properties.get(HANDLE_EXCEPTION_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final Boolean handleException = booleanValueOf(value.trim());
		if (handleException == true) {
			configure.setHandleException(true);
			internalLogger
			.verbose("properties: enable logging uncaught exception");
		} else if (handleException == false) {
			configure.setHandleException(false);
			internalLogger
			.verbose("properties: disable logging uncaught exception");
		}
	}

	private void parseLogCat(final Configure configure) {
		final String value = (String) properties.get(LOGCAT_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final Boolean useLogCatAppender = booleanValueOf(value.trim());
		if (useLogCatAppender == true) {
			configure.setUseLogCatAppender(true);
			internalLogger.verbose("properties: enable logcat appender");
		} else if (useLogCatAppender == false) {
			configure.setUseLogCatAppender(false);
			internalLogger.verbose("properties: disable logcat appender");
		}
	}

	private void parseTextView(final Configure configure) {
		final String value = (String) properties.get(TEXTVIEW_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final Boolean useTextViewAppender = booleanValueOf(value.trim());
		if (useTextViewAppender == true) {
			configure.setUseTextViewAppender(true);
			internalLogger.verbose("properties: enable textview appender");
		} else if (useTextViewAppender == false) {
			configure.setUseTextViewAppender(false);
			internalLogger.verbose("properties: disable textview appender");
		}
	}

	private void parseFile(final Configure configure) {
		final String value = (String) properties.get(FILE_KEY);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		final Matcher matcher = FILE_VALUE_PATTERN.matcher(value.trim());
		Boolean use = null;
		File dir = null;
		Long size = null;
		Boolean useGZip = null;
		if (matcher.matches()) {
			use = booleanValueOf(matcher.group(1));
			dir = logDirValueOf(matcher.group(3));
			try {
				size = Long.valueOf(matcher.group(5));
			} catch (final NumberFormatException e) {
			}
			useGZip = booleanValueOf(matcher.group(7));
		}
		if (use == true) {
			configure.setUseFileAppender(true);
			if (dir != null) {
				configure.setLogFileDir(dir);
			}
			if (size != null) {
				configure.setLogFileRollingSize(size);
			}
			if (useGZip != null) {
				configure.setCompressLogFiles(useGZip);
			}
			internalLogger.verbose("properties: enable rolling file appender");
		} else {
			configure.setUseFileAppender(false);
			internalLogger.verbose("properties: disable rolling file appender");
		}

	}

	private File logDirValueOf(final String str) {
		File dir;
		if (TextUtils.isEmpty(str)) {
			return null;
		} else if (str.startsWith(FILE_INTERNAL)) {
			dir = new File("/data/data/" + LoggerFactory.getPackageName()
					+ "/files", str.substring(FILE_INTERNAL.length(),
							str.length()));
		} else if (str.startsWith(FILE_EXTERNAL)) {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				internalLogger
				.verbose("properties: external sdcard not mounted, use default log file path");
				dir = new File("/data/data/" + LoggerFactory.getPackageName()
						+ "/files");
			} else {
				final String external = Environment
						.getExternalStorageDirectory().getPath()
						+ "/Android/data/"
						+ LoggerFactory.getPackageName()
						+ "/files";
				dir = new File(external, str.substring(FILE_EXTERNAL.length(),
						str.length()));
			}
		} else {
			dir = new File(str);
		}
		internalLogger.verbose("properties: log file path: %s", dir.getPath());
		return dir;
	}

	private Boolean booleanValueOf(final String str) {
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		if ("true".equalsIgnoreCase(str)) {
			return Boolean.TRUE;
		} else if ("false".equalsIgnoreCase(str)) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}

}
