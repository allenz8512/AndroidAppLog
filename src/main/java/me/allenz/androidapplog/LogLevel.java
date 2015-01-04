package me.allenz.androidapplog;

import android.graphics.Color;
import android.util.Log;

public enum LogLevel {

	VERBOSE(Log.VERBOSE, "#a0000000"),
	DEBUG(Log.DEBUG, "#a000007f"),
	INFO(Log.INFO, "#a0007f00"),
	WARN(Log.WARN, "#a0ff7f00"),
	ERROR(Log.ERROR, "#a0ff0000"),
	ASSERT(Log.ASSERT, "#a0ff0000"),
	OFF(Integer.MAX_VALUE, "#a0000000");

	private final int intVal;
	private final int color;

	private LogLevel(final int intVal, final String color) {
		this.intVal = intVal;
		this.color = Color.parseColor(color);
	}

	public int intValue() {
		return intVal;
	}

	public int getColor() {
		return color;
	}

	public boolean includes(final LogLevel level) {
		return level != null && this.intValue() <= level.intValue();
	}
}
