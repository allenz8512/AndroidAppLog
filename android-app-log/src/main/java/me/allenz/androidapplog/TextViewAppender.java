package me.allenz.androidapplog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextViewAppender extends AsyncAppender {

	private static final int MAX_LOG_TEXT_LENGHT_IN_VIEW = 5000;

	private static final long MIN_PRINT_PERIOD = 200;

	private WeakReference<TextView> textViewRef;

	private Handler mainHandler;

	private SpannableStringBuilder logBuilder;

	private long lastUpdateMillis;

	public TextViewAppender() {
		super();
		logBuilder = new SpannableStringBuilder();
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
		logBuilder.clearSpans();
		logBuilder.clear();
		logEventQueue.clear();
	}

	private void postLogToUIThread(final CharSequence log) {
		if (mainHandler == null) {
			final Context context = LoggerFactory.getContext();
			if (context == null) {
				return;
			} else {
				mainHandler = new Handler(context.getMainLooper());
			}
		}
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (textViewRef != null) {
					final TextView textView = textViewRef.get();
					if (textView != null) {
						textView.append(log);
					}
				}
			}

		});
	}

	@Override
	protected void handleEventQueue() throws InterruptedException {
		final LogEvent event = logEventQueue.poll(MIN_PRINT_PERIOD,
				TimeUnit.MILLISECONDS);
		if (textViewRef != null) {
			final TextView textView = textViewRef.get();
			if (textView != null) {
				final long currentMillis = System.currentTimeMillis();
				if (event != null) {
					final int start = logBuilder.length();
					logBuilder.append(event.toString());
					logBuilder.setSpan(new ForegroundColorSpan(event.getLevel()
							.getColor()), start, logBuilder.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					logBuilder.append("\n");
				}
				if ((currentMillis - lastUpdateMillis > MIN_PRINT_PERIOD || lastUpdateMillis == 0)
						&& logBuilder.length() > 0) {
					postLogToUIThread(new SpannedString(logBuilder));
					lastUpdateMillis = currentMillis;
					logBuilder.clearSpans();
					logBuilder.clear();
				}
			}
		}

	}

}
