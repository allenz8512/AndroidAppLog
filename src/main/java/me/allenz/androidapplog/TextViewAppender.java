package me.allenz.androidapplog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

public class TextViewAppender extends AsyncAppender {

    private static final int MAX_LOG_TEXT_LENGHT_IN_VIEW = 5000;

    private static final long MIN_PRINT_PERIOD = 200;

    private WeakReference<TextView> textViewRef;

    private Handler mainHandler;

    private StringBuilder logCache;

    private long lastUpdateMillis;

    public TextViewAppender(){
        super();
        logCache = new StringBuilder();
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
        logCache.delete(0, logCache.length());
        logEventQueue.clear();
    }

    private void postLogToUIThread(final String log) {
        if (mainHandler == null) {
            final Context context = LoggerFactory.getContext();
            if (context == null) {
                return;
            } else {
                mainHandler = new Handler(context.getMainLooper());
            }
        }
        mainHandler.post(new Runnable(){

            @Override
            public void run() {
                if (textViewRef != null) {
                    final TextView textView = textViewRef.get();
                    if (textView != null) {
                        textView.setText(log);
                    }
                }
            }

        });
    }

    @Override
    protected void handleEventQueue() throws InterruptedException {
        final LogEvent event = logEventQueue.poll(MIN_PRINT_PERIOD, TimeUnit.MILLISECONDS);
        if (textViewRef != null) {
            final TextView textView = textViewRef.get();
            if (textView != null) {
                final long currentMillis = System.currentTimeMillis();
                if (event != null) {
                    logCache.append(event.toString());
                }
                if ((currentMillis -
                     lastUpdateMillis > MIN_PRINT_PERIOD ||
                    lastUpdateMillis == 0) &&
                    logCache.length() > 0) {
                    final StringBuilder logBuilder = new StringBuilder(textView.getText());
                    logBuilder.append(logCache);
                    while (logBuilder.length() > MAX_LOG_TEXT_LENGHT_IN_VIEW) {
                        final int index = logBuilder.indexOf("\n");
                        logBuilder.delete(0, index + 1);
                    }
                    final String log = logBuilder.toString();
                    postLogToUIThread(log);
                    lastUpdateMillis = currentMillis;
                    logCache.delete(0, logCache.length());
                }
            }
        }

    }

}
