package com.kakaxicm.geekming.utils;

/**
 * Created by xinmei on 2015/1/19.
 */
public class UnicodeUtil {
    public static String newSingleCodePointString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf((char) codePoint);
        }
        return new String(Character.toChars(codePoint));
    }
}
