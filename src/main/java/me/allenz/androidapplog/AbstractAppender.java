package me.allenz.androidapplog;

public abstract class AbstractAppender implements Appender {

    private static final Logger internalLogger = LoggerFactory.getInternalLogger();

    protected boolean started;

    @Override
    public void start() {
        if (!started) {
            started = doStart();
            if (started) {
                internalLogger.verbose("%s is started", this.getClass().getSimpleName());
            } else {
                internalLogger.verbose("can not start %s", this.getClass().getSimpleName());
            }
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void stop() {
        if (started) {
            started = !doStop();
            if (!started) {
                internalLogger.verbose("%s is stop", this.getClass().getSimpleName());
            } else {
                internalLogger.verbose("can not stop %s", this.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void append(final LogEvent event) {
        if (isStarted()) {
            doAppend(event);
        }
    }

    protected abstract boolean doStart();

    protected abstract boolean doStop();

    protected abstract void doAppend(LogEvent event);

}
