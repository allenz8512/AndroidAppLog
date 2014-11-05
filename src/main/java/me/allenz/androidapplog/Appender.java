package me.allenz.androidapplog;

public interface Appender {

    void start();

    boolean isStarted();

    void stop();

    void append(LogEvent event);
}
