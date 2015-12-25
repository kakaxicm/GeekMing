package com.kakaxicm.geekming.utils;

/**
 * Created by yy on 2014/9/5.
 */
public final class LogForTest {

    public static final boolean debug = false;

    public static void logW(String str) {
        if (debug) {
            android.util.Log.w("LogForTest", " * " + str);
        }
    }

    public static void logW(String tag, String str) {
        if (debug) {
            android.util.Log.w("LogForTest:" + tag, " * " + str);
        }
    }

    public static void logE(String str) {
        if (debug) {
            android.util.Log.e("LogForTest:", " * " + str);
        }
    }

    public static void logE(String tag, String str) {
        if (debug) {
            android.util.Log.e("LogForTest:" + tag, " * " + str);
        }
    }
}
