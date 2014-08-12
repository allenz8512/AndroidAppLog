package me.allenz.zlog;

import android.content.Context;

public class LoggerFactory {

	static InternalLogger internalLogger = new InternalLogger(LogLevel.VERBOSE);
	static Context context;
	static LogLevel rootLogLevel;

	static InternalLogger getInternalLogger() {
		return internalLogger;
	}

	public static void init(final Context applicationContext) {
		context = applicationContext;
	}

	public static void destroy() {
		context = null;
	}

	public static Logger getLogger() {
		return null;
	}

	static class LoggerConfig {
		public String name;
		public String tag;
		public LogLevel level;

		public LoggerConfig() {
		}

		public LoggerConfig(final String name, final String tag,
				final LogLevel level) {
			this.name = name;
			this.tag = tag;
			this.level = level;
		}

	}

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
