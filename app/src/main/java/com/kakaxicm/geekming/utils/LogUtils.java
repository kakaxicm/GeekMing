package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

/**
 * Created by huyongsheng on 2014/7/18.
 * <p>
 * 当apk处于debug时显示log，否则不显示log
 */
public class LogUtils {

    public static void d(Context context, String tag, Object... msg) {
        if (isDebuggable(context)) {
            Log.d(tag, StringUtils.getString(msg));
        }
    }

    public static void e(Context context, String tag, Object... msg) {
        if (isDebuggable(context)) {
            Log.e(tag, StringUtils.getString(msg));
        }
    }

    public static void i(Context context, String tag, Object... msg) {
        if (isDebuggable(context)) {
            Log.i(tag, StringUtils.getString(msg));
        }
    }

    public static void showTime(Context context, Object... msg) {
        if (isDebuggable(context)) {
            Log.e("System Time:", StringUtils.getString(System.currentTimeMillis(), "----", StringUtils.getString(msg)));
        }
    }

    private static boolean isDebuggable(Context context) {
        boolean debuggable = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = packageInfo.signatures;
            for (Signature signature : signatures) {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signature.toByteArray());
                X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(stream);
                debuggable = x509Certificate.getSubjectX500Principal().equals(new X500Principal("CN=Android Debug,O=Android,C=US"));
                if (debuggable) {
                    break;
                }
            }
        } catch (PackageManager.NameNotFoundException | CertificateException e) {
            e.printStackTrace();
        }
        return debuggable;
    }
}
