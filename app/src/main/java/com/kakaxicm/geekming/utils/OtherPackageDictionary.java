package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * Created by yy on 2014/9/17.
 */
public class OtherPackageDictionary {

    //public static final String otherPackageName = "com.android.inputmethod.dict";
    private String otherPackageName;

    Context context;

    public OtherPackageDictionary(Context context, String otherPackageName) {
        this.otherPackageName = otherPackageName;

        this.context = context;
    }

    public Context getContext() {

        try {

            Context otherContext = context.createPackageContext(otherPackageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

            return otherContext;

        } catch (NameNotFoundException e) {

            LogForTest.logW("open other package error :" + e);

        }

        return null;

    }


}
