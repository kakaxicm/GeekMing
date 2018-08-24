package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.fragment.TestFragment;
import com.kakaxicm.geekming.frameworks.widgets.SimpleViewPagerIndicator;

/**
 * Created by chenming on 2018/8/24
 */
public class ScrollerStickyLayoutActivity extends BaseActivity {
    private SimpleViewPagerIndicator mIndicator;
    private String[] mTitles = new String[]{"苹果", "香蕉", "橘子"};
    private TestFragment[] mFragments = new TestFragment[mTitles.length];
//    private ViewPager mViewPager;
//    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller_stickylayout);
        mIndicator = findViewById(R.id.id_stickynavlayout_indicator);
        mIndicator.setTitles(mTitles);
//        mViewPager = findViewById(R.id.id_stickynavlayout_viewpager);
//        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
//        {
//            @Override
//            public int getCount()
//            {
//                return mTitles.length;
//            }
//
//            @Override
//            public Fragment getItem(int position)
//            {
//                return mFragments[position];
//            }
//
//        };
//
//        mViewPager.setAdapter(mAdapter);
//        mViewPager.setCurrentItem(0);
    }
}
