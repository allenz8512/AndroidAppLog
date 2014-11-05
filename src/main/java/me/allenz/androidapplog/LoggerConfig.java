package me.allenz.androidapplog;

public class LoggerConfig {

    private String name;

    private String tag;

    private LogLevel level;

    private boolean showThreadName;

    public LoggerConfig(final String name, final String tag,
                        final LogLevel level, final boolean showThreadName){
        this.name = name;
        this.tag = tag;
        this.level = level;
        this.showThreadName = showThreadName;
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

    public boolean isShowThreadName() {
        return showThreadName;
    }

    public void setShowThreadName(final boolean showThreadName) {
        this.showThreadName = showThreadName;
    }

    @Override
    public String toString() {
        return "LoggerConfig [name=" +
               name + ", tag=" + tag + ", level=" + level + ", showThreadName=" + showThreadName + "]";
    }

}
