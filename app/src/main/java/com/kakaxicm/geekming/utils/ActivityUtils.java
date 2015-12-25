package com.kakaxicm.geekming.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtils {

    /**
     * goto google play
     */
    public static boolean jumpToGooglePlayByPackage(Activity activity, String packageName) {
        return jumpToGooglePlay(activity, StringUtils.getGooglePlayString(activity, packageName));
    }

    /**
     * goto google play
     */
    public static boolean jumpToGooglePlay(Activity activity, String url) {
        boolean result = false;
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public static void jumpToMarket(Activity activity, String packageName) {
        Uri uri = Uri.parse(StringUtils.getString("market://details?id=", packageName));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void jumpToMarketByOrder(Activity activity, String packageName) {
        Uri uri = Uri.parse(StringUtils.getString("market://details?id=", packageName));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        List<String> marketAppNameList = new ArrayList<String>();
        List<String> marketAppClassList = new ArrayList<String>();
        for (ResolveInfo resolveInfo : infos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String targetPargetName = activityInfo.packageName;
            String className = activityInfo.name;
            marketAppNameList.add(targetPargetName);
            marketAppClassList.add(className);
        }
        // 应用宝；百度；360；小米；安智；
        if (marketAppNameList.contains("com.tencent.android.qqdownloader")) {
            int index = marketAppNameList.indexOf("com.tencent.android.qqdownloader");
            ComponentName cn = new ComponentName(marketAppNameList.get(index), marketAppClassList.get(index));
            intent.setComponent(cn);
        } else if (marketAppNameList.contains("com.baidu.appsearch")) {
            int index = marketAppNameList.indexOf("com.baidu.appsearch");
            ComponentName cn = new ComponentName(marketAppNameList.get(index), marketAppClassList.get(index));
            intent.setComponent(cn);
        } else if (marketAppNameList.contains("com.qihoo.appstore")) {
            int index = marketAppNameList.indexOf("com.qihoo.appstore");
            ComponentName cn = new ComponentName(marketAppNameList.get(index), marketAppClassList.get(index));
            intent.setComponent(cn);
        } else if (marketAppNameList.contains("com.xiaomi.market")) {
            int index = marketAppNameList.indexOf("com.xiaomi.market");
            ComponentName cn = new ComponentName(marketAppNameList.get(index), marketAppClassList.get(index));
            intent.setComponent(cn);
        } else if (marketAppNameList.contains("cn.goapk.market")) {
            int index = marketAppNameList.indexOf("cn.goapk.market");
            ComponentName cn = new ComponentName(marketAppNameList.get(index), marketAppClassList.get(index));
            intent.setComponent(cn);
        }
        activity.startActivity(intent);
    }

    /**
     * 跳转到指定app
     */
    public static boolean jumpToIKeyBoard(Context context, String packageName, String className) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, className);
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.MAIN");
        intent.putExtra("source package", "flip font");
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 跳转到activity
     */
    public static void jump(Context context, Intent intent) {
        context.startActivity(intent);
    }

    /**
     * 跳转到activity
     */
    public static void jump(Context context, Class<?> targetClass) {
        Intent intent = new Intent();
        intent.setClass(context, targetClass);
        jump(context, intent);
    }

    /**
     * 跳转到activity
     */
    public static void jump(Activity activity, Class<?> targetClass, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, targetClass);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到activity
     */
    public static void jump(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断intent是否可用
     */
    private static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 分享信息
     */
    public static void shareFont(Context content, int subjectRes, String context, int titleRes) {
        shareFont(content, content.getString(subjectRes), context, content.getString(titleRes));
    }

    /**
     * 分享信息
     */
    public static void shareFont(Context content, String subject, String context, CharSequence title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, context);
        content.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * 跳转到相册获取图片
     */
    public static void jumpToAlbun(Activity activity, int requestCode) {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image/*");
        activity.startActivityForResult(getAlbum, requestCode);
    }

    /****************
     * 发起添加群流程。群号：艺术签名个性版粉丝群(148134518) 的 key 为： Ag_rDfRJKiS4VZWaa5vX6IfBIOkgVvr2
     * 调用 joinQQGroup(Ag_rDfRJKiS4VZWaa5vX6IfBIOkgVvr2) 即可发起手Q客户端申请加群 艺术签名个性版粉丝群(148134518)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public static boolean joinQQGroup(Activity activity, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(
                "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static void openBrowser(Context ctx, String url) {
        try {
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(it);
        } catch (Exception e2) {

        }
    }
}
