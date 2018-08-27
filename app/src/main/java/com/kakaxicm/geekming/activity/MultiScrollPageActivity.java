package com.kakaxicm.geekming.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;
import com.kakaxicm.geekming.frameworks.widgets.JudgeNestedScrollView;
import com.kakaxicm.geekming.utils.ScreenUtil;
import com.kakaxicm.geekming.utils.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;

/**
 * Created by chenming on 2018/8/27
 */
public class MultiScrollPageActivity extends BaseActivity {
    SmartRefreshLayout refreshLayout;

    ImageView ivHeader;
    Toolbar toolbar;

    JudgeNestedScrollView scrollView;

    //ScrollView在RefreshLayout中滚动的偏移,用于设置图片的差分偏移
    private int mOffset;
    //当前的垂直滚动距离
    private int mScrollY = 0;
    private int toolBarPositionY;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private View magicIndicatorTitle;

    private ImageView ivBack;
    private ImageView ivMenu;
    private ButtonBarLayout buttonBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_scrollview);
        refreshLayout = findViewById(R.id.refreshLayout);
        toolbar = findViewById(R.id.toolbar);
        ivHeader = findViewById(R.id.iv_header);
        scrollView = findViewById(R.id.scrollView);
        viewPager = findViewById(R.id.view_pager);
        magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicatorTitle = findViewById(R.id.magic_indicator_title);
        ivBack = findViewById(R.id.iv_back);
        ivMenu = findViewById(R.id.iv_menu);
        buttonBarLayout = findViewById(R.id.buttonBarLayout);
        //沉浸式设置和toolbar的高度和padding矫正
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        /**
         * 下拉和松手时候的背景图片和ToolBar处理
         */
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                //图片慢速偏移
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        //设置VP的高度
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                dealWithViewPager();
            }
        });

        //滚动监听
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            //toolbar渐变的阈值
            int toolbarThrehold = DensityUtil.dp2px(170);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.mainWhite) & 0x00ffffff;
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //获取scrollview中的indicator垂直位置,和固定的虚假的indicator位置做比较
                int[] location = new int[2];
                magicIndicator.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {//tablayout悬浮
                    magicIndicatorTitle.setVisibility(View.VISIBLE);
                    scrollView.setNeedScroll(false);//不拦截事件，交给子View Rcv处理，实现rcv滚动
                } else {
                    magicIndicatorTitle.setVisibility(View.GONE);
                    scrollView.setNeedScroll(true);//tablayout没有悬浮时,VP没有滚动到顶部，此时ScrollView拦截事件
                }
                //处理toolbar,滚动距离小于h的时候开始渐变
                if (lastScrollY < toolbarThrehold) {
                    scrollY = Math.min(toolbarThrehold, scrollY);
                    mScrollY = scrollY > toolbarThrehold ? toolbarThrehold : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / toolbarThrehold);
                    toolbar.setBackgroundColor(((255 * mScrollY / toolbarThrehold) << 24) | color);
                    ivHeader.setTranslationY(mOffset - mScrollY);
                }

                if (scrollY == 0) {
                    ivBack.setImageResource(R.drawable.back_white);
                    ivMenu.setImageResource(R.drawable.icon_menu_white);
                } else {
                    ivBack.setImageResource(R.drawable.back_black);
                    ivMenu.setImageResource(R.drawable.icon_menu_black);
                }
            }
        });
    }

    /**
     * 设置VP的高度
     */
    private void dealWithViewPager() {
        toolBarPositionY = toolbar.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = ScreenUtil.getScreenHeightPx(getApplicationContext()) - toolBarPositionY - magicIndicator.getHeight() + 1;
        viewPager.setLayoutParams(params);
    }

}
