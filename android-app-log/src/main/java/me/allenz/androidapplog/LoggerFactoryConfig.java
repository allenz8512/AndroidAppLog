package me.allenz.androidapplog;

import android.text.TextUtils;

public class LoggerFactoryConfig {

    private static final String DEFAULT_PROPERTIES_ENCODING = "ISO-8859-1";

    static String propertiesEncoding = DEFAULT_PROPERTIES_ENCODING;

    static boolean forceDebug = false;

    public static void setPropertiesEncoding(String propertiesEncoding) {
        if (!TextUtils.isEmpty(propertiesEncoding)) {
            LoggerFactoryConfig.propertiesEncoding = propertiesEncoding;
        }
    }

    public static void setForceDebug(boolean forceDebug) {
        LoggerFactoryConfig.forceDebug = forceDebug;
    }
}
