package me.allenz.androidapplog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {

	private LoggerConfig rootLoggerConfig;

	private Map<String, LoggerConfig> loggerConfigs;

	private Map<String, Logger> loggers;

	private List<Appender> appenders;

	public Repository() {
		loggerConfigs = new HashMap<String, LoggerConfig>();
		loggers = new HashMap<String, Logger>();
		appenders = new ArrayList<Appender>();
	}

	public Repository(final Configure configure) {
		this();
		setConfigure(configure);
	}

	public void setConfigure(final Configure configure) {
		clear();
		configure.applyConfigure(this);
		startAppenders();
	}

	private void clear() {
		LoggerFactory.disableLoggingUncaughtException();
		rootLoggerConfig = null;
		loggerConfigs.clear();
		loggers.clear();
		for (final Appender appender : appenders) {
			appender.stop();
		}
		appenders.clear();
	}

	private void startAppenders() {
		for (final Appender appender : appenders) {
			appender.start();
		}
	}

	public LoggerConfig getRootLoggerConfig() {
		return rootLoggerConfig;
	}

	public void setRootLoggerConfig(final LoggerConfig loggerConfig) {
		this.rootLoggerConfig = loggerConfig;
	}

	public LoggerConfig getLoggerConfig(final String name) {
		return loggerConfigs.get(name);
	}

	public void addLoggerConfig(final LoggerConfig loggerConfig) {
		loggerConfigs.put(loggerConfig.getName(), loggerConfig);
	}

	public Logger getLogger(final String name) {
		return loggers.get(name);
	}

	public void addLogger(final String name, final Logger logger) {
		loggers.put(name, logger);
	}

	public List<Appender> getAppenders() {
		return appenders;
	}

	public void addAppender(final Appender appender) {
		appenders.add(appender);
	}

}
