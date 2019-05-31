package util;

import android.util.Log;

/**
 * @decs: Logger
 * @author: 郑少鹏
 * @date: 2019/5/31 14:48
 */
public class Logger {
    private static final boolean LOG_ENABLE = true;

    public static void i(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.i(tag, msg);
        }
    }

    static void v(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.e(tag, msg);
        }
    }
}