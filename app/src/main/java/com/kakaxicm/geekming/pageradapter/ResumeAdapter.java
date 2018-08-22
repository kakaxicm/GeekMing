package com.kakaxicm.geekming.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kakaxicm.geekming.fragment.TestFragment;

/**
 * Created by chenming on 2018/8/22
 */
public class ResumeAdapter extends FragmentPagerAdapter {
    String[] TABS = {"TAB1", "TAB2", "TAB3", "TAB4"};

    public ResumeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new TestFragment();
    }

    @Override
    public int getCount() {
        return TABS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TABS[position];
    }

}
