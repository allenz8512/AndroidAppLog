package me.allenz.zlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thre repository store all configures and loggers.
 * 
 * @author Allenz
 * @since 0.2.0-RELEASE
 */
class ConfigureRepository {

    private static final LoggerConfig DEFAULT_ROOT_LOGGER_CONFIG = new LoggerConfig(
        "root", null, LogLevel.VERBOSE, false);

    private static final String DEFAULT_INTERNAL_LOGGER_TAG = "zlog";

    private static final LogLevel DEFAULT_INTERNAL_LOG_LEVEL = LogLevel.VERBOSE;

    private static final SimpleLogger internalLogger = new SimpleLogger(null,
        DEFAULT_INTERNAL_LOGGER_TAG, DEFAULT_INTERNAL_LOG_LEVEL, false,
        null);

    public static Logger getInternalLogger() {
        return internalLogger;
    }

    public static void setInternalLogLevel(final LogLevel level) {
        internalLogger.setLevel(level);
    }

    private LoggerConfig rootLoggerConfig;

    private Map<String, LoggerConfig> loggerConfigs;

    private Map<String, Logger> loggers;

    private LogWriter logWriter;

    private TextViewPrinter textViewPrinter;

    public ConfigureRepository(){
        loggerConfigs = new HashMap<String, LoggerConfig>();
        loggers = new HashMap<String, Logger>();
        resetToDefault();
    }

    public LoggerConfig getRootLoggerConfig() {
        return rootLoggerConfig;
    }

    public void setRootLoggerConfig(final LoggerConfig loggerConfig) {
        this.rootLoggerConfig = loggerConfig;
    }

    public LoggerConfig getLoggerConfig(final String name) {
        return loggerConfigs.get(name);
    }

    public void addLoggerConfig(final LoggerConfig loggerConfig) {
        loggerConfigs.put(loggerConfig.getName(), loggerConfig);
    }

    public Logger getLogger(final String name) {
        return loggers.get(name);
    }

    public void addLogger(final String name, final Logger logger) {
        loggers.put(name, logger);
    }

    public List<Logger> getAllLoggers() {
        return new ArrayList<Logger>(loggers.values());
    }

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(final LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public TextViewPrinter getTextViewPrinter() {
        return textViewPrinter;
    }

    public void setTextViewPrinter(TextViewPrinter textViewPrinter) {
        this.textViewPrinter = textViewPrinter;
    }

    /**
     * Reset this repository to default. That means cleanning all configures and loggers it stored.
     */
    public void resetToDefault() {
        setInternalLogLevel(DEFAULT_INTERNAL_LOG_LEVEL);
        setRootLoggerConfig(DEFAULT_ROOT_LOGGER_CONFIG);
        loggerConfigs.clear();
        if (!loggers.isEmpty()) {// If there's any logger in use, reset their
                                 // configure just like default root logger
            for (final Logger logger: loggers.values()) {
                final SimpleLogger simpleLogger = (SimpleLogger) logger;
                simpleLogger.setLevel(DEFAULT_ROOT_LOGGER_CONFIG.getLevel());
                simpleLogger.setTag(Utils.finalTag(simpleLogger.getName(),
                    DEFAULT_ROOT_LOGGER_CONFIG.getTag()));
                simpleLogger.setThread(DEFAULT_ROOT_LOGGER_CONFIG.isThread());
            }
        }
        loggers.clear();
    }

}
