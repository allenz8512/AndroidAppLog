package me.allenz.androidapplog;

import android.util.Log;

public enum LogLevel {

    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT),
    OFF(Integer.MAX_VALUE);

    private final int intVal;

    private LogLevel(final int intVal){
        this.intVal = intVal;
    }

    public int intValue() {
        return intVal;
    }

    public boolean includes(final LogLevel level) {
        return level != null &&
               this.intValue() <= level.intValue();
    }
}
