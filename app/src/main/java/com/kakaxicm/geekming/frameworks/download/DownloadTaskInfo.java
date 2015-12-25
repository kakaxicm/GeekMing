package com.kakaxicm.geekming.frameworks.download;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kakaxicm on 2015/12/21.
 */
public class DownloadTaskInfo {
    public long mId;//任务ID
    public long mTotalSize;//总大小
    public long mCurrentSize;//当前下载的大小
    private String mName;//文件名
    public String mDownloadUrl;//下载链接
    public String mLocalPath;//文件存储路径
    public int mState;//任务状态
    private List<DownloadCallback> mCallbacks = new ArrayList<>();

    public DownloadTaskInfo(String name, String downloadUrl) {
        mName = name;
        mDownloadUrl = downloadUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDownloadUrl() {
        return mDownloadUrl;
    }

    public void addCallback(DownloadCallback callback) {
        mCallbacks.add(callback);
    }

    public void removeCallback(DownloadCallback callback) {
        mCallbacks.remove(callback);
    }

    public List<DownloadCallback> getCallbacklist() {
        return mCallbacks;
    }
}
