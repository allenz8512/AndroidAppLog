package me.allenz.zlog;

import android.widget.TextView;

public class TextViewLogger extends SimpleLogger {

    public TextViewLogger(
                          final String name, final String tag, final LogLevel level, final boolean thread,
                          final LogWriter logWriter){
        super(name, tag, level, thread, logWriter);
    }

    public void associateTextView(final TextView textView) {
        LoggerFactory.bind(textView);
    }

    public void unassociateTextView() {
        LoggerFactory.unbind();
    }

    @Override
    protected LogEvent println(final LogLevel level, final Throwable t, final String format, final Object... args) {
        final LogEvent event = super.println(level, t, format, args);
        LoggerFactory.printlnLogOnScreen(event);
        return event;
    }

}
