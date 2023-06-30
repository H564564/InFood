package com.example.infood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

public class StretchScrollView extends NestedScrollView {

    // 子View
    private View innerView;
    // 上次手势事件的y坐标
    private float mLastY;
    // 记录子View的正常位置
    private Rect normal = new Rect();

    public StretchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        initView();
        super.onFinishInflate();
    }

    /**
     * 获取ScrollView的子布局
     */
    private void initView() {
        // 去除原本ScrollView滚动到边界时的阴影效果
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (getChildAt(0) != null) {
            innerView = getChildAt(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                // 手指松开恢复
                if (!normal.isEmpty()) {
                    planAnimation();
                    normal.setEmpty();
                    mLastY = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                // 滑动距离
                int distanceY = (int) (mLastY - currentY);

                // 处理Y轴的滚动事件，当滚动到最上或者最下时需要移动布局
                // 手指刚触及屏幕时，也会触发此事件，此时mLastY的值还是0，会立即触发一个比较大的移动。这里过滤掉这种情况
                if (isNeedTranslate() && mLastY != 0) {
                    if (normal.isEmpty()) {
                        // 保存正常的布局位置
                        normal.set(innerView.getLeft(), innerView.getTop(), innerView.getRight(), innerView.getBottom());
                    }
                    // 移动布局， 使distance / 2 防止平移过快
                    innerView.layout(innerView.getLeft(), innerView.getTop() - distanceY / 2,
                            innerView.getRight(), innerView.getBottom() - distanceY / 2);
                }
                mLastY = currentY;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 回缩动画
     */
    public void planAnimation() {
        // 开启移动动画
        TranslateAnimation animation = new TranslateAnimation(0, 0, innerView.getTop(), normal.top);
        animation.setDuration(200);
        innerView.startAnimation(animation);
        // 补间动画并不会真正修改innerView的位置，这里需要设置使得innerView回到正常的布局位置
        innerView.layout(normal.left, normal.top, normal.right, normal.bottom);
    }

    /**
     * 是否需要Y移动布局
     */
    public boolean isNeedTranslate() {
        int offset = innerView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 顶部或者底部
        return scrollY == 0 || scrollY == offset;
    }
}
