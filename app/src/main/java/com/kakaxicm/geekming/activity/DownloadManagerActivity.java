package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.activity.BaseActivity;
import com.kakaxicm.geekming.frameworks.download.DownloadCallback;
import com.kakaxicm.geekming.frameworks.download.DownloadConfig;
import com.kakaxicm.geekming.frameworks.download.DownloadManager;
import com.kakaxicm.geekming.frameworks.download.DownloadTask;
import com.kakaxicm.geekming.frameworks.download.DownloadTaskInfo;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.utils.MD5Utils;
import com.kakaxicm.geekming.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kakaxicm on 2015/12/25.
 */
@ContentViewAnnotation(value = R.layout.activity_download_listview)
public class DownloadManagerActivity extends BaseActivity {
    private String[] mTaskNames = {
            "go桌面",
            "妙仔体",
            "星星消除",
            "桃子体",
            "宇宙体"
    };
    private String[] mTaskUrls = {
            "http://godfs.3g.cn/gosoft/qudao/go_launcher_ex_721.apk",
            "http://upaicdn.xinmei365.com/newwfs/fontfile/miaozaiti.apk",
            "http://upaicdn.xinmei365.com/newwfs/support/PopStar2TK_LSY_73_20141023_2.0.0.4.apk",
            "http://upaicdn.xinmei365.com/wfs/2014-02/9dcfe62769d740629beeb8fc43e052ba.apk",
            "http://upaicdn.xinmei365.com/newwfs/fontfile/yuzhouti.apk"
    };

    @ViewIdAnnotation(value = R.id.download_tasks_container)
    private LinearLayout mDownloadTaskContainer;

    private DownloadManager mDownloadManager;
    private List<DownloadTaskInfo> mUIDownloadInfos;//用于初始化原始的任务列表,并不是调度中的任务列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadManager = DownloadManager.getInstance(mContext);
        mUIDownloadInfos = new ArrayList<>();
        for (int i = 0; i < mTaskNames.length; i++) {
            DownloadTaskInfo info = new DownloadTaskInfo(mTaskNames[i], mTaskUrls[i]);
            info.mLocalPath = StringUtils.getString(DownloadConfig.DOWNLOAD_PATH, MD5Utils.getMD5(info.getmDownloadUrl()), ".apk");
            mUIDownloadInfos.add(info);
        }

        for(DownloadTaskInfo info : mUIDownloadInfos) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_download, null);
            TextView nameView = (TextView) itemView.findViewById(R.id.name);
            View startDownloadView = itemView.findViewById(R.id.download);
            View stopDownloadView = itemView.findViewById(R.id.stop);
            View restartView = itemView.findViewById(R.id.restart);
            startDownloadView.setTag(info);
            startDownloadView.setOnClickListener(this);

            stopDownloadView.setTag(info);
            stopDownloadView.setOnClickListener(this);

            restartView.setTag(info);
            restartView.setOnClickListener(this);
            /**
             * 从内存到数据库读取这个下载地址的任务信息
             * step1:获取这个url对应的任务ID
             *     1.1:如果有，则走step2
             *     1.2:数据库里面没有这条记录，则表示这个视频从未下载过
             * setp2:从内存中读取，
             *     2.1如果有，则给它直接加上任务监听
             *     2.2 如果没有，则从数据库里读取
             */
            DownloadTaskInfo scheduleTaskInfo = mDownloadManager.getDownloadTaskByUrlFromDb(mContext, info.mDownloadUrl);
            if(scheduleTaskInfo == null) {
                //该任务从来没下载过
            } else {
                //尝试从内存中取任务
                long taskId = scheduleTaskInfo.mId;
                DownloadTask scheduleTask = mDownloadManager.getDownloadTaskByIdFromMemory(taskId);
                if(scheduleTask != null) {
                    scheduleTask.mInfo.addCallback(new TestCallback(itemView));
                }
                //UI处理
                initDownloadItemUI(itemView, scheduleTaskInfo);
            }

            nameView.setText(info.getmName());
            mDownloadTaskContainer.addView(itemView);
        }
    }

    private void initDownloadItemUI(View itemView, DownloadTaskInfo info) {
        TextView stateView = (TextView) itemView.findViewById(R.id.state);
        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.download_pb);

        progressBar.setProgress((int) (100*info.mCurrentSize/info.mTotalSize));
        int state = info.mState;
        switch (state) {
            case DownloadTask.STATE_WAIT:
                stateView.setText("Wait");
                break;
            case DownloadTask.STATE_DOWNLOADING:
                stateView.setText("Start");
                break;
            case DownloadTask.STATE_STOP:
                stateView.setText("Stop");
                break;
            case DownloadTask.STATE_FINISH:
                stateView.setText("Finish");
                break;
            case DownloadTask.STATE_FAIL:
                stateView.setText("Fail");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        DownloadTaskInfo info = (DownloadTaskInfo) v.getTag();//注意这个任务可能不在调度队列！
        switch (v.getId()) {
            case R.id.download:
                info.addCallback(new TestCallback((View) v.getParent()));
                mDownloadManager.addTask(info);
                break;
            case R.id.stop://从数据库取任务ID
                info = mDownloadManager.getDownloadTaskByUrlFromDb(mContext, info.mDownloadUrl);
                if(info != null) {
                    mDownloadManager.stopTask(info.mId);
                }
                mDownloadManager.stopTask(info.mId);
                break;
            case R.id.restart://从数据库取任务
                info = mDownloadManager.getDownloadTaskByUrlFromDb(mContext, info.mDownloadUrl);
                if(info != null) {
                    mDownloadManager.restartDownloadTask(info);
                }
                break;
            default:
                break;
        }
    }

    private class TestCallback implements DownloadCallback {
        private TextView mStateView;
        private ProgressBar mProgressBar;
        public TestCallback(View itemView) {
            mStateView = (TextView) itemView.findViewById(R.id.state);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.download_pb);
        }

        @Override
        public void onWait() {
            mStateView.setText("Wait");
        }

        @Override
        public void onStart() {
            mStateView.setText("Start");
        }

        @Override
        public void onUpdate(int progress) {
            mProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            mStateView.setText("Finish");
        }

        @Override
        public void onFail(String error) {
            mStateView.setText("Fail");
        }

        @Override
        public void onStop() {
            mStateView.setText("Stop");
        }
    }
}
