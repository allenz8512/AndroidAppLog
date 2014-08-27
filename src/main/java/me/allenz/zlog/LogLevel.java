package me.allenz.zlog;

import android.util.Log;

/**
 * Enumeration of log level, providing additional log level 'OFF'.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 * @see android.util.Log
 */
public enum LogLevel {

	/** @see android.util.Log#VERBOSE */
	VERBOSE(Log.VERBOSE),
	/** @see android.util.Log#DEBUG */
	DEBUG(Log.DEBUG),
	/** @see android.util.Log#INFO */
	INFO(Log.INFO),
	/** @see android.util.Log#WARN */
	WARN(Log.WARN),
	/** @see android.util.Log#ERROR */
	ERROR(Log.ERROR),
	/** @see android.util.Log#ASSERT */
	ASSERT(Log.ASSERT),
	/** This log level means all messages will not be printed. */
	OFF(Integer.MAX_VALUE);

	private final int intVal;

	LogLevel(final int intVal) {
		this.intVal = intVal;
	}

	/**
	 * Get the integer value of LogLevel, the value is the same as origin
	 * android log level, except the additonal log level 'OFF'.
	 * 
	 * @return The integer value of log level.
	 */
	public int intValue() {
		return intVal;
	}

	/**
	 * Figure out if this LogLevel instance includes the specified LogLevel. <br>
	 * For Example:<br>
	 * <code>LogLevel.DEBUG.includes(LogLevel.INFO)</code> returns TRUE<br>
	 * <code>LogLevel.DEBUG.includes(LogLevel.DEBUG)</code> returns TRUE<br>
	 * <code>LogLevel.DEBUG.includes(LogLevel.VERBOSE)</code> returns FALSE<br>
	 * 
	 * @param level
	 *            The LogLevel instance to compare
	 * @return If this LogLevel instance includes the specified LogLevel returns
	 *         true, otherwise returns false.
	 */
	public boolean includes(final LogLevel level) {
		return level != null && this.intValue() <= level.intValue();
	}
}
