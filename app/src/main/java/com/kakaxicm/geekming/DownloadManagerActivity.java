package com.kakaxicm.geekming;

import android.os.Bundle;
import android.widget.ListView;

import com.kakaxicm.geekming.frameworks.download.DownloadConfig;
import com.kakaxicm.geekming.frameworks.download.DownloadTaskInfo;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.universaladapter.UniversalAdapter;
import com.kakaxicm.geekming.frameworks.universaladapter.UniversalViewHolder;
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
            "天天酷跑",
            "桃子体",
            "宇宙体"
    };
    private String[] mTaskUrls = {
            "http://godfs.3g.cn/gosoft/qudao/go_launcher_ex_721.apk",
            "http://upaicdn.xinmei365.com/newwfs/fontfile/miaozaiti.apk",
            "http://upaicdn.xinmei365.com/newwfs/support/PopStar2TK_LSY_73_20141023_2.0.0.4.apk",
            "http://upaicdn.xinmei365.com/support/TTKPZXB_LSY_09_20141023_3.0.0.1.apk",
            "http://upaicdn.xinmei365.com/wfs/2014-02/9dcfe62769d740629beeb8fc43e052ba.apk",
            "http://upaicdn.xinmei365.com/newwfs/fontfile/yuzhouti.apk"
    };

    @ViewIdAnnotation(value = R.id.listview)
    private ListView mListView;


    List<DownloadTaskInfo> mDownloadInfos;


    private UniversalAdapter<DownloadTaskInfo> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadInfos = new ArrayList<>();
        for (int i = 0; i < mTaskNames.length; i++) {
            DownloadTaskInfo info = new DownloadTaskInfo(mTaskNames[i], mTaskUrls[i]);
            info.mLocalPath = StringUtils.getString(DownloadConfig.DOWNLOAD_PATH, MD5Utils.getMD5(info.getmDownloadUrl()), ".apk");
            mDownloadInfos.add(info);
        }

        mAdapter = new UniversalAdapter<DownloadTaskInfo>(this, mDownloadInfos, R.layout.item_download) {
            @Override
            public void bindView(UniversalViewHolder vh, int postion, DownloadTaskInfo item) {
                vh.setTextView(R.id.name, item.getmName());
            }
        };

        mListView.setAdapter(mAdapter);
    }
}
