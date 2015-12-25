package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by you on 2015/6/4.
 */
public class AppUtils {
    /**
     *获得version code
     */
    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前版本号
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getMetaDataValue(Context context,String name, String def) {

        String value = getMetaDataValue(context,name);

        return (value == null) ? def : value;

    }

    public static String getMetaDataValue(Context context,String name) {

        Object value = null;

        PackageManager packageManager = context.getPackageManager();

        ApplicationInfo applicationInfo;

        try {

            applicationInfo = packageManager.getApplicationInfo(context

                    .getPackageName(), 128);

            if (applicationInfo != null && applicationInfo.metaData != null) {

                value = applicationInfo.metaData.get(name);

            }

        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(

                    "Could not read the name in the manifest file.", e);

        }

        if (value == null) {

            throw new RuntimeException("The name '" + name

                    + "' is not defined in the manifest file's meta data.");

        }

        return value.toString();

    }

}
