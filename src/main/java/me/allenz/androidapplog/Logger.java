package me.allenz.androidapplog;

public interface Logger {

    String getName();

    LogLevel getLogLevel();

    void setLogLevel(LogLevel level);

    String getTag();

    void setTag(String tag);

    boolean isShowThreadName();

    void setShowThreadName(boolean show);

    void verbose(String format, Object... args);

    void verbose(Throwable t);

    void verbose(Throwable t, String format, Object... args);

    void verbose(String message);

    void verbose(Throwable t, String message);

    void verbose(Object obj);

    void debug(String format, Object... args);

    void debug(Throwable t);

    void debug(Throwable t, String format, Object... args);

    void debug(String message);

    void debug(Throwable t, String message);

    void debug(Object obj);

    void info(String format, Object... args);

    void info(Throwable t);

    void info(Throwable t, String format, Object... args);

    void info(String message);

    void info(Throwable t, String message);

    void info(Object obj);

    void warn(String format, Object... args);

    void warn(Throwable t);

    void warn(Throwable t, String format, Object... args);

    void warn(String message);

    void warn(Throwable t, String message);

    void warn(Object obj);

    void error(String format, Object... args);

    void error(Throwable t);

    void error(Throwable t, String format, Object... args);

    void error(String message);

    void error(Throwable t, String message);

    void error(Object obj);

    void wtf(String format, Object... args);

    void wtf(Throwable t);

    void wtf(Throwable t, String format, Object... args);

    void wtf(String message);

    void wtf(Throwable t, String message);

    void wtf(Object obj);
}
