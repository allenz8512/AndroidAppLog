package me.allenz.androidapplog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollingFileAppender extends AsyncAppender {

	private static final byte[] LINE_BREAK_BYTES = "\n".getBytes();

	private File logDir;

	private String packageName;

	private int rolling;

	private long rollSize;

	private boolean useGZip;

	private File logFile;

	private FileOutputStream out;

	public RollingFileAppender(final File logDir, final long rollSize,
			final boolean useGZip) {
		this.logDir = logDir;
		this.rollSize = rollSize;
		this.useGZip = useGZip;
		packageName = LoggerFactory.getPackageName();
	}

	@Override
	protected boolean doStart() {
		final boolean created = createLogDir();
		if (!created) {
			LoggerFactory.getInternalLogger().verbose(
					"can not create folder %s", logDir.getPath());
			return false;
		}
		rolling = getRollingNumber();
		logFile = getLogFile(rolling);
		try {
			out = createOrOpenLogFile(logFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return super.doStart();
	}

	@Override
	protected boolean doStop() {
		try {
			out.close();
		} catch (final IOException e) {
		}
		return super.doStop();
	}

	private boolean createLogDir() {
		if (!logDir.exists()) {
			return logDir.mkdirs();
		}
		return true;
	}

	private int getRollingNumber() {
		final Pattern fileNamePattern = Pattern.compile("^"
				+ packageName.replace(".", "\\.").replace("$", "\\$")
				+ "(\\.(\\d+))?\\.log(\\.gz)?$");
		int number = 0;
		final File[] logFiles = logDir.listFiles();
		for (int i = 0; i < logFiles.length; i++) {
			final String logFileName = logFiles[i].getName();
			final Matcher matcher = fileNamePattern.matcher(logFileName);
			if (matcher.find()) {
				try {
					final Integer currentNumber = Integer.valueOf(matcher
							.group(2));
					if (number < currentNumber) {
						number = currentNumber;
					}
				} catch (final NumberFormatException e) {
				}
			}
		}
		return number;
	}

	private File getLogFile(final int rolling) {
		String logFilename = packageName;
		if (rolling > 0) {
			logFilename += "." + rolling;
		}
		logFilename += ".log";
		return new File(logDir, logFilename);
	}

	private FileOutputStream createOrOpenLogFile(final File logFile)
			throws IOException {
		if (!logFile.exists()) {
			logFile.createNewFile();
			return new FileOutputStream(logFile);
		} else {
			return new FileOutputStream(logFile, true);
		}
	}

	@Override
	protected void handleEventQueue() throws InterruptedException {
		try {
			final LogEvent event = logEventQueue.take();
			final byte[] logBytes = event.toString().getBytes();
			if (!logFile.exists()) {
				out.close();
				out = createOrOpenLogFile(logFile);
			}
			final long logFileSize = out.getChannel().size();
			if (logFileSize + logBytes.length > rollSize) {
				out.close();
				if (useGZip) {
					Runtime.getRuntime().exec("gzip " + logFile.getPath());
				}
				rolling++;
				logFile = getLogFile(rolling);
				out = createOrOpenLogFile(logFile);
			}
			out.write(logBytes);
			out.write(LINE_BREAK_BYTES);
			out.flush();
			out.getFD().sync();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
