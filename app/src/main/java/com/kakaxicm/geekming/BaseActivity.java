package com.kakaxicm.geekming;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.kakaxicm.geekming.frameworks.ioc.ViewInjector;

/**
 * Created by kakaxicm on 2015/12/25.
 */
public class BaseActivity extends Activity implements View.OnClickListener{
    protected Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjector.injectView(this);
        mContext = this;
    }

    @Override
    public void onClick(View v) {
    }
}
