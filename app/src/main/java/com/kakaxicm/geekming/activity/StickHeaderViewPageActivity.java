package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.activity.BaseActivity;
import com.kakaxicm.geekming.pageradapter.ResumeAdapter;

/**
 * Created by chenming on 2018/8/22
 */
public class StickHeaderViewPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 设置返回主页的按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ((NestedScrollView) findViewById(R.id.nestedScrollView)).setFillViewport(true);
        // ViewPager
        ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);
        ResumeAdapter mPagerAdapter = new ResumeAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mPager);
        // ViewPager切换时NestedScrollView滑动到顶部
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
