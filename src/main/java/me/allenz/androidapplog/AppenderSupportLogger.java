package me.allenz.androidapplog;

import java.util.List;

public class AppenderSupportLogger extends AbstractLogger {

    public AppenderSupportLogger(final String name, final LogLevel level, final String tag, final boolean showThreadName){
        super(name, level, tag, showThreadName);
    }

    @Override
    protected void println(final LogLevel level, final Throwable t, final String format, final Object... args) {
        if (this.level.includes(level) &&
            (t != null || format != null)) {
            final LogEvent event = buildLogEvent(level, t, format, args);
            final List<Appender> appenders = LoggerFactory.getRepository().getAppenders();
            for (final Appender appender: appenders) {
                appender.append(event);
            }
        }
    }

}
