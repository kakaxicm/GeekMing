package com.kakaxicm.geekming.frameworks.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.kakaxicm.geekming.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by star on 15/12/31.
 */
public class FlowLayout extends ViewGroup{
    private static final int DEFAULT_DIP_ITEM_GAP = 5;
    private List<Rect> mChildLayoutRects = new ArrayList<>();//childView的布局位置

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    //确定view的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获得建议模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int resultWidth = 0;//最终的测量结果
        int resultHeight = 0;

        int widthPerLine = 0;//每一行的宽度，最终取最大值
        int heightPerLine = 0;//每一行的高度，取childView的最高值，最终累加它得到高度


        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            //获得child测测量值
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams mp = (MarginLayoutParams) childView.getLayoutParams();
            //child的占用空间
            int childConsumeWidth = childView.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
            int childConsumeHeight = childView.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;

            if(childConsumeWidth + widthPerLine <= sizeWidth) {//未换行,累加宽度，高度取child的最值
                widthPerLine +=  childConsumeWidth;
                heightPerLine = Math.max(heightPerLine, childConsumeHeight);
            } else {//换行, 做最终结果的处理： 宽为最值，高累加
                resultWidth = Math.max(childConsumeWidth, widthPerLine);
                resultHeight += heightPerLine;

                //开启新行
                widthPerLine = childConsumeWidth;
                heightPerLine = childConsumeHeight;
            }

            //最后一行的宽高处理,exp:两次换行，实际是3行
            if(i == childCount - 1) {
                resultWidth = Math.max(resultWidth, widthPerLine);
                resultHeight += heightPerLine;
            }
        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : resultWidth, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : resultHeight);
    }

    private List<Integer> mLineHeights = new ArrayList<>();
    private List<List<View>> mLineViews = new ArrayList<>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLineViews.clear();
        mLineHeights.clear();
        int width = getMeasuredWidth();
        int tempLineWidth = 0;
        int tempLineHeight = 0;
        int childCount = getChildCount();

        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<>();
        for(int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //获得child测测量值
            MarginLayoutParams mp = (MarginLayoutParams) childView.getLayoutParams();
            //child的占用空间
            int childConsumeWidth = childView.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
            int childConsumeHeight = childView.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;
            if(childConsumeWidth + tempLineWidth > width) {//换行
                mLineHeights.add(tempLineHeight);
                mLineViews.add(lineViews);
                tempLineHeight = 0;//next line
                tempLineWidth = 0;
                lineViews = new ArrayList<>();
            }

            tempLineWidth += childConsumeWidth;
            tempLineHeight = Math.max(childConsumeHeight, tempLineHeight);
            lineViews.add(childView);
        }
        //处理最后一行
        mLineHeights.add(tempLineHeight);
        mLineViews.add(lineViews);

        //layout child
        int startLeft = 0;
        int startTop = 0;
        for(int j = 0; j < mLineHeights.size(); j++) {
            lineViews = mLineViews.get(j);
            for(int k = 0; k < lineViews.size(); k++) {
                View view = lineViews.get(k);
                if(view.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams mp = (MarginLayoutParams) view.getLayoutParams();
                int cleft = startLeft + mp.leftMargin;
                int ctop = startTop + mp.topMargin;
                int cright = cleft + view.getMeasuredWidth();
                int cbottom = ctop + view.getMeasuredHeight();

                view.layout(cleft, ctop, cright, cbottom);
                startLeft += mp.leftMargin + mp.rightMargin + view.getMeasuredWidth();
            }

            //下一行布局起点
            startLeft = 0;
            startTop += mLineHeights.get(j);

        }
    }

    @Override
    public void addView(View child) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        int gap = (int) SizeUtils.dp2Px(getResources(), DEFAULT_DIP_ITEM_GAP);
        lp.bottomMargin = gap;
        lp.topMargin = gap;
        lp.leftMargin = gap;
        lp.rightMargin = gap;
        super.addView(child, lp);
    }

}
