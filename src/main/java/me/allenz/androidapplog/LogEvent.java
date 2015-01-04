package me.allenz.androidapplog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEvent {

	private static final String DELIMITER = "    ";

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private long time;

	private LogLevel level;

	private String tag;

	private String message;

	public LogEvent(final LogLevel level, final String tag, final String message) {
		this(System.currentTimeMillis(), level, tag, message);
	}

	public LogEvent(final long millis, final LogLevel level, final String tag,
			final String message) {
		this.time = millis;
		this.level = level;
		this.tag = tag;
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getTag() {
		return tag;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		final StringBuilder sb = new StringBuilder();
		sb.append(formatter.format(new Date(time))).append(DELIMITER);
		sb.append(level.toString()).append(DELIMITER);
		sb.append(tag).append(DELIMITER);
		sb.append(message);
		return sb.toString();
	}
}
