package com.kakaxicm.geekming;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kakaxicm.geekming.frameworks.ioc.ViewInjector;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;

@ContentViewAnnotation(value = R.layout.content_main)
public class MainActivity extends Activity implements View.OnClickListener{
    @ViewIdAnnotation(value = R.id.common_adapter_entry)
    private View mCommenAdapterEntryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjector.injectContentViewForActivity(this);
        ViewInjector.injectViewsForActivity(this);
        mCommenAdapterEntryView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "hahah", Toast.LENGTH_LONG).show();
    }
}
