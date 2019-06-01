package dodola.spring;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.facebook.rebound.SpringChain;

public class RecyclerViewWrapper extends RecyclerView {
    private static ArrayList<OnScrollListener> sListeners = new ArrayList<>();
    private int mActivePointerId = -1;
    private boolean mFirstAtTopOrBottom = true;
    private boolean mFirstMove = true;
    private boolean mReachBottom;
    private boolean mReachTop;
    private final SpringChain mSpringChain = SpringChain.create(40, 6, 70, 10);
    private float mStartY;
    private int[] mVelocity;
    private VelocityTracker mVelocityTracker;

    public RecyclerViewWrapper(Context context) {
        super(context);
        init();
    }

    public RecyclerViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mVelocity = new int[6];
    }

    public void setOnScrollListener(OnScrollListener listener) {
        super.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (OnScrollListener l : sListeners) {
                    l.onScrolled(recyclerView, dx, dy);
                }
            }
        });
        sListeners.add(listener);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        sListeners.add(listener);
    }

    public void removeScrollListeners() {
        sListeners.clear();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        int pointerIndex;
        int x;
        int y;
        int i;
        switch (event.getActionMasked()) {
            case 1:
            case 3:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex >= 0) {
                    x = (int) (event.getX(pointerIndex) + 0.5f);
                    y = (int) (event.getY(pointerIndex) + 0.5f);
                    for (i = 0; i < getChildCount(); i++) {
                        ((SpringFrameLayout) getChildAt(i).findViewById(R.id.container)).onSpringTouchChanged
                                (false);
                    }
                    if (mReachTop || mReachBottom) {
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(16);
                        mSpringChain.getControlSpring().setEndValue(0.0d);
                    } else {
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(16);
                        mSpringChain.getControlSpring()
                                .setCurrentValue((double) mVelocityTracker.getYVelocity()).setEndValue(0.0d);
                    }
                    mVelocityTracker.clear();
                    mFirstMove = true;
                    break;
                }
                return false;
            case 2:
                int firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                int visibleItemCount = getChildCount();
                int totalItemCount = ((LinearLayoutManager) getLayoutManager()).getItemCount();
                if (!mFirstMove) {
                    pointerIndex = event.findPointerIndex(mActivePointerId);
                    if (pointerIndex >= 0) {
                        x = (int) (event.getX(pointerIndex) + 0.5f);
                        y = (int) (event.getY(pointerIndex) + 0.5f);
                        if (firstVisibleItem == 0) {
                            View firstView = getChildAt(firstVisibleItem);
                            if (firstView == null || firstView.getTop() + getPaddingTop() < 0
                                    || ((float) y) - mStartY <= 0.0f) {
                                mReachTop = false;
                            } else {
                                mReachTop = true;
                            }
                        } else {
                            mReachTop = false;
                        }
                        if (firstVisibleItem + visibleItemCount == totalItemCount) {
                            View lastView = getChildAt(visibleItemCount - 1);
                            if (lastView == null || lastView.getBottom() + getPaddingBottom() > getHeight()
                                    || totalItemCount < visibleItemCount || ((float) y) - mStartY >= 0.0f) {
                                mReachBottom = false;
                            } else {
                                mReachBottom = true;
                            }
                        } else {
                            mReachBottom = false;
                        }
                        for (i = 0; i < getChildCount(); i++) {
                            SpringFrameLayout layout = (SpringFrameLayout) getChildAt(i).findViewById(R.id
                                    .container);
                            layout.onSpringScrollChanged(mReachTop, mReachBottom, totalItemCount);
                            layout.onSpringTouchChanged(true);
                        }
                        if (!mReachTop && !mReachBottom) {
                            mFirstAtTopOrBottom = true;
                        } else if (mFirstAtTopOrBottom) {
                            mStartY = (float) y;
                            mFirstAtTopOrBottom = false;
                        }
                        if (!mReachTop && !mReachBottom) {
                            mVelocityTracker.addMovement(event);
                            break;
                        }
                        float distance = (((float) y) - mStartY) * 0.05f;
                        mVelocityTracker.clear();
                        mSpringChain.getControlSpring().setCurrentValue((double) distance);
                        if (mReachTop && ((float) y) - mStartY > 0.0f) {
                            return true;
                        }
                        if (mReachBottom && ((float) y) - mStartY < 0.0f) {
                            return true;
                        }
                    }
                    return false;
                }
                mActivePointerId = event.getPointerId(0);
                int initialTouchX = (int) (event.getX() + 0.5f);
                int initialTouchY = (int) (event.getY() + 0.5f);
                mVelocityTracker.addMovement(event);
                int itemPosition =
                        getChildLayoutPosition(findChildViewUnder((float) initialTouchX, (float) initialTouchY));
                if (itemPosition == -1) {
                    itemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                }
                mSpringChain.setControlSpringIndex(itemPosition).getControlSpring().setCurrentValue(0.0d);
                mFirstMove = false;
                mFirstAtTopOrBottom = true;
                mStartY = (float) initialTouchY;
                break;
        }
        return super.onTouchEvent(event);
    }

    public SpringChain getSpringChain() {
        return mSpringChain;
    }
}
