package com.jacob.viewdraghelper.lesson1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.jacob.viewdraghelper.R;

/**
 * Created by jacob-wj on 2015/4/14.
 *
 * 仿头部放大效果
 */
public class DragLayoutOne extends LinearLayout {
    private static final String TAG = "DragLayoutOne";
    private ViewDragHelper mViewDragHelper;
    private View mBottom, mTop;
    private int mBottomTop, mBottomH;
    private Point mPoint = new Point();

    public DragLayoutOne(Context context) {
        this(context, null);
    }

    public DragLayoutOne(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayoutOne(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1f, new ViewDragCallBack());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mBottomTop = mBottom.getTop();
        mBottomH = mBottom.getMeasuredHeight();
        mPoint.set(mBottom.getLeft(), mBottom.getTop());
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child.getId() == R.id.bottom;
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 拖动的View
         * @param left  移动到x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //两个if主要是让view在ViewGroup中
            if (left < getPaddingLeft()) {
                return getPaddingLeft();
            }

            if (left > getWidth() - child.getMeasuredWidth()) {
                return getWidth() - child.getMeasuredWidth();
            }
            return 0;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            Log.e(TAG, "top:" + top + "++++dy:" + dy);
            int max = Math.max(mBottomTop, top);
            return Math.min(mBottomTop + mBottomH, max);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (releasedChild.getId() == R.id.bottom) {
                mViewDragHelper.settleCapturedViewAt(mPoint.x, mPoint.y);
                invalidate();
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView.getId() == R.id.bottom) {
                int offset = top - mBottomTop;
                float scale = 1.0f * (offset + mTop.getMeasuredHeight()) / mTop.getMeasuredHeight();
                mTop.setPivotX(mTop.getWidth() / 2.0f);
                mTop.setPivotY(0);
                mTop.setScaleX(scale);
                mTop.setScaleY(scale);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                //正在拖动过程中
                case ViewDragHelper.STATE_DRAGGING:
                    break;
                //view没有被拖动，或者正在进行fling
                case ViewDragHelper.STATE_IDLE:
                    break;
                //fling完毕后被放置到一个位置
                case ViewDragHelper.STATE_SETTLING:
                    break;
                default:
                    break;
            }
            super.onViewDragStateChanged(state);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.cancel();
                break;
            default:
                break;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTop = findViewById(R.id.top);
        mBottom = findViewById(R.id.bottom);
    }
}
