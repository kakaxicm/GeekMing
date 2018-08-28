package com.kakaxicm.geekming.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.RulerView;

@ContentViewAnnotation(value = R.layout.content_main)
public class MainActivity extends BaseActivity implements View.OnClickListener{

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
            default:
                break;
        }
    }
}
