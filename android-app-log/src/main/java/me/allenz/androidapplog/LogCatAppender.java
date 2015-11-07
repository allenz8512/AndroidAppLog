package me.allenz.androidapplog;

import android.util.Log;

public class LogCatAppender extends AbstractAppender {

    @Override
    public void doAppend(final LogEvent event) {
        Log.println(event.getLevel().intValue(), event.getTag(), event.getMessage());
    }

    @Override
    protected boolean doStart() {
        return true;
    }

    @Override
    protected boolean doStop() {
        return true;
    }

}
