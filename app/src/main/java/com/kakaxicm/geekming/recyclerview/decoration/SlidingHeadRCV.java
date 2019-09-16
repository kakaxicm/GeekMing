package com.kakaxicm.geekming.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kakaxicm.geekming.R;
import com.kakaxicm.geekming.activity.BaseActivity;
import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvAdapter;
import com.kakaxicm.geekming.frameworks.rcvadapter.UniversalRcvViewHolder;
import com.kakaxicm.geekming.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class SlidingHeadRCV extends BaseActivity {
    private RecyclerView rcv;
    private List<Object> data = new ArrayList<>();
    private UniversalRcvAdapter<Object> adapter = new UniversalRcvAdapter<>(data, new UniversalRcvAdapter.OnMultiTypeBindDataInterface<Object>() {
        @Override
        public int getItemViewType(int postion) {
            if (data.get(postion) instanceof String) {
                return 1;
            }
            return 0;
        }

        @Override
        public void onBindData(Object model, UniversalRcvViewHolder holder, int pos, int type) {
            if (type == 0) {
                holder.setText(R.id.tv1, String.valueOf(data.get(pos)));
            } else {
                holder.setText(R.id.tv, String.valueOf(data.get(pos)));
            }
        }

        @Override
        public int getItemLayoutId(int viewType) {
            if (viewType == 0) {
                return R.layout.item1_conmon_list;
            }
            return R.layout.item_section;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        rcv = findViewById(R.id.id_stickynavlayout_innerscrollview);
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                data.add("Section-" + i / 10);
            } else {
                data.add(i);
            }
        }

        rcv.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        rcv.addItemDecoration(new SectionItemDecoration());
        rcv.setAdapter(adapter);
    }

    private boolean isSection(int index) {
        return data.get(index) instanceof String;
    }

    private class SectionItemDecoration extends RecyclerView.ItemDecoration {
        private Paint paint;
        private Paint textPaint;

        public SectionItemDecoration() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLUE);

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(SizeUtils.sp2px(20, rcv.getContext()));
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final float height = SizeUtils.dp2Px(parent.getResources(), 60);
            SizeUtils.dp2Px(parent.getResources(), 60);
            View firstChild = parent.getChildAt(0);
            int firstVisibleIndex = parent.getChildAdapterPosition(firstChild);
            boolean isHead = isSection(firstVisibleIndex);
            //寻找下一个section
            int starIndex = isHead ? 1 : 0;
            int endIndex = parent.getChildCount() - 1;
            int top = 0;
            for (int i = starIndex; i <= endIndex; i++) {
                View child = parent.getChildAt(i);
                int pos = parent.getChildAdapterPosition(child);
                if (isSection(pos)) {
                    if (child.getTop() - height <= 0) {
                        top = (int) Math.min(0, child.getTop() - height);
                    }
                    break;
                }
            }

            int left = firstChild.getLeft();
            int right = firstChild.getRight();
            int bottom = (int) (top + height);
            c.drawRect(left, top, right, bottom, paint);

            String curSection = (String) data.get(10 * (firstVisibleIndex / 10));
            Rect rect = new Rect();
            textPaint.getTextBounds(curSection, 0, curSection.length(), rect);
            int textHeight = rect.height();
            int textTop = (int) (top + (height - textHeight) / 2 + textHeight);
            int textLeft = left;
            c.drawText(curSection, textLeft, textTop, textPaint);
        }
    }
}
