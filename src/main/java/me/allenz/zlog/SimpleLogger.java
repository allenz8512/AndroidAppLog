package me.allenz.zlog;

import android.util.Log;

/**
 * A Simple implementation for Logger that prints all messages to Logcat.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 * @see me.allenz.zlog.Logger
 * @see android.util.Log
 */
class SimpleLogger extends LoggerConfig implements Logger {

	/**
	 * Create a new SimpleLogger instance.
	 * 
	 * @param name
	 *            class fullname of logger user
	 * @param level
	 *            the LogLevel of the logger
	 * @param tag
	 *            the tag of the logger
	 * @param thread
	 *            if true, shows thread name as a prefix of the tag
	 * @since 0.2.0-RELEASE
	 */
	public SimpleLogger(final String name, final String tag,
			final LogLevel level, final boolean thread) {
		super(name, tag, level, thread);
	}

	/**
	 * Internal method that prints log message.
	 * 
	 * @param level
	 *            the LogLevel of the log message
	 * @param t
	 *            a throwable(exception) object, can be {@code null}
	 * @param format
	 *            a format string of the log message, can be {@code null}.
	 * @param args
	 *            an array of arguments, can be {@code null}
	 * @since 0.1.0-RELEASE
	 */
	protected void println(final LogLevel level, final Throwable t,
			final String format, final Object... args) {
		if (this.level.includes(level) && (t != null || format != null)) {
			String message = null;
			if (format != null && format.length() > 0) {
				message = (args != null && args.length > 0) ? String.format(
						format, args) : format;
			}
			if (t != null) {
				message = message != null ? message + "\n"
						+ Log.getStackTraceString(t) : Log
						.getStackTraceString(t);
			}
			if (thread) {
				final StringBuilder sb = new StringBuilder();
				sb.append("[").append(Thread.currentThread().getName())
						.append("]").append(tag);
				Log.println(level.intValue(), sb.toString(), message);
			} else {
				Log.println(level.intValue(), tag, message);
			}
		}
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

}
