package me.allenz.androidapplog;

import java.lang.Thread.UncaughtExceptionHandler;

public class UncaughtExceptionLogger implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler mDefaultExceptionHandler;

	public UncaughtExceptionLogger() {
		this(Thread.getDefaultUncaughtExceptionHandler());

	}

	public UncaughtExceptionLogger(
			final UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.mDefaultExceptionHandler = uncaughtExceptionHandler;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		final Logger logger = LoggerFactory.getLogger(ex.getStackTrace()[0]
				.getClassName());
		logger.error(ex, "Uncaught exception in thread [%s] :",
				thread.getName());
		if (mDefaultExceptionHandler != null) {
			mDefaultExceptionHandler.uncaughtException(thread, ex);
		}
	}

	public UncaughtExceptionHandler getDefaultExceptionHandler() {
		return mDefaultExceptionHandler;
	}

}
