package me.allenz.androidapplog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Configure {

	public static final LogLevel DEFAULT_ROOT_LOG_LEVEL = LogLevel.VERBOSE;

	private static final long DEFAULT_LOG_FILE_ROLLING_SIZE = 1024 * 1024;

	private static final String ROOT_LOGGER_NAME = "root";

	private boolean debug = true;

	private LogLevel rootLogLevel = DEFAULT_ROOT_LOG_LEVEL;

	private String rootTag = null;

	private boolean rootShowThread = false;

	private List<LoggerConfig> loggerConfigs;

	private boolean handleException = true;

	private boolean useLogCatAppender = true;

	private boolean useFileAppender = false;

	private File logFileDir;

	private long logFileRollingSize = DEFAULT_LOG_FILE_ROLLING_SIZE;

	private boolean useTextViewAppender = false;

	public Configure() {
		loggerConfigs = new ArrayList<LoggerConfig>();
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	public LogLevel getRootLogLevel() {
		return rootLogLevel;
	}

	public void setRootLogLevel(final LogLevel rootLogLevel) {
		this.rootLogLevel = rootLogLevel;
	}

	public String getRootTag() {
		return rootTag;
	}

	public void setRootTag(final String rootTag) {
		this.rootTag = rootTag;
	}

	public boolean isRootShowThread() {
		return rootShowThread;
	}

	public void setRootShowThread(final boolean rootShowThread) {
		this.rootShowThread = rootShowThread;
	}

	public boolean isHandleException() {
		return handleException;
	}

	public void setHandleException(final boolean handleException) {
		this.handleException = handleException;
	}

	public boolean isUseLogCatAppender() {
		return useLogCatAppender;
	}

	public void setUseLogCatAppender(final boolean useLogCatAppender) {
		this.useLogCatAppender = useLogCatAppender;
	}

	public boolean isUseFileAppender() {
		return useFileAppender;
	}

	public void setUseFileAppender(final boolean useFileAppender) {
		this.useFileAppender = useFileAppender;
	}

	public File getLogFileDir() {
		return logFileDir;
	}

	public void setLogFileDir(final File logFileDir) {
		this.logFileDir = logFileDir;
	}

	public long getLogFileRollingSize() {
		return logFileRollingSize;
	}

	public void setLogFileRollingSize(final long logFileRollingSize) {
		this.logFileRollingSize = logFileRollingSize;
	}

	public boolean isUseTextViewAppender() {
		return useTextViewAppender;
	}

	public void setUseTextViewAppender(final boolean useTextViewAppender) {
		this.useTextViewAppender = useTextViewAppender;
	}

	public List<LoggerConfig> getLoggerConfigs() {
		return loggerConfigs;
	}

	public void addLoggerConfig(final String name, final LogLevel level,
			final String tag, final boolean showThreadName) {
		loggerConfigs.add(new LoggerConfig(name, tag, level, showThreadName));
	}

	public static Configure defaultConfigure() {
		final Configure configure = new Configure();
		return configure;
	}

	public static Configure releaseConfigure() {
		final Configure configure = new Configure();
		configure.setDebug(false);
		configure.setRootLogLevel(LogLevel.OFF);
		configure.setUseLogCatAppender(false);
		return configure;
	}

	public void applyConfigure(final Repository repository) {
		if (!debug) {
			LoggerFactory.getInternalLogger().setLogLevel(LogLevel.OFF);
		} else {
			LoggerFactory.getInternalLogger().setLogLevel(
					DEFAULT_ROOT_LOG_LEVEL);
		}
		repository.setRootLoggerConfig(new LoggerConfig(ROOT_LOGGER_NAME,
				rootTag, rootLogLevel, rootShowThread));
		if (handleException) {
			LoggerFactory.enableLoggingUncaughtException(null);
		}
		if (useLogCatAppender) {
			repository.addAppender(new LogCatAppender());
		}
		if (useFileAppender) {
			repository.addAppender(new RollingFileAppender(logFileDir,
					logFileRollingSize));
		}
		if (useTextViewAppender) {
			repository.addAppender(new TextViewAppender());
		}
		for (final LoggerConfig loggerConfig : loggerConfigs) {
			repository.addLoggerConfig(loggerConfig);
		}
	}

}
