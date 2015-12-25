package com.kakaxicm.geekming;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;

@ContentViewAnnotation(value = R.layout.content_main)
public class MainActivity extends BaseActivity implements View.OnClickListener{

    @ViewIdAnnotation(value = R.id.common_adapter_entry)
    private View mCommenAdapterEntryView;
    @ViewIdAnnotation(value = R.id.download_manager_entry)
    private View mDownloadManagerEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommenAdapterEntryView.setOnClickListener(this);
        mDownloadManagerEntry.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
