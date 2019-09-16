package com.kakaxicm.geekming.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class TestItemDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;
    private int DIV_HEIGHT = 10;

    public TestItemDecoration() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    /**
     * 设置item内容的偏移
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, DIV_HEIGHT);
    }

    /**
     * 先于item绘制
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //遍历
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            int left = parent.getPaddingLeft();

            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = (int) (child.getBottom() + layoutParams.bottomMargin + child.getTranslationY());
            int right = parent.getWidth() - parent.getPaddingRight();
            int bottom = top + DIV_HEIGHT;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

    /**
     * 后于item绘制
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
