package com.kakaxicm.geekming.behavior.slidingbehavior;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.kakaxicm.geekming.R;

/**
 * 作者：余天然 on 16/9/13 下午11:16
 */
public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingCardView> {

    private int mDefaultOffset;//默认偏移值

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, SlidingCardView child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        int childIndex = getIndexOfChild(parent, child);
        if (childIndex != -1) {
            int offset = childIndex * parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
            //TODO 问题1 做向下偏移的时候，卡片的高度没有变化，导致整个卡片布局下沉，底部有一部分显示不出来,需要重新矫正高度
            child.offsetTopAndBottom(offset);
        }

        mDefaultOffset = child.getTop();
        return true;
    }

    /**
     * 在摆放childview前，测量child，该步骤不是必须，这里是为了解决 TODO 问题1
     *
     * @param parent
     * @param child
     * @param parentWidthMeasureSpec
     * @param widthUsed
     * @param parentHeightMeasureSpec
     * @param heightUsed
     * @return
     */
    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, SlidingCardView child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        //卡片的高度为父布局高度 - (count-1)*标题栏高度
        int offset = getChildMeasureOffset(parent, child);
        if (getIndexOfChild(parent, child) < parent.getChildCount() - 1) {
            offset += parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
        }
        int parentHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
        int height = parentHeight - offset;
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        child.measure(parentWidthMeasureSpec, heightMeasureSpec);
        return true;
    }

    /**
     * 获取卡片的默认偏移值
     *
     * @param parent
     * @return
     */
    private int getChildMeasureOffset(CoordinatorLayout parent, SlidingCardView child) {
        int perOffset = parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
        int cardCount = 0;
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View view = parent.getChildAt(i);
            //排除自己
            if (view != child && view instanceof SlidingCardView) {
                cardCount++;
            }
        }
        return perOffset * cardCount;
    }

    /**
     * 获取卡片的index,如果不是则返回-1
     *
     * @param parent
     * @param child
     * @return
     */
    private int getIndexOfChild(CoordinatorLayout parent, SlidingCardView child) {
        //获取当前子View的index
        if (child instanceof SlidingCardView) {
            int index = parent.indexOfChild(child);
            return index;
        }
        return -1;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull SlidingCardView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        //选中的卡片垂直滚动
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 &&
                child == directTargetChild;//第二个条件是只指定被触摸的卡片滑动,如果只有一个垂直条件，那么所有child都会滑动
    }

    /**
     * @param parent
     * @param child
     * @param target
     * @param dx
     * @param dy
     * @param consumed 父View告诉子View消耗了多少距离
     * @param type
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout parent, @NonNull SlidingCardView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //限定当前child的滚动范围
        int minOffset = mDefaultOffset;
        int headHeight = parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
        int index = parent.indexOfChild(child);
        int bottomCardCount = parent.getChildCount() - index;
        int maxOffset = parent.getMeasuredHeight() - headHeight*bottomCardCount;
        int initOffset = child.getTop();
        //1。开启单个child的滑动
        //dy向上的时候+,向下的时候-
        //拿到当前滚动的距离
        int offset = clamp(initOffset - dy, minOffset, maxOffset) - initOffset;
        child.offsetTopAndBottom(offset);
        //TODO 问题2：卡片之间的联合滚动
        consumed[1] = -offset;
        //2。开启碰撞滑动
        shiftSlide(consumed[1], parent, child);
    }

    /**
     * TODO 问题2 卡片建的碰撞联动
     *
     * @param offset
     * @param parent
     * @param child
     */
    private void shiftSlide(int offset, CoordinatorLayout parent, SlidingCardView child) {
        int headHeight = parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
        if (offset == 0) {
            return;
        } else if (offset > 0) {//向上滑
            SlidingCardView curChild = child;
            SlidingCardView previousChild = getPreviousChild(parent, child);
            //循环递推，
            while (previousChild != null) {
                //一次循环中，检测当前child和preChild之间的碰撞推动关系
                //当前child的top
                int preOffset = getOffsetFromTopToBottom(headHeight, previousChild, curChild);
                if (preOffset > 0) {
                    previousChild.offsetTopAndBottom(-preOffset);
                }
                curChild = previousChild;
                previousChild = getPreviousChild(parent, curChild);
            }
        } else {//向下滑
            SlidingCardView curChild = child;
            SlidingCardView nextChild = getNextChild(parent, child);
            //循环递推，
            while (nextChild != null) {
                //一次循环中，检测当前child和preChild之间的碰撞推动关系
                //当前child的top
                int nextOffset = getOffsetFromTopToBottom(headHeight, curChild, nextChild);
                if (nextOffset > 0) {
                    nextChild.offsetTopAndBottom(nextOffset);
                }
                curChild = nextChild;
                nextChild = getNextChild(parent, curChild);
            }
        }
    }

    private int getOffsetFromTopToBottom(int headHeight, SlidingCardView topChild, SlidingCardView bottomChild) {
        int botomChildTop = bottomChild.getTop();
        int topChildHeaderBottom = topChild.getTop() + headHeight;
        int offset = topChildHeaderBottom - botomChildTop;
        return offset;
    }

    /**
     * 处理其它的滑动
     *
     * @param scrollY
     * @param parent
     * @param child
     */
    private void shiftScroll(int scrollY, CoordinatorLayout parent, SlidingCardView child) {
        int headHeight = parent.getResources().getDimensionPixelSize(R.dimen.card_header_height);
        if (scrollY == 0) return;
        if (scrollY > 0) {//往上推
            SlidingCardView current = child;
            SlidingCardView card = getPreviousChild(parent, current);
            while (card != null) {
                int delta = calcOtherOffset(card, current, headHeight);
                if (delta > 0) {
                    card.offsetTopAndBottom(-delta);
                }
                current = card;
                card = getPreviousChild(parent, current);
            }
        } else {//往下推
            SlidingCardView current = child;
            SlidingCardView card = getNextChild(parent, current);
            while (card != null) {
                int delta = calcOtherOffset(current, card, headHeight);
                if (delta > 0) {
                    card.offsetTopAndBottom(delta);
                }
                current = card;
                card = getNextChild(parent, current);
            }

        }
    }

    /**
     * 计算卡片之间的偏移值
     *
     * @param above
     * @param below
     * @return
     */
    private int calcOtherOffset(SlidingCardView above, SlidingCardView below, int headHeight) {
        return above.getTop() + headHeight - below.getTop();
    }

    /**
     * 获取上一个卡片
     *
     * @param parent
     * @param child
     * @return
     */
    private SlidingCardView getPreviousChild(CoordinatorLayout parent, SlidingCardView child) {
        int cardIndex = parent.indexOfChild(child);
        Log.e("onLayoutChild", "cardIndex = " + cardIndex);
        for (int i = cardIndex - 1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardView) {
                return (SlidingCardView) view;
            }
        }
        return null;
    }

    /**
     * 获取下一个卡片
     *
     * @param parent
     * @param child
     * @return
     */
    private SlidingCardView getNextChild(CoordinatorLayout parent, SlidingCardView child) {
        int cardIndex = parent.indexOfChild(child);
        for (int i = cardIndex + 1; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardView) {
                return (SlidingCardView) view;
            }
        }
        return null;
    }

    /**
     * 取上下限之间的值
     *
     * @param i
     * @param minOffset
     * @param maxOffset
     * @return
     */
    private int clamp(int i, int minOffset, int maxOffset) {
        if (i < minOffset) {
            return minOffset;
        } else if (i > maxOffset) {
            return maxOffset;
        } else {
            return i;
        }
    }
}
