package com.kakaxicm.geekming.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huyongsheng on 2014/7/18.
 */
public class StringUtils {

    private static final String LOG_TAG = StringUtils.class.getSimpleName();

    /**
     * 最优化String的构建
     */
    public static final String getString(Object... objects) {
        StringBuffer buffer = new StringBuffer();
        for (Object object : objects) {
            buffer.append(object);
        }
        return buffer.toString();
    }

    /**
     * TextView中文字显示不同颜色
     */
//    public static SpannableStringBuilder getMultiColorString(int color, String context, String... segments) {
//        SpannableStringBuilder builder = new SpannableStringBuilder(context);
//        for (String segment : segments) {
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
//            int start = context.indexOf(segment);
//            int end = start + segment.length();
//            builder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return builder;
//    }

    /**
     * 关键字高亮显示
     *
     * @param segments 需要高亮的关键字
     * @param text     需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder getMultiColorString(int color, String text, String... segments) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        Pattern p;
        Matcher m;
        for (String segment : segments) {
            p = Pattern.compile(segment);
            m = p.matcher(text);
            while (m.find()) {
                span = new ForegroundColorSpan(color);// 需要重复！
                spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }

    /**
     * 生成base64编码
     */
    public static final String encodeBase64(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.NO_WRAP);
    }

    /**
     * 对double数据进行截断
     */
    public static final String cutDouble2(double value) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        return fnum.format(value);
    }

    /**
     * 对double数据进行截断
     */
    public static final String cutFloat2(float value) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        return fnum.format(value);
    }

    /**
     * base64解码
     */
    public static final String decodeBase64(String string) {
        String result = null;
        if (!StringUtils.isNullOrEmpty(string)) {
            try {
                result = new String(Base64.decode(string, Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                LogForTest.logW(LOG_TAG, "解析base64出错 \n");
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                LogForTest.logW(LOG_TAG, "解析base64出错 \n");
                e.printStackTrace();
            }
        }
        return result;
    }

    public static final boolean isNullOrEmpty(String inputString) {
        if (null == inputString) {
            return true;
        } else {
            return inputString.trim().equals("");
        }
    }

    public static final boolean isNullOrEmpty(byte[] bytes) {
        if (null == bytes) {
            return true;
        } else {
            return bytes.length == 0;
        }
    }

    /**
     * 生成google play连接地址
     */
    public static final String getGooglePlayString(Activity activity, String packageName) {
        return getGooglePlayString(packageName, "flip", activity.getPackageName());
    }

    /**
     * 生成google play连接地址
     */
    public static final String getGooglePlayString(String packageName, String source, String medium) {
        return StringUtils.getString("market://details?id=", packageName, "&referrer=", "utm_source%3D", source,
                "%26utm_medium%3D", medium);
    }


    /**
     * 得到配置文件中的MetaData数据
     */
    public static String getMetaData(Context context, String keyName) {
        try {
            ApplicationInfo appi = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);
            return value.toString();
        } catch (Exception e) {
            LogForTest.logE(LOG_TAG, "can not get metaData:" + keyName);
            return "";
        }
    }

    /**
     * 获取package信息
     */
    public static final PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            LogForTest.logE(LOG_TAG, "Could not get package info.");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 对double数据进行截断
     */
    public static final String cutDouble0(double value) {
        DecimalFormat fnum = new DecimalFormat("##0");
        return fnum.format(value);
    }

    /**
     * 对double数据进行截断
     */
    public static final String cutFloat0(float value) {
        DecimalFormat fnum = new DecimalFormat("##0");
        return fnum.format(value);
    }


    /**
     * 获取post请求中的参数
     */
    public static final String getPostParams(String preString, Object object) {
        String result = getString(preString, "{");
        boolean isFirst = true;
        // 获取object对象对应类中的所有属性域
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 对于每个属性，获取属性名
            String varName = field.getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                // 获取在对象object中属性field对应的对象中的变量
                Object value = field.get(object);
                // 生成参数,其实跟get的URL中'?'后的参数字符串一致
                if (isFirst) {
                    if (value instanceof String) {
                        result += getString("\"", URLEncoder.encode(varName, "utf-8"), "\":\"",
                                URLEncoder.encode(String.valueOf(value), "utf-8"), "\"");
                    } else {
                        result += getString("\"", URLEncoder.encode(varName, "utf-8"), "\":",
                                URLEncoder.encode(String.valueOf(value), "utf-8"));
                    }
                    isFirst = false;
                } else {
                    if (value instanceof String) {
                        result += getString(",\"", URLEncoder.encode(varName, "utf-8"), "\":\"",
                                URLEncoder.encode(String.valueOf(value), "utf-8"), "\"");
                    } else {
                        result += getString(",\"", URLEncoder.encode(varName, "utf-8"), "\":",
                                URLEncoder.encode(String.valueOf(value), "utf-8"));
                    }
                }
                // 恢复访问控制权限
                field.setAccessible(accessFlag);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        result += "}";
        return result;
    }

    /**
     * 获取post请求中的参数
     */
    public static String getSimplePostParams(Object object) {
        String result = "";
        boolean isFirst = true;
        // 获取object对象对应类中的所有属性域
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 对于每个属性，获取属性名
            String varName = field.getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                // 获取在对象object中属性field对应的对象中的变量
                Object value = field.get(object);
                // 生成参数,其实跟get的URL中'?'后的参数字符串一致
                if (value != null) {
                    if (isFirst) {
                        result += getString(URLEncoder.encode(varName, "utf-8"), "=",
                                URLEncoder.encode(String.valueOf(value), "utf-8"));
                        isFirst = false;
                    } else {
                        result += getString("&", URLEncoder.encode(varName, "utf-8"), "=",
                                URLEncoder.encode(String.valueOf(value), "utf-8"));
                    }
                }
                // 恢复访问控制权限
                field.setAccessible(accessFlag);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 使用sha加密
     */
    public static String getSHA(String val) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    /**
     * 手机号合法性校验
     */
    public static boolean checkPhoneNumber(String value) {
        String regExp = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        return m.find();
    }

    /**
     * 手机号合法性校验
     */
    public static boolean checkUserName(String value) {
        String regExp = "^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        return m.find();
    }

    /**
     * 将字符串转成MD5值
     */
    public static String toMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
