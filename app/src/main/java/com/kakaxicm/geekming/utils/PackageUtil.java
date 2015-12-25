package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by xinmei on 2014/10/31.
 */
public class PackageUtil {

    private static final String LOVE_NIGHT = "locktheme";
    private static final String THEME_ACTIVITY = "themeactivity";
    public static final String URL_KEY = "url:";
    public static final String URL_WITHOUT_DETAIL_KEY = "url_without_detail:";

    //拿目标字体包名，如果存在，则contex
    public static boolean checkIsPackageInstalled(Context context, String packName) {
        try {
            Context otherContext = context.createPackageContext(packName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            return otherContext != null;
        } catch (PackageManager.NameNotFoundException e) {
            LogForTest.logW("PackageUtil", "apk:" + packName + " not founded!");
        }
        return false;
    }

    public static boolean checkIsThemeLoveNight(String packName) {

        return packName.toLowerCase().contains(LOVE_NIGHT);

    }

    public static boolean checkIsThemeActivity(String packName) {

        return packName.contains(THEME_ACTIVITY);

    }

    public static boolean checkIsUrl(String packName) {
        return packName.contains(URL_KEY);

    }

    public static boolean checkIsUrlWithoutDetail(String packName) {
        return packName.contains(URL_WITHOUT_DETAIL_KEY);

    }

    public static int getVersionCode(Context context) {
        int verCode = -1;
        String pckName = context.getPackageName();
        try {
            verCode = context.getPackageManager().getPackageInfo(pckName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }
}
