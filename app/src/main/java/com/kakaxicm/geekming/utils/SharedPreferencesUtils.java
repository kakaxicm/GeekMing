package com.kakaxicm.geekming.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yy on 2014/9/28.
 */
public class SharedPreferencesUtils {

    public static void setSharedPreferences(Context context, String str, int i) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑
            editor.putInt(str, i);
            editor.apply();//提交修改
        }
    }

    public static int getSharedPreferences(Context context, String str, int def) {
        if (context == null) {
            return def;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(str, def);
        }
        return def;
    }

    public static void setSharedPreferencesLong(Context context, String str, long i) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑
            editor.putLong(str, i);
            editor.apply();//提交修改
        }
    }

    public static long getSharedPreferencesLong(Context context, String str) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            //SharedPreferences preferences=context.getSharedPreferences("inputmethod", Context.MODE_PRIVATE);

            return sharedPreferences.getLong(str, 0);
        }
        return -1;
    }

    public static void setSharedPreferencesBoolean(Context context, String str, boolean b) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑
            editor.putBoolean(str, b);
            editor.apply();//提交修改
        }
    }

    public static boolean getSharedPreferencesBoolean(Context context, String str) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            //SharedPreferences preferences=context.getSharedPreferences("inputmethod", Context.MODE_PRIVATE);

            return sharedPreferences.getBoolean(str, false);
        }
        return false;
    }

    public static void setSharedPreferences(Context context, String str, String str2) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putString(str, str2);
            editor.apply();//提交修改
        }
    }

    public static void setSharedPreferences(Context context, String str, boolean i) {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑
            editor.putBoolean(str, i);
            editor.apply();//提交修改
        }
    }

    public static boolean getSharedPreferences(Context context, String str) {
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences != null && sharedPreferences.getBoolean(str, false);
        } else {
            return false;
        }
    }


    public static String getSharedPreferencesString(Context context, String str) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences == null ? "" : sharedPreferences.getString(str, "");
    }
}
