package me.allenz.zlog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import android.content.Context;

/**
 * The logger factory.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 */
public class LoggerFactory {

	private static final String CONFIG_FILE_NAME = "zlog";
	private static final String ROOT_PREFIX = "root";
	private static final String LOGGER_PREFIX = "logger.";
	private static final String PACKAGE_NAME_PATTERN = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";

	private static final String DEFAULT_ROOT_TAG = null;
	private static final LogLevel DEFAULT_ROOT_LOG_LEVEL = LogLevel.DEBUG;

	// prints all zlog debug messages by default
	private static InternalLogger internalLogger = new InternalLogger(
			LogLevel.VERBOSE);
	private static boolean initNormally = false;
	private static LoggerConfig rootLoggerConfig = new LoggerConfig(
			ROOT_PREFIX, DEFAULT_ROOT_TAG, DEFAULT_ROOT_LOG_LEVEL);
	private static Map<String, LoggerConfig> loggerConfigs = new HashMap<String, LoggerConfig>();
	private static Context appContext;

	/**
	 * Get the logger for zlog itself.
	 * 
	 * @return InternalLogger
	 */
	static InternalLogger getInternalLogger() {
		return internalLogger;
	}

	/**
	 * Get the root logger config.
	 * 
	 * @return LoggerConfig
	 */
	static LoggerConfig getRootLoggerConfig() {
		return rootLoggerConfig;
	}

	/**
	 * Initialize zlog.
	 * 
	 * @param context
	 *            the applicationContext of the app
	 */
	public static void init(final Context context) {
		synchronized (LoggerFactory.class) {
			if (initNormally) {
				return;
			}
			if (context == null) {
				internalLogger
						.verbose("zlog initialize failed: context is null.");
				return;
			}
			appContext = context.getApplicationContext();
			// try to load config
			final InputStream is = locateProperties();
			if (is != null) {
				try {
					loadProperties(is);
					initNormally = true;
				} catch (final Exception e) {
					internalLogger.verbose(e, "load config failed.");
				} finally {
					try {
						is.close();
					} catch (final IOException e) {
					}
				}
			} else {// there's no config properties, try to judge running
					// environment by BuildConfig.DEBUG and setup

			}
			checkInitialization();
		}
	}

	/**
	 * Locate the config properties file, search assets folder, res/raw in
	 * order, read the steam of the file after found.
	 * 
	 * @return the stream of config file, if file not found return {@code null}
	 */
	private static InputStream locateProperties() {
		InputStream is = null;
		final String filename = CONFIG_FILE_NAME + ".properties";
		try {
			is = appContext.getAssets().open(filename);
			internalLogger.verbose("found %s in assets", filename);
		} catch (final IOException e) {
			internalLogger.verbose("can not found %s in assets", filename);
			final String packageName = appContext.getPackageName();
			try {
				final int id = Utils.intReflectStaticFieldValue(packageName
						+ ".R$raw", CONFIG_FILE_NAME, -1);
				if (id != -1) {
					is = appContext.getResources().openRawResource(id);
					internalLogger.verbose("found %s in res/raw", filename);
				}
			} catch (final Exception e2) {
				internalLogger.verbose("can not found %s in res/raw", filename);
			}
		}
		return is;
	}

	private static void loadProperties(final InputStream is) throws Exception {
		final Properties properties = new Properties();
		properties.load(is);
		if (!properties.propertyNames().hasMoreElements()) {
			throw new IOException("config file has no elements");
		}
		loadRootLoggerConfig(properties);
		loadLoggerConfigs(properties);
	}

	private static void loadRootLoggerConfig(final Properties properties) {
		final String value = (String) properties.get(ROOT_PREFIX);
		if (value == null) {
			return;
		}
		final LoggerConfig loggerConfig = parseLoggerConfig(ROOT_PREFIX, value);
		if (loggerConfig == null) {
			internalLogger
					.verbose(
							"root logger setting not specified in properties file, use default: %s",
							rootLoggerConfig.toString());
		} else {
			rootLoggerConfig = loggerConfig;
			internalLogger.verbose("setup root logger: %s",
					loggerConfig.toString());
		}
	}

