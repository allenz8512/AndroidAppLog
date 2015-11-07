package me.allenz.androidapplog;

import android.text.TextUtils;

public class LoggerFactoryConfig {

    private static final String DEFAULT_PROPERTIES_ENCODING = "ISO-8859-1";

    static String mPropertiesEncoding = DEFAULT_PROPERTIES_ENCODING;

    public static void setPropertiesEncoding(String propertiesEncoding) {
        if (!TextUtils.isEmpty(propertiesEncoding)) {
            mPropertiesEncoding = propertiesEncoding;
        }
    }
}
