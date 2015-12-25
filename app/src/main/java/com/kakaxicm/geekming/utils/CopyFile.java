package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by yy on 2014/9/16.
 */
public class CopyFile {


//    private static final int [] fileId = {
//            R.raw.main,
//            R.raw.main_ar,
//            R.raw.main_de,
//            R.raw.main_e_en,
//            R.raw.main_e_fr,
//            R.raw.main_en,
//            R.raw.main_es,
//            R.raw.main_fr,
//            R.raw.main_it,
//            R.raw.main_pt_br,
//            R.raw.main_ru,
//    };
//
//    private static final String [] fileName = {
//            "main",
//            "main_ar",
//            "main_de",
//            "main_e_en",
//            "main_e_fr",
//            "main_en",
//            "main_es",
//            "main_fr",
//            "main_it",
//            "main_pt_br",
//            "main_ru",
//    };

    public static final boolean isTest = false;
    public static final int MAX_DICT_SIZE = 8192;

    public static void asyncCopyFileFromID(final int[] id, final String[] newPath, final Context context) {
        if (id.length == 0 || newPath == null || newPath.length == 0 || context == null) {
            return;
        }
        try {
            new Thread("asyncCopyFileFromID") {
                @Override
                public void run() {
                    for (int i = 0; i < id.length; i++)
                        copyFile(id[i], newPath[i], context);
                }

            }.start();
        } catch (Exception e) {
            LogForTest.logW("asyncCopyFileFromID error" + e);
        }

    }

//    public static void copyAllDictionary(final String newPath , final Context context){
//        String [] path = new  String[fileName.length];
//        for(int i = 0 ; i < fileName.length ; i++)
//            path[i] = newPath + fileName[i];
//        asyncCopyFileFromID( fileId , path,  context );
//    }

    public static boolean copyFile(int id, String newPath, Context context) {


        if (id == 0 || newPath == null || newPath == "" || context == null) {
            return false;
        }


        try {

            LogForTest.logW("start copy file to:" + newPath);

            int byteRead = 0;
            int byteSum = 0;
            InputStream inStream = context.getResources().openRawResource(id); //读入原文件
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[MAX_DICT_SIZE];

            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                fs.write(buffer, 0, byteRead);
            }

            inStream.close();
            fs.close();

            LogForTest.logW("end copy file ,size:" + byteSum);

            return true;

        } catch (Exception e) {
            Log.e("yy", "copy file error :" + e);
            e.printStackTrace();

        }


        return false;

    }


    public static void copyFileByPath(String oldPath, String newPath) {


        if (isTest) {

            newPath = "";
        }

        try {

            LogForTest.logW("start copy file from:" + oldPath + " to:" + newPath);

            int byteRead = 0;
            int byteSum = 0;
            InputStream inStream = new FileInputStream(oldPath);
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[MAX_DICT_SIZE];

            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                fs.write(buffer, 0, byteRead);
            }

            inStream.close();
            fs.close();

            LogForTest.logW("end copy file ,size:" + byteSum);

        } catch (Exception e) {
            Log.e("yy", "copy file error :" + e);
            e.printStackTrace();

        }


    }


//    public int getDictFromCopyFile(Context context ,Locale loacale,String path)  {
//
//        Resources localResources = context.getResources();
//        int resourceID = localResources.getIdentifier(context.getPackageName() + ":color/" + resourceName, null, null);
//
//        return resourceID ;
//    }


}
