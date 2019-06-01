/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package dodola.spring;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringListener;

public class SpringFrameLayout extends FrameLayout implements SpringListener {
    private boolean mIsReachBottom;
    private boolean mIsReachTop;
    private boolean mIsTouchMove;
    private int mItemCount;
    private float mLastTranslationY;
    private int mPosition;
    private SpringChain mSpringChain;

    public SpringFrameLayout(Context context) {
        super(context);
    }

    public SpringFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpringFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onSpringUpdate(Spring spring) {
        float val = (float) spring.getCurrentValue();
        setTranslationY((getTranslationY() - mLastTranslationY) + val);
        mLastTranslationY = val;
    }

    public void onSpringChainUpdate(Spring spring, Spring springControl) {
        float distance;
        float val = (float) spring.getCurrentValue();
        float valControl = (float) springControl.getCurrentValue();
        int index = mPosition;
        int ctrlIndex = mSpringChain.getControlSpringIndex();
        float currentTranslationY = getTranslationY();
        int multiple;
        if (mIsReachTop) {
            multiple = index;
            if (index > ctrlIndex) {
                multiple = Math.max(1, ctrlIndex);
            }
            distance = ((float) multiple) * valControl;
            setTranslationY(currentTranslationY + (distance - mLastTranslationY));
        } else if (mIsReachBottom) {
            multiple = (mItemCount - index) - 1;
            if (index < ctrlIndex) {
                multiple = Math.max(1, (mItemCount - ctrlIndex) - 1);
            }
            distance = ((float) multiple) * valControl;
            setTranslationY(currentTranslationY + (distance - mLastTranslationY));
        } else if (mIsTouchMove) {
            distance = val - valControl;
            setTranslationY(currentTranslationY + (distance - mLastTranslationY));
        } else {
            distance = val;
            setTranslationY(currentTranslationY + (distance - mLastTranslationY));
        }
        mLastTranslationY = distance;
    }

    public void onSpringAtRest(Spring spring) {
    }

    public void onSpringActivate(Spring spring) {
    }

    public void onSpringEndStateChange(Spring spring) {
    }

    public void onSpringScrollChanged(boolean isReachTop, boolean isReachBottom, int visibleItemCount) {
        mIsReachTop = isReachTop;
        mIsReachBottom = isReachBottom;
        mItemCount = visibleItemCount;
    }

    public void onSpringTouchChanged(boolean isTouchMove) {
        mIsTouchMove = isTouchMove;
    }

    public void setPositionInSpringChain(int position) {
        mPosition = position;
    }

    public int getPositionInSpringChain() {
        return mPosition;
    }

    public void setSpringChain(SpringChain springChain) {
        mSpringChain = springChain;
    }

    public void setLastTranslationY(float lastTranslationY) {
        mLastTranslationY = lastTranslationY;
    }
}
