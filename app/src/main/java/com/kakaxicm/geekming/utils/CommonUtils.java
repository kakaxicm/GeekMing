package com.kakaxicm.geekming.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by you on 2015/5/26.
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    /**
     * 手机号合法性校验
     */
    public static boolean checkPhoneNumber(String value) {
        String regExp = "^[1]([3][0-9]{1}|59|51|50|58|88|89|82)[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        return m.find();
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }


    /**
     * 判断一个字符是否为汉字
     * @param txt
     * @return
     */
    private static boolean isChineseText(char txt){
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(txt);
        if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || unicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串是否全为汉字
     * @param string
     * @return
     */

    public static boolean isChinese(String string){
        boolean flags = false;
        char[] strChars = string.toCharArray();
        for (char strChar : strChars) {
            flags = isChineseText(strChar);
            if (!flags) {
                break;
            }
        }
        return flags;
    }
}
