package me.allenz.androidapplog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

public class LoggerFactory {

	private static final String CONFIG_FILE_NAME = "aal";

	private static Logger internalLogger = new InternalLogger();

	private static Repository repository = new Repository();

	private static Context appContext;

	private static String packageName;

	private static UncaughtExceptionLogger mUncaughtExceptionLogger;

	static {
		getContext();
		getPackageName();
		checkBuildConfigAndApplyConfigure();
		loadConfigure();
	}

	private LoggerFactory() {
		throw new UnsupportedOperationException();
	}

	static Logger getInternalLogger() {
		return internalLogger;
	}

	static Repository getRepository() {
		return repository;
	}

	static Context getContext() {
		if (appContext == null) {
			try {
				final Class<?> activityThreadClass = LoggerFactory.class
						.getClassLoader().loadClass(
								"android.app.ActivityThread");
				final Method currentActivityThread = activityThreadClass
						.getDeclaredMethod("currentActivityThread");
				final Object activityThread = currentActivityThread
						.invoke(null);
				final Method getApplication = activityThreadClass
						.getDeclaredMethod("getApplication");
				final Application application = (Application) getApplication
						.invoke(activityThread);
				appContext = application.getApplicationContext();
			} catch (final Exception e) {
			}
		}
		return appContext;
	}

	static String getPackageName() {
		if (packageName == null) {
			try {
				final Class<?> activityThreadClass = LoggerFactory.class
						.getClassLoader().loadClass(
								"android.app.ActivityThread");
				final Method currentPackageName = activityThreadClass
						.getDeclaredMethod("currentPackageName");
				packageName = (String) currentPackageName.invoke(null);
			} catch (final Exception e) {
				if (appContext != null) {
					packageName = appContext.getPackageName();
				}
			}
		}
		return packageName;
	}

	static void loadConfigure() {
		final Properties properties = readProperties(readPropertiesFileFromClasspath());
		if (properties != null) {
			applyProperties(properties);
		}
	}

	private static void applyProperties(final Properties properties) {
		final Configure configure = (new PropertiesParser(properties)).parse();
		repository.setConfigure(configure);
	}

	private static void checkBuildConfigAndApplyConfigure() {
		final boolean underDevelopment = ReflectUtils
				.booleanReflectStaticFieldValue(packageName + ".BuildConfig",
						"DEBUG", false);
		if (underDevelopment) {
			repository.setConfigure(Configure.defaultConfigure());
		} else {
			repository.setConfigure(Configure.releaseConfigure());
		}

	}

	private static InputStream readPropertiesFileFromClasspath() {
		final String filename = CONFIG_FILE_NAME + ".properties";
		LoggerFactory.class.getClassLoader();
		InputStream in = LoggerFactory.class.getClassLoader()
				.getResourceAsStream("assets/" + filename);
		if (in != null) {
			internalLogger.verbose("found %s.properties in assets",
					CONFIG_FILE_NAME);
		} else {
			in = LoggerFactory.class.getClassLoader().getResourceAsStream(
					"res/raw/" + filename);
			if (in != null) {
				internalLogger.verbose("found %s.properties in res/raw",
						CONFIG_FILE_NAME);
			}
		}
		return in;
	}

	private static Properties readProperties(final InputStream in) {
		if (in == null) {
			internalLogger.verbose("%s.properties not found", CONFIG_FILE_NAME);
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

	public static Logger getLogger() {
		return getLogger(ReflectUtils.getCallerClassName(LoggerFactory.class
				.getName()));
	}

	public static Logger getLogger(final String className) {
		synchronized (LoggerFactory.class) {
			internalLogger.verbose("Caller: %s", className);
			final Logger logger = getDeclaredLogger(className);
			return logger != null ? logger : createNewLogger(className);
		}
	}

	private static Logger getDeclaredLogger(final String caller) {
		return repository.getLogger(caller);
	}

	private static Logger createNewLogger(final String caller) {
		Logger logger = null;
		final LoggerConfig loggerConfig = repository.getLoggerConfig(caller);
		if (loggerConfig != null) {
			logger = createAppenderSupportLogger(caller, loggerConfig);
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
				logger = createAppenderSupportLogger(caller, loggerConfig);
				parentFound = true;
				break;
			}
		}
		if (!parentFound) {
			logger = createAppenderSupportLogger(caller,
					repository.getRootLoggerConfig());
		}
		return logger;
	}

	private static Logger createAppenderSupportLogger(final String caller,
			final LoggerConfig loggerConfig) {
		final int dot = caller.lastIndexOf(".");
		final String className = dot == -1 ? caller : caller.substring(dot + 1);
		final String tag = loggerConfig.getTag() == null ? className
				: loggerConfig.getTag();
		final Logger logger = new AppenderSupportLogger(className,
				loggerConfig.getLevel(), tag, loggerConfig.isShowThreadName());
		internalLogger.verbose("logger created: %s", logger);
		return logger;
	}

	public static void bindTextView(final TextView textView) {
		final List<Appender> appenders = repository.getAppenders();
		for (final Appender appender : appenders) {
			if (appender instanceof TextViewAppender) {
				((TextViewAppender) appender).bind(textView);
				return;
			}
		}
	}

	public static void unbindTextView() {
		final List<Appender> appenders = repository.getAppenders();
		for (final Appender appender : appenders) {
			if (appender instanceof TextViewAppender) {
				((TextViewAppender) appender).unbind();
				return;
			}
		}
	}

	public static TextView createLogTextView(final Activity activity) {
		final FrameLayout root = (FrameLayout) activity.getWindow()
				.getDecorView().findViewById(android.R.id.content);
		final TextView textView = new LogTextView(activity);
		root.addView(textView);
		root.bringChildToFront(textView);
		return textView;
	}

	public static void enableLoggingUncaughtException(
			final UncaughtExceptionHandler customHandler) {
		if (customHandler != null) {
			mUncaughtExceptionLogger = new UncaughtExceptionLogger(
					customHandler);
		} else {
			mUncaughtExceptionLogger = new UncaughtExceptionLogger();
		}
		Thread.setDefaultUncaughtExceptionHandler(mUncaughtExceptionLogger);
	}

	public static void disableLoggingUncaughtException() {
		if (mUncaughtExceptionLogger != null) {
			final UncaughtExceptionHandler customHandler = mUncaughtExceptionLogger
					.getDefaultExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(customHandler);
		}
	}
}
