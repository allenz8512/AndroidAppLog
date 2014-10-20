package me.allenz.zlog;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

class TextViewPrinter {

    private static final Logger internalLogger = ConfigureRepository
        .getInternalLogger();

    private static final int DEFAULT_LOG_EVENT_QUEUE_SIZE = 100;

    private static final int MAX_LOG_TEXT_LENGHT_IN_VIEW = 5000;

    private static final long MIN_PRINT_PERIOD = 200;

    private WeakReference<TextView> textViewRef;

    private Handler mainHandler;

    private boolean started;

    private boolean stop;

    private Thread worker;

    private BlockingQueue<LogEvent> logEventQueue;

    private long lastUpdateMillis;

    private StringBuilder logCache;

    public TextViewPrinter(final Context context){
        mainHandler = new Handler(context.getMainLooper());
        worker = new UpdateTextViewContentThread();
        logEventQueue = new ArrayBlockingQueue<LogEvent>(
            DEFAULT_LOG_EVENT_QUEUE_SIZE);
        logCache = new StringBuilder();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isStop() {
        return stop;
    }

    public void start() {
        if (isStarted() || isStop()) {
            throw new IllegalStateException("Can not restart a TextViewPrinter");
        }
        worker.start();
        started = true;
    }

    public void stop() {
        if (isStop()) {
            return;
        }
        worker.interrupt();
        stop = true;
        started = false;
    }

    public void bind(final TextView textView) {
        if (textView == null) {
            return;
        }
        textViewRef = new WeakReference<TextView>(textView);
    }

    public void unbind() {
        textViewRef = null;
        lastUpdateMillis = 0;
        logCache = new StringBuilder();
        logEventQueue.clear();

    }

    public void print(final LogEvent event) {
        if (textViewRef == null || event == null) {
            return;
        }
        if (!started) {
            return;
        }
        final TextView textView = textViewRef.get();
        if (textView == null) {
            internalLogger.debug("TextView instance is null, maybe it has been released by GC");
            return;
        }
        while (!logEventQueue.offer(event)) {
            logEventQueue.poll();
        }
    }

    private class UpdateTextViewContentThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {
                final BlockingQueue<LogEvent> logEventQueue = TextViewPrinter.this.logEventQueue;
                LogEvent event;
                try {
                    event = logEventQueue.take();
                    if (textViewRef != null) {
                        final TextView textView = textViewRef.get();
                        if (textView != null) {
                            final long currentMillis = System.currentTimeMillis();
                            final long lastUpdateMillis = TextViewPrinter.this.lastUpdateMillis;
                            final StringBuilder logCache = TextViewPrinter.this.logCache;
                            logCache.append(event.toString());
                            if (currentMillis - lastUpdateMillis > MIN_PRINT_PERIOD || lastUpdateMillis == 0) {
                                final StringBuilder logBuilder = new StringBuilder(textView.getText());
                                logBuilder.append(logCache);
                                while (logBuilder.length() > MAX_LOG_TEXT_LENGHT_IN_VIEW) {
                                    final int index = logBuilder.indexOf("\n");
                                    logBuilder.delete(0, index + 1);
                                }
                                final String logText = logBuilder.toString();
                                mainHandler.post(new Runnable(){

                                    @Override
                                    public void run() {
                                        if (textView != null) {
                                            textView.setText(logText);
                                        }
                                    }

                                });
                                TextViewPrinter.this.lastUpdateMillis = currentMillis;
                                TextViewPrinter.this.logCache = new StringBuilder();
                            }
                        }
                    }
                } catch (final InterruptedException e) {
                    if (isStop()) {
                        return;
                    }
                }
            }
        }
    }

}
