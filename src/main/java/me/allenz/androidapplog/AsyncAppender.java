package me.allenz.androidapplog;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.Process;

public abstract class AsyncAppender extends AbstractAppender {

    protected static final int DEFAULT_LOG_EVENT_QUEUE_SIZE = 100;

    protected Thread workThread;

    protected BlockingQueue<LogEvent> logEventQueue;

    public AsyncAppender(){
        workThread = new WorkThread();
        logEventQueue = new ArrayBlockingQueue<LogEvent>(
            DEFAULT_LOG_EVENT_QUEUE_SIZE);
    }

    @Override
    protected boolean doStart() {
        workThread.start();
        return true;
    }

    @Override
    protected boolean doStop() {
        workThread.interrupt();
        return true;
    }

    @Override
    protected void doAppend(final LogEvent event) {
        try {
            logEventQueue.put(event);
        } catch (final InterruptedException e) {
        }
    }

    protected abstract void handleEventQueue() throws InterruptedException;

    protected class WorkThread extends Thread {

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (!isInterrupted()) {
                try {
                    handleEventQueue();
                } catch (final InterruptedException e) {
                    if (!isStarted()) {
                        return;
                    }
                }
            }
        }

    }

}
