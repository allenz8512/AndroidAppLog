package me.allenz.zlog;

import java.text.SimpleDateFormat;
import java.util.Date;

class LogEvent {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss.SSS");

    private long time;

    private LogLevel level;

    private String tag;

    private String message;

    public LogEvent(final LogLevel level,
                    final String tag, final String message){
        this(System.currentTimeMillis(), level, tag, message);
    }

    public LogEvent(final long millis, final LogLevel level,
                    final String tag, final String message){
        this.time = millis;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(DATE_FORMAT.format(new Date(time))).append("\t");
        sb.append(level.toString()).append("\t");
        sb.append(tag).append("\t");
        sb.append(message).append("\n");
        return sb.toString();
    }
}