	private static void loadLoggerConfigs(final Properties properties) {
		for (final Enumeration<?> names = properties.propertyNames(); names
				.hasMoreElements();) {
			final String propertyName = (String) names.nextElement();
			if (propertyName.startsWith(LOGGER_PREFIX)) {
				final String name = propertyName.substring(
						LOGGER_PREFIX.length(), propertyName.length());
				if (name.matches(PACKAGE_NAME_PATTERN)) {
					final String propertyValue = properties
							.getProperty(propertyName);
					final LoggerConfig loggerConfig = parseLoggerConfig(name,
							propertyValue);
					if (loggerConfig != null) {
						internalLogger.verbose("setup logger '%s':%s", name,
								loggerConfig.toString());
						loggerConfigs.put(name, loggerConfig);
					}
				} else {
					internalLogger
							.verbose(
									"name '%s' is illegal, it should be package or class fullname, skip",
									name);
				}
			}
		}
	}

	static LoggerConfig parseLoggerConfig(final String name,
			final String propertyValue) {
		if (Utils.isEmpty(propertyValue)) {
			internalLogger.verbose("logger %s property value is empty, skip",
					name);
			return null;
		}
		String levelStr, tagStr = null;
		final int comma = propertyValue.indexOf(",");
		if (comma == -1) {
			levelStr = propertyValue;
		} else {
			levelStr = propertyValue.substring(0, comma);
			tagStr = propertyValue.substring(comma + 1);
		}
		final LogLevel level;
		final String tag;
		if (Utils.isEmpty(levelStr)) {
			return null;
		} else {
			level = parseLogLevel(levelStr);
			if (level == null) {
				internalLogger.verbose("logger %s level '%s' is illegal, skip",
						name, levelStr);
				return null;
			}
		}
		tag = parseTag(name, tagStr);
		return new LoggerConfig(name, tag, level);
	}

	private static LogLevel parseLogLevel(final String level) {
		try {
			return LogLevel.valueOf(level.toUpperCase(Locale.ENGLISH));
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}

	private static String parseTag(final String name, final String tag) {
		final int length = tag.length();
		if (length == 0) {
			return null;
		} else if (length <= 23) {
			return tag;
		} else {
			final String legalTag = tag.substring(0, 23);
			internalLogger.verbose(
					"logger %s tag '%s' is too long, cut to '%s'", name, tag,
					legalTag);
			return legalTag;
		}
	}

	/**
	 * Check if zlog found and loaded logger config successfully, if not we
	 * print a warnning message.
	 */
	private static void checkInitialization() {
		if (!initNormally) {
			internalLogger
					.verbose("zlog did not initialized normally, run in default mode.");
			initNormally = true;
		}
	}

	/**
	 * Destroy zlog.
	 */
	public static void destroy() {
		appContext = null;
		loggerConfigs.clear();
	}

	/**
	 * Get the logger of the calling class.
	 * 
	 * @return
	 */
	public static Logger getLogger() {
		synchronized (LoggerFactory.class) {
			checkInitialization();

		}
		return null;
	}

	/**
	 * Holder for every logger property pair.
	 * 
	 * @author Allenz
	 * @since 0.1.0-RELEASE
	 */
	private static class LoggerConfig {
		public String name;
		public String tag;
		public LogLevel level;

		public LoggerConfig(final String name, final String tag,
				final LogLevel level) {
			this.name = name;
			this.tag = tag;
			this.level = level;
		}

		@Override
		public String toString() {
			return "[name=" + name + ", tag=" + tag + ", level=" + level + "]";
		}

	}

	/**
	 * Logger for zlog itself.
	 * 
	 * @author Allenz
	 * @since 0.1.0-RELEASE
	 */
	static class InternalLogger extends SimpleLogger {

		static final String DEFAULT_INTERNAL_LOGGER_TAG = "zlog";

		public InternalLogger(final LogLevel level) {
			super(level, DEFAULT_INTERNAL_LOGGER_TAG);
		}

		public void setLogLevel(final LogLevel level) {
			this.level = level;
		}

	}
}
