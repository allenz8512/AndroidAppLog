package me.allenz.androidapplog;

import android.util.Log;

public abstract class AbstractLogger implements Logger {

	protected static final String STRING_NULL = "null";

	protected String name;

	protected LogLevel level;

	protected String tag;

	protected boolean showThreadName;

	public AbstractLogger(final String name) {
		this(name, Configure.DEFAULT_ROOT_LOG_LEVEL, null, false);
	}

	public AbstractLogger(final String name, final LogLevel level,
			final String tag, final boolean showThreadName) {
		this.name = name;
		this.level = level;
		this.tag = tag;
		this.showThreadName = showThreadName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LogLevel getLogLevel() {
		return level;
	}

	@Override
	public void setLogLevel(final LogLevel level) {
		this.level = level;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public void setTag(final String tag) {
		this.tag = tag;
	}

	@Override
	public boolean isShowThreadName() {
		return showThreadName;
	}

	@Override
	public void setShowThreadName(final boolean show) {
		showThreadName = show;
	}

	@Override
	public void verbose(final String format, final Object... args) {
		println(LogLevel.VERBOSE, null, format, args);
	}

	@Override
	public void verbose(final Throwable t) {
		println(LogLevel.VERBOSE, t, null);
	}

	@Override
	public void verbose(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.VERBOSE, t, null);
	}

	@Override
	public void verbose(final String message) {
		println(LogLevel.VERBOSE, null, message);
	}

	@Override
	public void verbose(final Throwable t, final String message) {
		println(LogLevel.VERBOSE, t, message);
	}

	@Override
	public void verbose(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.VERBOSE, null, message);
	}

	@Override
	public void debug(final String format, final Object... args) {
		println(LogLevel.DEBUG, null, format, args);
	}

	@Override
	public void debug(final Throwable t) {
		println(LogLevel.DEBUG, t, null);
	}

	@Override
	public void debug(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.DEBUG, t, format, args);
	}

	@Override
	public void debug(final String message) {
		println(LogLevel.DEBUG, null, message);
	}

	@Override
	public void debug(final Throwable t, final String message) {
		println(LogLevel.DEBUG, t, message);
	}

	@Override
	public void debug(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.DEBUG, null, message);
	}

	@Override
	public void info(final String format, final Object... args) {
		println(LogLevel.INFO, null, format, args);
	}

	@Override
	public void info(final Throwable t) {
		println(LogLevel.INFO, t, null);
	}

	@Override
	public void info(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.INFO, t, format, args);
	}

	@Override
	public void info(final String message) {
		println(LogLevel.INFO, null, message);
	}

	@Override
	public void info(final Throwable t, final String message) {
		println(LogLevel.INFO, t, message);
	}

	@Override
	public void info(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.INFO, null, message);
	}

	@Override
	public void warn(final String format, final Object... args) {
		println(LogLevel.WARN, null, format, args);
	}

	@Override
	public void warn(final Throwable t) {
		println(LogLevel.WARN, t, null);
	}

	@Override
	public void warn(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.WARN, t, format, args);
	}

	@Override
	public void warn(final String message) {
		println(LogLevel.WARN, null, message);
	}

	@Override
	public void warn(final Throwable t, final String message) {
		println(LogLevel.WARN, t, message);
	}

	@Override
	public void warn(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.WARN, null, message);
	}

	@Override
	public void error(final String format, final Object... args) {
		println(LogLevel.ERROR, null, format, args);
	}

	@Override
	public void error(final Throwable t) {
		println(LogLevel.ERROR, t, null);
	}

	@Override
	public void error(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.ERROR, t, format, args);
	}

	@Override
	public void error(final String message) {
		println(LogLevel.ERROR, null, message);
	}

	@Override
	public void error(final Throwable t, final String message) {
		println(LogLevel.ERROR, t, message);
	}

	@Override
	public void error(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.ERROR, null, message);
	}

	@Override
	public void wtf(final String format, final Object... args) {
		println(LogLevel.ASSERT, null, format, args);
	}

	@Override
	public void wtf(final Throwable t) {
		println(LogLevel.ASSERT, t, null);
	}

	@Override
	public void wtf(final Throwable t, final String format,
			final Object... args) {
		println(LogLevel.ASSERT, t, format, args);
	}

	@Override
	public void wtf(final String message) {
		println(LogLevel.ASSERT, null, message);
	}

	@Override
	public void wtf(final Throwable t, final String message) {
		println(LogLevel.ASSERT, t, message);
	}

	@Override
	public void wtf(final Object obj) {
		final String message = obj == null ? STRING_NULL : obj.toString();
		println(LogLevel.ASSERT, null, message);
	}

	protected LogEvent buildLogEvent(final LogLevel level, final Throwable t,
			final String format, final Object... args) {
		String message = null;
		if (format != null && format.length() > 0) {
			message = (args != null && args.length > 0) ? String.format(format,
					args) : format;
		}
		if (t != null) {
			message = message != null ? message + "\n"
					+ Log.getStackTraceString(t) : Log.getStackTraceString(t);
		}
		String tag;
		if (showThreadName) {
			final StringBuilder sb = new StringBuilder();
			sb.append("[").append(Thread.currentThread().getName()).append("]")
					.append(this.tag);
			tag = sb.toString();
		} else {
			tag = this.tag;
		}
		return new LogEvent(level, tag, message);
	}

	protected abstract void println(final LogLevel level, final Throwable t,
			final String format, final Object... args);

	@Override
	public String toString() {
		return "Logger [name=" + name + ", level=" + level + ", tag=" + tag
				+ ", showThreadName=" + showThreadName + "]";
	}

}
