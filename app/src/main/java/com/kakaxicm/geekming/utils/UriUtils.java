package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.net.Uri;

/**
 * Created by star on 15/6/5.
 */
public class UriUtils {
    public static Uri getResourceUri(Context context, int resId) {
        return Uri.parse(StringUtils.getString("res://", context.getPackageName(), "/", resId));
    }
}
