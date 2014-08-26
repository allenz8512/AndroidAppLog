package me.allenz.zlog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.text.format.DateFormat;

/**
 * A consumer class that consume log events, and storage them to specify log
 * file.
 * 
 * @author Allenz
 * @since 0.3.0-RELEASE
 */
public class LogWriter {

	private static final int DEFAULT_LOG_EVENT_QUEUE_SIZE = 50;

	private static final Logger internalLogger = ConfigureRepository
			.getInternalLogger();

	private File logFile;
	private boolean started;
	private boolean stop;
	private Thread worker;
	private BlockingQueue<LogEvent> logEventQueue;
	private FileOutputStream out;

	public LogWriter(final File logFile) {
		this.logFile = logFile;
		worker = new WriteFileThread();
		logEventQueue = new ArrayBlockingQueue<LogEvent>(
				DEFAULT_LOG_EVENT_QUEUE_SIZE);
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isStop() {
		return stop;
	}

	public void start() {
		if (isStarted() || isStop()) {
			throw new IllegalStateException("can not restart a LogWriter");
		}
		try {
			createAndOpenLogFile();
			worker.start();
		} catch (final Exception e) {
			stop();
			internalLogger.verbose(e, "can not start LogWriter");
		}
		started = true;
	}

	private void createAndOpenLogFile() throws IOException {
		if (!logFile.exists()) {
			logFile.createNewFile();
			out = new FileOutputStream(logFile);
		} else {
			out = new FileOutputStream(logFile, true);
		}

	}

	public void write(final LogLevel level, final String tag,
			final String message) {
		try {
			logEventQueue.put(new LogEvent(System.currentTimeMillis(), level,
					tag, message));
		} catch (final InterruptedException e) {
		}
	}

	public void stop() {
		if (isStop()) {
			return;
		}
		worker.interrupt();
		closeFile();
		stop = true;
		started = false;
	}

	private void closeFile() {
		if (out != null) {
			try {
				out.close();
			} catch (final IOException e) {
			}
		}
	}

	private void writeInternal(final LogEvent logEvent) throws IOException {
		out.write(logEvent.toString().getBytes());
	}

	private static class LogEvent {

		private long time;
		private LogLevel level;
		private String tag;
		private String message;

		public LogEvent(final long time, final LogLevel level,
				final String tag, final String message) {
			this.time = time;
			this.level = level;
			this.tag = tag;
			this.message = message;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(DateFormat.format("yyyy-MM-dd HH:mm:ss SSS", time))
					.append(" ");
			sb.append(level.toString()).append(" ");
			sb.append(tag).append(" ");
			sb.append(message).append("\n");
			return sb.toString();
		}
	}

	private class WriteFileThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					final LogEvent logEvent = logEventQueue.take();
					writeInternal(logEvent);
				} catch (final InterruptedException e) {
					if (stop) {
						return;
					}
				} catch (final IOException e) {
					// just ignore IOException
				}
			}
		}

	}
}
