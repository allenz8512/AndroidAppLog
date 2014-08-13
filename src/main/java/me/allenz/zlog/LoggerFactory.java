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

	private static final String LOGGER_FACTORY_CLASS_NAME = LoggerFactory.class
			.getName();

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
	private static Map<String, LoggerConfig> loggerConfigs;
	private static Context appContext;
	private static CallerResolver callerResolver = new CallerResolver();
	private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();

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
				loggerConfigs = new HashMap<String, LoggerFactory.LoggerConfig>();
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

	/**
	 * 
	 * Parse the logger tag in property value, if the tag is empty we consider
	 * there's no tag for the logger and the logger will use it's class name as
	 * the tag.
	 * <p>
	 * 
	 * There's no obviously limit for the length of android log tag. The size of
	 * 'tag + message' should not be greater than 4073 bytes, and the exceed
	 * bytes won't be written to internal log buffer.
	 * <p>
	 * 
	 * In {@link android.util.Log#isLoggable(String tag, int level)}, the size
	 * of argument 'tag' should be no greater than 23 bytes or we will receive
	 * an exception, simplely not to call this method to avoid it.
	 * <p>
	 * 
	 * Reference:
	 * 
	 * <pre>
	 * http://developer.android.com/reference/android/util/Log.html
	 * http://www.slf4j.org/android/
	 * http://stackoverflow.com/questions/4126815/android-logging-levels
	 * https://github.com/android/platform_frameworks_base/blob/master/core/jni/android_util_Log.cpp
	 * https://github.com/android/platform_bionic/blob/master/libc/include/sys/system_properties.h
	 * https://android.googlesource.com/kernel/common.git/+/android-3.4/drivers/staging/android/logger.h
	 * https://android.googlesource.com/kernel/common.git/+/android-3.4/drivers/staging/android/logger.c
	 * </pre>
	 * 
	 * @param name
	 *            the name of the logger
	 * @param tag
	 *            the tag of the logger
	 * @return If the length of the tag is zero return {@code null}, otherwise
	 *         return the tag.
	 */
	private static String parseTag(final String name, final String tag) {
		return Utils.isEmpty(tag) ? null : tag;
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
		if (loggerConfigs != null) {
			loggerConfigs.clear();
			loggerConfigs = null;
		}
		loggers.clear();
		loggers = null;
	}

	/**
	 * Get the logger of the calling class.
	 * 
	 * @return
	 */
	public static Logger getLogger() {
		synchronized (LoggerFactory.class) {
			checkInitialization();
			Logger logger = null;
			final String caller = getCallerClassName();
			internalLogger.verbose("Caller: %s", caller);
			logger = getDeclaredLogger(caller);
			return logger != null ? logger : getNewLogger(caller);
		}
	}

	private static Logger getDeclaredLogger(final String caller) {
		return loggers.get(caller);
	}

	private static Logger getNewLogger(final String caller) {
		Logger logger = null;
		if (loggerConfigs.containsKey(caller)) {
			logger = createSimpleLogger(caller, loggerConfigs.get(caller));
		} else {
			logger = createInheritParentConfigLogger(caller);
		}
		loggers.put(caller, logger);
		return logger;
	}

	private static Logger createInheritParentConfigLogger(final String caller) {
		boolean parentFound = false;
		Logger logger = null;
		for (int i = caller.lastIndexOf('.'); i >= 0; i = caller.lastIndexOf(
				'.', i - 1)) {
			final String parentPackage = caller.substring(0, i);
			if (loggerConfigs.containsKey(parentPackage)) {
				logger = createSimpleLogger(caller,
						loggerConfigs.get(parentPackage));
				parentFound = true;
				break;
			}
		}
		if (!parentFound) {
			logger = createSimpleLogger(caller, rootLoggerConfig);
		}
		return logger;
	}

	private static Logger createSimpleLogger(final String caller,
			final LoggerConfig loggerConfig) {
		final LogLevel level = loggerConfig.level;
		final int dot = caller.lastIndexOf(".");
		final String className = dot == -1 ? caller : caller.substring(dot + 1);
		final String tag = loggerConfig.tag == null ? className
				: loggerConfig.tag;
		internalLogger.verbose("logger created: [name=%s, level=%s tag=%s]",
				caller, level, tag);
		return new SimpleLogger(level, tag);
	}

	private static String getCallerClassName() {
		final Class<?> caller = callerResolver.getCaller();
		if (caller == null) {
			final StackTraceElement callerStackTrace = getCallerStackTrace();
			return callerStackTrace == null ? null : callerStackTrace
					.getClassName();
		} else {
			return caller.getName();
		}
	}

	private static StackTraceElement getCallerStackTrace() {
		final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		if (stackTrace == null || stackTrace.length <= 0) {
			return null;
		}

		for (int i = 0; i < stackTrace.length; i++) {
			final StackTraceElement stackTraceElement = stackTrace[i];
			if (stackTraceElement.getClassName().equals(
					LOGGER_FACTORY_CLASS_NAME)) {
				return stackTrace[i + 3];
			}
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

	private static final class CallerResolver extends SecurityManager {

		@SuppressWarnings("rawtypes")
		public Class<?> getCaller() {
			final Class[] classContext = getClassContext();
			if (classContext == null || classContext.length <= 0) {
				return null;
			}
			for (int i = 0; i < classContext.length; i++) {
				final Class clazz = classContext[i];
				if (clazz.getName().equals(LOGGER_FACTORY_CLASS_NAME)) {
					return classContext[i + 2];
				}
			}
			return null;
		}
	}
}
