package com.kakaxicm.geekming;

import android.app.Activity;
import android.os.Bundle;

import com.kakaxicm.geekming.frameworks.ioc.ViewInjector;

/**
 * Created by kakaxicm on 2015/12/25.
 */
public class BaseActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjector.injectView(this);
    }
}
