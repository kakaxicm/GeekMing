package com.kakaxicm.geekming.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.RulerView;

@ContentViewAnnotation(value = R.layout.content_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @ViewIdAnnotation(value = R.id.common_adapter_entry)
    private View mCommenAdapterEntryView;
    @ViewIdAnnotation(value = R.id.download_manager_entry)
    private View mDownloadManagerEntry;
    @ViewIdAnnotation(value = R.id.custom_viewgroup_entry)
    private View mCustomViewGroupEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommenAdapterEntryView.setOnClickListener(this);
        mDownloadManagerEntry.setOnClickListener(this);
        mCustomViewGroupEntry.setOnClickListener(this);
        findViewById(R.id.ruler_view_entry).setOnClickListener(this);
        findViewById(R.id.ios_switch_entry).setOnClickListener(this);
        findViewById(R.id.gesture_lock_entry).setOnClickListener(this);
        findViewById(R.id.bezier_entry).setOnClickListener(this);
        findViewById(R.id.wave_view_entry).setOnClickListener(this);
        findViewById(R.id.xfermodel).setOnClickListener(this);
        findViewById(R.id.video_player).setOnClickListener(this);

        checkPermissions();
        Log.e("TEST", "" + ~9);
    }

    /**
     * 读写磁盘权限
     */
    private void checkPermissions() {

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.common_adapter_entry:
                intent = new Intent(this, CommonAdapterListViewActivity.class);
                startActivity(intent);
                break;
            case R.id.download_manager_entry:
                intent = new Intent(this, DownloadManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.custom_viewgroup_entry:
                intent = new Intent(this, CustomViewGroupEntryActivity.class);
                startActivity(intent);
                break;
            case R.id.ruler_view_entry:
                intent = new Intent(this, RulerActivity.class);
                startActivity(intent);
                break;
            case R.id.ios_switch_entry:
                intent = new Intent(this, AppleSwitchActivity.class);
                startActivity(intent);
                break;
            case R.id.gesture_lock_entry:
                intent = new Intent(this, GestureLockActivity.class);
                startActivity(intent);
                break;
            case R.id.bezier_entry:
                intent = new Intent(this, BezierActivity.class);
                startActivity(intent);
                break;
            case R.id.wave_view_entry:
                intent = new Intent(this, WaveBallActivity.class);
                startActivity(intent);
                break;
            case R.id.xfermodel:
                intent = new Intent(this, XfermodeReLearnActivity.class);
                startActivity(intent);
                break;
            case R.id.video_player:
                //TODO 视频播放
                intent = new Intent(this, TinyWindowPlayActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}
