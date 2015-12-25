package com.kakaxicm.geekming.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    public static final String getImageCacheFile(Context context) {
        return StringUtils.getString(Environment.getExternalStorageDirectory(), "/", context.getPackageName(), "/cacheImage/");
    }

    public static final String getGifFile(Context context) {
        String path = StringUtils.getString(Environment.getExternalStorageDirectory(), "/", context.getPackageName(), "/gif/");
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        return path;
    }

    /**
     * 复制Assets目录下的文件到指定目录
     */
    public static boolean copyAssets(Context context, String assetsName, String savePath, String saveName) {
        boolean success;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(StringUtils.getString(dir, File.separator, saveName));
        // 如果指定文件中已经有此文件，覆盖
        if (file.exists()) {
            file.delete();
        }
        try {
            InputStream inputStream = context.getResources().getAssets().open(assetsName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            inputStream.close();
            fileOutputStream.close();
            success = true;
        } catch (IOException e) {
            LogUtils.e(context, LOG_TAG, "error to copy assets");
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * Save Bitmap to a file.保存图片到SD卡。
     */
    public static void saveBitmapToFile(Bitmap bitmap, String _file) {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (IOException e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Save byte to a file.保存文件到SD卡。
     */
    public static void saveByteToFile(byte[] bytes, String fileName) {
        File file = new File(fileName);
        int end = fileName.lastIndexOf(File.separator);
        String _filePath = fileName.substring(0, end);
        File filePath = new File(_filePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(fileName);
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readStringFromFileCache(Context context, String fileName) {
        if (context == null || fileName == null) {
            return null;
        }
        try {
            // 打开文件输入流
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buff = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder("");
            // 读取文件内容
            while ((hasRead = fis.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }
            // 关闭文件输入流
            fis.close();
            return sb.toString();
        } catch (Exception e) {
        }
        return null;
    }


    public static void writeStringToFileCache(Context context, String fileName, String content) {
        if (content == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(content)) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取相册图片绝对路径
     */
    public static String getPhotoPath(Context context, Uri contentUri) {
        Cursor cursor;
        String fileName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, contentUri)) {
            String wholeID = DocumentsContract.getDocumentId(contentUri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            fileName = cursor.getString(column_index);
        }
        return fileName;
    }

    /**
     * Get content via a InputStream
     */
    public static String getStringFromInputStream(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 直接从assets读取txt文件
     */
    public static String getStringFromAsset(Context context, String fileName) {
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            String str = new String(bytes);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
