package com.kakaxicm.geekming.frameworks.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by kakaxicm on 2015/12/21.
 * 下载任务的调度单元,负责后台线程和UI线程的交互
 */
public class DownloadTask {
    public DownloadTaskInfo mInfo;//任务的所有信息
    protected DownloadThread mDownloadThread;//下载信息
    //任务调度管理
    protected DownloadManager mDownloadManager;

    public final static int STATE_WAIT = 1;
    public final static int STATE_DOWNLOADING = 2;
    public final static int STATE_STOP = 3;
    public final static int STATE_FAIL = 4;
    public final static int STATE_FINISH = 5;

    //任务状态，用于更新UI
    public final static int CALLBACK_STATE_WAIT = 6;
    public final static int CALLBACK_STATE_START = 7;
    public final static int CALLBACK_STATE_STOP = 8;
    public final static int CALLBACK_STATE_FAIL = 9;
    public final static int CALLBACK_STATE_FINISH = 10;
    public final static int CALLBACK_STATE_UPDATE = 11;

//    public int mState = 0;

    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CALLBACK_STATE_WAIT:
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onWait();
                    }
                    break;
                case  CALLBACK_STATE_START:
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onStart();
                    }
                    break;
                case CALLBACK_STATE_UPDATE:
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onUpdate(msg.arg1);
                    }
                    break;
                case CALLBACK_STATE_FINISH:
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onFinish();
                    }
                    //完成当前任务后，调度下一任务
                    mDownloadManager.finishTask(DownloadTask.this);
                    break;
                case CALLBACK_STATE_FAIL:
                    mDownloadManager.handlerFailedTask(mInfo.mId);
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onFail((String) msg.obj);
                    }
                    break;
                case CALLBACK_STATE_STOP:
                    for (DownloadCallback callback : mInfo.getCallbacklist()) {
                        callback.onStop();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public DownloadTask(Context context, DownloadTaskInfo info) {
        mInfo = info;
        mDownloadThread = new DownloadThread(context, this);
        mDownloadManager = DownloadManager.getInstance(context);
    }


    public void onWait() {
        mUIHandler.sendEmptyMessage(CALLBACK_STATE_WAIT);
    }

    public void onStart() {
        mUIHandler.sendEmptyMessage(CALLBACK_STATE_START);
    }

    public void onUpdate(int progress) {
        Message msg = Message.obtain();
        msg.what = CALLBACK_STATE_UPDATE;
        msg.arg1 = progress;
        mUIHandler.sendMessage(msg);
    }

    public void onFinish() {
        mUIHandler.sendEmptyMessage(CALLBACK_STATE_FINISH);
    }

    public void onFail(String error) {
        Message msg = Message.obtain();
        msg.what = CALLBACK_STATE_FAIL;
        msg.obj = error;
        mUIHandler.sendMessage(msg);
    }

    public void onStop() {
        mUIHandler.sendEmptyMessage(CALLBACK_STATE_STOP);
    }

    public void stop() {
        mDownloadThread.mIsRunning = false;
        onStop();
    }

    public void start() {
        mDownloadThread.mIsRunning = true;
    }

    public DownloadTaskInfo getDownloadInfo() {
        return this.mInfo;
    }

//    public int getState() {
//        return mState;
//    }
}
