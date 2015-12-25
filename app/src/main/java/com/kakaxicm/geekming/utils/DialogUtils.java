package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by kakaxicm on 2015/12/25.
 */
public class DialogUtils {
    private static Toast mShortPromptToast;
    private static Toast mLongPromptToast;

    public static void showShortPromptToast(Context context, int resid) {
        if (mShortPromptToast == null) {
            mShortPromptToast = Toast.makeText(context, resid, Toast.LENGTH_SHORT);
        }
        mShortPromptToast.setText(resid);
        mShortPromptToast.show();
    }

    public static void showShortPromptToast(Context context, String res) {
        if (mShortPromptToast == null) {
            mShortPromptToast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        }
        mShortPromptToast.setText(res);
        mShortPromptToast.show();
    }

}
