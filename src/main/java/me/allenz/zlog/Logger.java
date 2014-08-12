package me.allenz.zlog;

/**
 * Logger interface for logging.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 */
public interface Logger {

	/**
	 * Log a message at the VERBOSE level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void verbose(String format, Object... args);

	/**
	 * Log an exception (throwable) at the VERBOSE level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void verbose(Throwable t);

	/**
	 * Log an exception (throwable) at the VERBOSE level with a message
	 * according to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void verbose(Throwable t, String format, Object... args);

	/**
	 * Log a message at the DEBUG level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void debug(String format, Object... args);

	/**
	 * Log an exception (throwable) at the DEBUG level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void debug(Throwable t);

	/**
	 * Log an exception (throwable) at the DEBUG level with a message according
	 * to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void debug(Throwable t, String format, Object... args);

	/**
	 * Log a message at the INFO level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void info(String format, Object... args);

	/**
	 * Log an exception (throwable) at the INFO level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void info(Throwable t);

	/**
	 * Log an exception (throwable) at the INFO level with a message according
	 * to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void info(Throwable t, String format, Object... args);

	/**
	 * Log a message at the WARN level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void warn(String format, Object... args);

	/**
	 * Log an exception (throwable) at the WARN level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void warn(Throwable t);

	/**
	 * Log an exception (throwable) at the WARN level with a message according
	 * to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void warn(Throwable t, String format, Object... args);

	/**
	 * Log a message at the ERROR level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void error(String format, Object... args);

	/**
	 * Log an exception (throwable) at the ERROR level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void error(Throwable t);

	/**
	 * Log an exception (throwable) at the ERROR level with a message according
	 * to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void error(Throwable t, String format, Object... args);

	/**
	 * Log a message at the ASSERT level according to the specified format and
	 * args.
	 * 
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void wtf(String format, Object... args);

	/**
	 * Log an exception (throwable) at the ASSERT level.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @since 0.1.0-RELEASE
	 */
	void wtf(Throwable t);

	/**
	 * Log an exception (throwable) at the ASSERT level with a message according
	 * to the specified format and args.
	 * 
	 * @param t
	 *            the exception (throwable) to log
	 * @param format
	 *            the format string
	 * @param args
	 *            a list of args
	 * @since 0.1.0-RELEASE
	 */
	void wtf(Throwable t, String format, Object... args);
}
