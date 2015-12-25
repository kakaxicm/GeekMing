package com.kakaxicm.geekming.frameworks.download;

/**
 * Created by kakaxicm on 2015/12/21.
 */
public interface DownloadCallback {
    void onWait();
    void onStart();
    void onUpdate(int progress);
    void onFinish();
    void onFail(String error);
    void onStop();
}
