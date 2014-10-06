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
	private static final LogLevel DEFAULT_RELEASE_LOG_LEVEL = LogLevel.WARN;
	private static final String CONFIG_FILE_NAME = "zlog";

	private static Context appContext;
	private static ConfigureRepository repository = new ConfigureRepository();
	private static boolean tryToloadPropertiesFromClasspath = false;
	private static boolean loadPropertiesSuccess = false;
	private static String packageName = null;

	/**
	 * Initialize zlog.
	 * 
	 * @param context
	 *            the applicationContext of the app
	 * @since 0.1.0-RELEASE
	 */
	public static void init(final Context context) {
		init(context, DEFAULT_RELEASE_LOG_LEVEL);
	}

	/**
	 * Initialize zlog.
	 * 
	 * @param context
	 *            the applicationContext of the app
	 * @param releaseRootLoggerLevel
	 *            log level of the root logger in release mode
	 * @since 0.2.0-RELEASE
	 */
	public static void init(final Context context,
			final LogLevel releaseRootLoggerLevel) {
		synchronized (LoggerFactory.class) {
			if (loadPropertiesSuccess) {
				return;
			}
			if (context == null) {
				internalLogger
						.warn("zlog can not read configure file because the context is null,"
								+ " zlog will run in safe mode");
				return;
			}
			appContext = context.getApplicationContext();
			// try to setup by running environment before read config
			checkEnvironmentAndSetup(releaseRootLoggerLevel);
			// try to load config
			final Properties configProperties = locateAndLoadProperties();
			if (configProperties != null) {
				parseProperties(configProperties);
			}
		}
	}

	private static void parseProperties(final Properties configProperties) {
		new PropertyConfigurator(packageName, configProperties)
				.doConfigure(repository);
		loadPropertiesSuccess = true;
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
			internalLogger.verbose("find %s.properties in assets",
					CONFIG_FILE_NAME);
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
					internalLogger.verbose("find %s.properties res/raw",
							CONFIG_FILE_NAME);
				}
			} catch (final Exception e) {
			}
		}
		return loadProperties(in);
	}

	private static InputStream readConfiguresFromClasspath() {
		tryToloadPropertiesFromClasspath = true;
		final String filename = CONFIG_FILE_NAME + ".properties";
		LoggerFactory.class.getClassLoader();
		// search assets
		InputStream in = LoggerFactory.class.getClassLoader()
				.getResourceAsStream("assets/" + filename);
		if (in != null) {
			internalLogger.verbose("find %s.properties in assets",
					CONFIG_FILE_NAME);
		} else {
			// search res/raw
			in = LoggerFactory.class.getClassLoader().getResourceAsStream(
					"res/raw/" + filename);
			if (in != null) {
				internalLogger.verbose("find %s.properties in res/raw",
						CONFIG_FILE_NAME);
			}
		}
		return in;
	}

	private static Properties loadProperties(final InputStream in) {
		if (in == null) {
			internalLogger.verbose(
					"no %s.properties found, zlog will run in safe mode",
					CONFIG_FILE_NAME);
			return null;
		}
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
		if (appContext != null) {
			packageName = appContext.getPackageName();
		} else {
			packageName = getPackageNameFromAndroidManifest();
		}
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

	private static String getPackageNameFromAndroidManifest() {
		// TODO Get package name from 'AndroidManifest.xml' without using
		// ApplicationContext
		final InputStream in = LoggerFactory.class.getClassLoader()
				.getResourceAsStream("AndroidManifest.xml");
		return null;
	}

	/**
	 * Destroy zlog.
	 * 
	 * @since 0.1.0-RELEASE
	 */
	@Deprecated
	public static void destroy() {
		appContext = null;
		repository.getLogWriter().stop();
		repository = null;
	}

	/**
	 * Get the logger of the calling class.
	 * 
	 * @return logger of the calling class
	 * @since 0.1.0-RELEASE
	 */
	public static Logger getLogger() {
		return getLogger(Utils.getCallerClassName(LOGGER_FACTORY_CLASS_NAME, 1));
	}

	/**
	 * Get the logger of specify class name.
	 * 
	 * @param className
	 *            the name of class
	 * @return logger of the class name
	 * @since 0.4.0
	 */
	public static Logger getLogger(final String className) {
		synchronized (LoggerFactory.class) {
			if (!loadPropertiesSuccess && !tryToloadPropertiesFromClasspath) {
				// try to load configures if have not call init() method first
				checkEnvironmentAndSetup(DEFAULT_RELEASE_LOG_LEVEL);
				final Properties configProperties = loadProperties(readConfiguresFromClasspath());
				if (configProperties != null) {
					parseProperties(configProperties);
				}
			}
			internalLogger.verbose("Caller: %s", className);
			final Logger logger = getDeclaredLogger(className);
			return logger != null ? logger : getNewLogger(className);
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
				loggerConfig.getLevel(), loggerConfig.isThread(),
				repository.getLogWriter());
		internalLogger.verbose("logger created: %s", logger);
		return logger;
	}
}
