package me.allenz.androidapplog;

import android.util.Log;

public class InternalLogger extends AbstractLogger {

    private static final LogLevel DEFAULT_INTERNAL_LOG_LEVEL = LogLevel.VERBOSE;

    private static final String TAG = "aal";

    public InternalLogger(){
        super(TAG, DEFAULT_INTERNAL_LOG_LEVEL, TAG, false);
    }

    @Override
    protected void println(final LogLevel level, final Throwable t, final String format, final Object... args) {
        if (this.level.includes(level) &&
            (t != null || format != null)) {
            final LogEvent event = buildLogEvent(level, t, format, args);
            Log.println(event.getLevel().intValue(), event.getTag(), event.getMessage());
        }
    }

}
