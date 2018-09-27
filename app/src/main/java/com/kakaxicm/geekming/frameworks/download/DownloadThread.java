package com.kakaxicm.geekming.frameworks.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.kakaxicm.geekming.frameworks.download.db.DatabaseManager;
import com.kakaxicm.geekming.utils.DeviceUtil;
import com.kakaxicm.geekming.utils.DialogUtils;
import com.kakaxicm.geekming.utils.NetUtils;
import com.kakaxicm.geekming.utils.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Created by kakaxicm on 2015/12/21.
 * 下载任务的最小实现单元
 */
public class DownloadThread extends Thread {
    public final static String ACCEPT = "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
    private Context mContext;
    public boolean mIsRunning;//下载线程的开关
    protected long mStartPosition;//下载的开始位置
    protected long mEndPostion;//下载的结束位置

    private DownloadTaskInfo mInfo;
    private DownloadTask mTask;

    public DownloadThread(Context context, DownloadTask task) {
        mContext = context;
        mTask = task;
        mInfo = task.mInfo;
        mIsRunning = true;
        //任务等待
        mTask.onWait();
    }

    @Override
    public void run() {
        if (!NetUtils.isNetworkConnected(mContext)) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.showShortPromptToast(mContext, "无网络连接，请检查网络设置");
                }
            });
            return;
        }
        mTask.onStart();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", ACCEPT);
        headers.put("Accept-Language", "zh-CN");
        headers.put("Charset", "UTF-8");
        if (mInfo.mTotalSize != 0) {//断点续传
            if (mInfo.mCurrentSize == mInfo.mTotalSize) {
                mInfo.mCurrentSize = mInfo.mTotalSize - 10;
            }
            mStartPosition = mInfo.mCurrentSize;
            mEndPostion = mInfo.mTotalSize;
            headers.put("Range", StringUtils.getString(
                    "bytes=", mStartPosition, "-", mEndPostion));
        }
        headers.put("Connection", "Keep-Alive");
        //下载代码开始
        Downloader downloader = new Downloader(mContext);
        try {
            downloader.downFile(mInfo.getmDownloadUrl(), headers, new Downloader.DownloadProcessor() {

                @Override
                public void processStream(InputStream stream, long totalSize, Header encoding, String newURL) {
                    try {
                        //初始化本地路径
                        initDownloadDir(mInfo.mLocalPath);
                    } catch (Exception e) {
                        mTask.onFail(e.getMessage());
                        mTask.mInfo.mState = DownloadTask.STATE_FAIL;
                        DatabaseManager.getInstance(mContext).updateTaskStateToDb(mInfo.mId, DownloadTask.STATE_FAIL);
                    }
                    if (stream != null) {
                        if (mInfo.mTotalSize == 0) {//info的total字段为0，表明初次下载,更新总大小到数据库
                            //更新总大小到数据库
                            DatabaseManager.getInstance(mContext).updateTaskTotalLengthToDb(mInfo.mId, totalSize);
                            mInfo.mTotalSize = totalSize;
                        }
                        byte[] buffer = new byte[1024 * 8];
                        int readLength = 0;
                        RandomAccessFile raf = null;
                        ByteArrayOutputStream bos = null;

                        try {
                            raf = new RandomAccessFile(StringUtils.getString(mInfo.mLocalPath, ".tmp"), "rwd");
                            raf.seek(mStartPosition);
                            bos = new ByteArrayOutputStream();
                            int tempLength = 0;
                            while ((readLength = stream.read(buffer)) > 0 && mIsRunning) {
                                bos.write(buffer, 0, readLength);
                                mInfo.mCurrentSize += readLength;
                                tempLength += readLength;
                                // 每 10 kb 更新一下界面, 往文件里写一次, 然后清字节流, 写数据库
                                if (tempLength >= 1024 * 10) {
                                    raf.write(bos.toByteArray());
                                    //更新下载进度到数据库
                                    DatabaseManager.getInstance(mContext).updateTaskProgressToDb(mInfo.mId, mInfo.mCurrentSize, DownloadTask.STATE_DOWNLOADING);
                                    mTask.onUpdate((int) (100 * mInfo.mCurrentSize / mInfo.mTotalSize));
                                    bos.reset();
                                    tempLength = 0;
                                }
                            }

                            // 因为线程有可能是手动停止，所以循环退出后还是要再写一次文件
                            raf.write(bos.toByteArray());
                            // 下载完成
                            if (mInfo.mCurrentSize >= mInfo.mTotalSize) {
                                File file = new File(StringUtils.getString(mInfo.mLocalPath + ".tmp"));
                                file.renameTo(new File(mInfo.mLocalPath));
                                mTask.onFinish();
                                //更新进度和状态到数据库
                                DatabaseManager.getInstance(mContext).updateTaskProgressToDb(mInfo.mId, mInfo.mCurrentSize, DownloadTask.STATE_FINISH);
                            }
                        } catch (Exception e) {
                            mTask.onFail(e.getMessage());
                            mTask.mInfo.mState = DownloadTask.STATE_FAIL;
                            //更新状态到数据库
                            DatabaseManager.getInstance(mContext).updateTaskStateToDb(mInfo.mId, DownloadTask.STATE_FAIL);
                        } finally {
                            if (bos != null) {
                                try {
                                    bos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            if (raf != null) {
                                try {
                                    raf.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }, true);
        } catch (HttpException e) {
            mTask.onFail(e.getMessage());
            mTask.mInfo.mState = DownloadTask.STATE_FAIL;
            DatabaseManager.getInstance(mContext).updateTaskStateToDb(mInfo.mId, DownloadTask.STATE_FAIL);
        }
    }

    protected void initDownloadDir(String localPath) throws Exception {
        boolean SDCardIsExist = DeviceUtil.isExitsSdcard();
        if (!SDCardIsExist) {
            throw new Exception("SDcard not exists");
        }
        localPath = localPath.substring(0, localPath.lastIndexOf("/"));
        File downloaddir = new File(localPath);
        if (!downloaddir.exists()) {
            downloaddir.mkdirs();
        }
    }
}

