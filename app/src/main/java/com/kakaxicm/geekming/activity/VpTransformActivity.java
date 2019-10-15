package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakaxicm.geekming.R;

public class VpTransformActivity extends BaseActivity {
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform);
        vp = findViewById(R.id.vp);
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.vp_item, container, false);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                final View view = (View) object;
                container.removeView(view);
            }
        });
        vp.setPageTransformer(true, new TestTransform());
    }

    private static class TestTransform implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            if (position < -1) {
                page.setRotation(0);
            } else if (position <= 1) {// a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
                page.setPivotX(page.getWidth() / 2);
                page.setPivotY(page.getHeight());
                page.setRotation(20 * position);
            } else {
                page.setRotation(0);
            }
        }
    }
}
