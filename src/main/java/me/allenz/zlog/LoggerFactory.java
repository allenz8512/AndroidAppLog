package me.allenz.zlog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

/**
 * The logger factory.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 */
public class LoggerFactory {

	private static final Logger internalLogger = ConfigureRepository
			.getInternalLogger();

	private static final String LOGGER_FACTORY_CLASS_NAME = LoggerFactory.class
			.getName();
	private static final String CONFIG_FILE_NAME = "zlog";

	private static Context appContext;
	private static ConfigureRepository repository = new ConfigureRepository();
	private static CallerResolver callerResolver = new CallerResolver();

	/**
	 * Initialize zlog.
	 * 
	 * @param context
	 *            the applicationContext of the app
	 */
	public static void init(final Context context) {
		init(context, LogLevel.WARN);// by default, prints log that level equals
										// or higher than WARN
	}

	/**
	 * Initialize zlog.
	 * 
	 * @param context
	 *            the applicationContext of the app
	 * @param releaseRootLoggerLevel
	 *            log level of the root logger in release mode
	 */
	public static void init(final Context context,
			final LogLevel releaseRootLoggerLevel) {
		synchronized (LoggerFactory.class) {
			if (context == null) {
				internalLogger
						.warn("zlog can not read configure file because the context is null,"
								+ " zlog will run at safe mode");
				return;
			}
			appContext = context.getApplicationContext();
			// try to setup by running environment before read config
			checkEnvironmentAndSetup(releaseRootLoggerLevel);
			// try to load config
			final Properties configProperties = locateAndLoadProperties();
			if (configProperties != null) {
				new PropertyConfigurator(configProperties)
						.doConfigure(repository);
			}
		}
	}

	/**
	 * Locate the config properties file, search assets folder, res/raw in
	 * order, read the file after found.
	 * 
	 * @return the properties of config file, if file not found return
	 *         {@code null}
	 */
	private static Properties locateAndLoadProperties() {
		InputStream in = null;
		final String filename = CONFIG_FILE_NAME + ".properties";
		try {
			// search assets
			in = appContext.getAssets().open(filename);
		} catch (final IOException e) {
		}
		if (in == null) {
			// search res/raw
			final String packageName = appContext.getPackageName();
			try {
				final int id = Utils.intReflectStaticFieldValue(packageName
						+ ".R$raw", CONFIG_FILE_NAME, -1);
				if (id != -1) {
					in = appContext.getResources().openRawResource(id);
				}
			} catch (final Exception e) {
			}
		}
		if (in == null) {
			return null;
		}
		// load the input steam into properties
		final Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (final IOException e) {
			return null;
		} finally {
			try {
				in.close();
			} catch (final IOException e) {
			}
		}
		return properties;
	}

	/**
	 * Read the value of 'package.BuildConfig.DEBUG', this value is {@code true}
	 * in developing and is {@code false} after being packaged(release mode). In
	 * generally, we want to hide the low level log message in release mode.
	 * 
	 * @param level
	 *            log level of the root logger in release mode
	 */
	private static void checkEnvironmentAndSetup(final LogLevel level) {
		final String packageName = appContext.getPackageName();
		final boolean underDevelopment = Utils.booleanReflectStaticFieldValue(
				packageName + "BuildConfig", "DEBUG", true);
		if (!underDevelopment) {// if the app is released
			// shutdown internal logging
			ConfigureRepository.setInternalLogLevel(LogLevel.OFF);
			// reset the log level of root logger
			repository.setRootLoggerConfig(new LoggerConfig("root", null,
					level, false));
		}
	}

	/**
	 * Destroy zlog.
	 */
	public static void destroy() {
		appContext = null;
		callerResolver = null;
		repository.resetToDefault();
		repository = null;
	}

	/**
	 * Get the logger of the calling class.
	 * 
	 * @return Logger
	 */
	public static Logger getLogger() {
		synchronized (LoggerFactory.class) {
			Logger logger = null;
			final String caller = getCallerClassName();
			internalLogger.verbose("Caller: %s", caller);
			logger = getDeclaredLogger(caller);
			return logger != null ? logger : getNewLogger(caller);
		}
	}

	private static Logger getDeclaredLogger(final String caller) {
		return repository.getLogger(caller);
	}

	private static Logger getNewLogger(final String caller) {
		Logger logger = null;
		final LoggerConfig loggerConfig = repository.getLoggerConfig(caller);
		if (loggerConfig != null) {
			logger = createSimpleLogger(caller, loggerConfig);
		} else {
			logger = createInheritParentConfigLogger(caller);
		}
		repository.addLogger(caller, logger);
		return logger;
	}

	private static Logger createInheritParentConfigLogger(final String caller) {
		boolean parentFound = false;
		Logger logger = null;
		for (int i = caller.lastIndexOf('.'); i >= 0; i = caller.lastIndexOf(
				'.', i - 1)) {
			final String parentPackage = caller.substring(0, i);
			final LoggerConfig loggerConfig = repository
					.getLoggerConfig(parentPackage);
			if (loggerConfig != null) {
				logger = createSimpleLogger(caller, loggerConfig);
				parentFound = true;
				break;
			}
		}
		if (!parentFound) {
			logger = createSimpleLogger(caller,
					repository.getRootLoggerConfig());
		}
		return logger;
	}

	private static Logger createSimpleLogger(final String caller,
			final LoggerConfig loggerConfig) {
		final int dot = caller.lastIndexOf(".");
		final String className = dot == -1 ? caller : caller.substring(dot + 1);
		final String tag = loggerConfig.getTag() == null ? className
				: loggerConfig.getTag();
		final Logger logger = new SimpleLogger(caller, tag,
				loggerConfig.getLevel(), loggerConfig.isThread());
		internalLogger.verbose("logger created: %s", logger);
		return logger;
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
