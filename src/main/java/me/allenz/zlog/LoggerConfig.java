package me.allenz.zlog;

/**
 * Logger config holder for each logger property in configure file.
 * 
 * @author Allenz
 * @since 0.2.0-RELEASE
 */
class LoggerConfig {

	protected String name;
	protected String tag;
	protected LogLevel level;
	protected boolean thread;

	/**
	 * Create a new LoggerConfig instance.
	 * 
	 * @param name
	 *            package or class fullname
	 * @param level
	 *            the LogLevel for the package or class
	 * @param tag
	 *            the tag for the package or class
	 * @param thread
	 *            if true, shows thread name as a prefix of the tag
	 */
	public LoggerConfig(final String name, final String tag,
			final LogLevel level, final boolean thread) {
		this.name = name;
		this.tag = tag;
		this.level = level;
		this.thread = thread;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(final String tag) {
		this.tag = tag;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(final LogLevel level) {
		this.level = level;
	}

	public boolean isThread() {
		return thread;
	}

	public void setThread(final boolean thread) {
		this.thread = thread;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[name=").append(name).append(", tag=").append(tag)
				.append(", level=").append(level).append(", thread=")
				.append(thread).append("]");
		return builder.toString();
	}

}