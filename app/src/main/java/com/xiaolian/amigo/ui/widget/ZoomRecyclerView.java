package com.xiaolian.amigo.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.xiaolian.amigo.R;

/**
 * @author zcd
 * @date 18/6/27
 */
public class ZoomRecyclerView extends RecyclerView implements View.OnTouchListener {
    /** 非法的手指ID */
    private static final int INVALID_POINTER_ID = -1;

    private Context mContext;

    /** 第一根按下的手指的ID,进行拖动事件处理 */
    private int mMainPointerId = INVALID_POINTER_ID;

    /** 缩放因子*/
    private float mScaleFactor;
    /** 上次触摸点坐标 */
    private float mLastTouchX;// Map what appeared to be clicked in the potentially zoomed view back to the scale = 1 view.
    private float mLastTouchY;
    /** canvas的偏移量 */
    private float mDeltaX;
    private float mDeltaY;
    /** 缩放中心 */
    private float centerX;
    private float centerY;
    /** 缩放因子 */
    private float mInitScaleFactor = 1.0f;
    private float mMidScaleFactor  = mInitScaleFactor*2;
    private float mMaxScaleFactor  = mInitScaleFactor*4;
    /** 双击自动缩放 */
    private boolean isAutoScale;
    private int mAutoTime = 5;
    private float mAutoBigger = 1.07f;
    private float mAutoSmall  = 0.93f;


    /** 单击、双击手势 */
    private GestureDetector mGestureDetector;
    /** 缩放手势*/
    private ScaleGestureDetector mScaleGestureDetector;
    /** 开放监听接口 */
    private OnGestureListener mOnGestureListener;


    public interface OnGestureListener{
        boolean onScale(ScaleGestureDetector detector, float scaleFactor);
        boolean onSingleTapConfirmed(MotionEvent e);
        boolean onDoubleTap(MotionEvent e);
    }

    /** 自动缩放的核心类 */
    private class AutoScaleRunnable implements Runnable {
        /** 目标Scale */
        private float mTargetScale;
        /** Scale变化梯度 */
        private float mGrad;
        /** 缩放中心 */
        private float x, y;

        private AutoScaleRunnable(float TargetScale, float x, float y, float grad) {
            mTargetScale = TargetScale;
            mGrad = grad;
        }

        @Override
        public void run() {
            if ((mGrad > 1.0f && mScaleFactor < mTargetScale)
                    || (mGrad < 1.0f && mScaleFactor > mTargetScale)) {
                mScaleFactor *= mGrad;
                postDelayed(this, mAutoTime);
            } else {
                mScaleFactor = mTargetScale;
            }
            /** 检查边界 */
            checkBorder();
            invalidate();
        }
    }
    public class SupportLinearLayoutManager extends LinearLayoutManager {
        public SupportLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            /** 控制缩放后上下滑动的速度 */
            int result = super.scrollVerticallyBy((int) (dy / mScaleFactor + 0.5), recycler, state);
            if (result == (int) (dy / mScaleFactor + 0.5)) {
                return dy;
            }
            return result;
        }
    }

    /** 数据变化监听 */
    private AdapterDataObserver observer;
    /** 数据为空时显示 */
    private View emptyView;

    public ZoomRecyclerView(Context context) {
        this(context, null);
    }
    public ZoomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ZoomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new SupportLinearLayoutManager(context, VERTICAL, false));
        mContext = context;
        obtainStyledAttributes(attrs);
        initView();
        initDetector();
    }

    /** 从XML文件获取属性 */
    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ZoomRecyclerView);
        for(int i = 0; i < ta.getIndexCount(); i++){
            int attr = ta.getIndex(i);
            if (attr == R.styleable.ZoomRecyclerView_minScaleFactor) {
                mInitScaleFactor = ta.getFloat(attr, 1.0f);
            } else if (attr == R.styleable.ZoomRecyclerView_maxScaleFactor) {
                mMaxScaleFactor = ta.getFloat(attr, mInitScaleFactor * 4);
            } else if (attr == R.styleable.ZoomRecyclerView_autoScaleTime) {
                mAutoTime = ta.getInt(attr, 5);
            }
        }
        ta.recycle();
    }

    /** 初始化View */
    private void initView(){
        mMidScaleFactor = (mInitScaleFactor+mMaxScaleFactor)/2;
        mScaleFactor = mInitScaleFactor;
        isAutoScale = false;

        observer = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                adapterIsEmpty();
            }
        };

    }

    /** 初始化手势监听 */
    private void initDetector() {
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                /** 获取缩放中心 */
                centerX = detector.getFocusX();
                centerY = detector.getFocusY();

                /** 缩放 */
                mScaleFactor *= detector.getScaleFactor();
                mScaleFactor = Math.max(mInitScaleFactor, Math.min(mScaleFactor, mMaxScaleFactor));

                /** 缩放导致偏移 */
//                mDeltaX += centerX * (mScaleFactor - lastScaleFactor);
//                mDeltaY += centerY * (mScaleFactor - lastScaleFactor);
//                checkBorder();//检查边界
                ZoomRecyclerView.this.invalidate();

                if(mOnGestureListener!=null){
                    mOnGestureListener.onScale(detector, mScaleFactor);
                }
                return true;
            }
        });

        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(mOnGestureListener!=null){
//                    return mOnGestureListener.onSingleTapConfirmed(e);
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale) {
                    return true;
                }
//                centerX = e.getX();
//                centerY = e.getY();
                centerX = 0;
                centerY = 0;
                if (mScaleFactor < mMidScaleFactor) {
//                    postDelayed(new AutoScaleRunnable(mMidScaleFactor, centerX, centerY, mAutoBigger), mAutoTime);
                } else if(mScaleFactor < mMaxScaleFactor){
//                    postDelayed(new AutoScaleRunnable(mMaxScaleFactor, centerX, centerY, mAutoBigger), mAutoTime);
                } else {
//                    postDelayed(new AutoScaleRunnable(mInitScaleFactor, centerX, centerY, mAutoSmall), mAutoTime);
                }

                if(mOnGestureListener!=null){
//                    mOnGestureListener.onDoubleTap(e);
                }
                return true;
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        if (mScaleFactor == 1.0f) {
            mDeltaX = 0.0f;
            mDeltaY = 0.0f;
        }

//        anim();
        canvas.translate(mDeltaX, mDeltaY);
        canvas.scale(mScaleFactor, mScaleFactor, centerX, centerY);
        canvas.scale(mScaleFactor, mScaleFactor);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    /**
     * 属性动画解决缩放不能点击问题
     */
    private void  anim(){
//        ObjectAnimator objectAnimatorTransle = ObjectAnimator.ofFloat(this ,"translationX",mDeltaX);
//        ObjectAnimator objectAnimatorTranslaY = ObjectAnimator.ofFloat(this ,"translationY" , mDeltaY);
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(this ,"scaleX" ,mScaleFactor);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(this ,"scaleY" , mScaleFactor);
        objectAnimatorX.setDuration(100);
        objectAnimatorY.setDuration(100);
//        objectAnimatorTranslaY.setDuration(500);
//        objectAnimatorTransle.setDuration(500);
        objectAnimatorX.start();
        objectAnimatorY.start();
//        objectAnimatorTranslaY.start();
//        objectAnimatorTransle.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        /** 单击、双击事件的处理 */
        if (mGestureDetector.onTouchEvent(event)) {
            mMainPointerId = event.getPointerId(0);//防止发生手势事件后,mActivePointerId=-1的情况
            return true;
        }
        /** 缩放事件的处理 */
        mScaleGestureDetector.onTouchEvent(event);

        /** 拖动事件的处理 */
        /** 只获得事件类型值，不获得point的index值 */
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                mMainPointerId = event.getPointerId(0);
                mClickCandidate = true ;
                break;
            case MotionEvent.ACTION_MOVE:
                final int mainPointIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//                int mainPointIndex = event.findPointerIndex(mMainPointerId);
                float mainPointX = event.getX(mainPointIndex);
                float mainPointY = event.getY(mainPointIndex);

                /** 计算与上次坐标的偏移量并累加 */
                mDeltaX += (mainPointX - mLastTouchX);
                mDeltaY += (mainPointY - mLastTouchY);

                /** 保存坐标 */
                mLastTouchX = mainPointX;
                mLastTouchY = mainPointY;

                /** 检查边界 */
                checkBorder();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mMainPointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                mMainPointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                /** 获取抬起手指 */
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mMainPointerId) {
                    /** 抬起手指是主手指,则寻找另一根手指*/
                    int newPointerIndex = (pointerIndex==0?1:0);
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mMainPointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    /**
     * 映射缩放后的View 位置 ，实现RecyclerView的点击事件   **********/

    // Map what appeared to be clicked in the potentially zoomed view back to the scale = 1 view.
    public View getMappedView(View v, float x, float y) {
        View expectedView = null;
        View scanView;


        int mappedX = (int) (getWidth() * (x - mDeltaX) / (getWidth() * getScaleFactor()));
        int mappedY = (int) (getHeight() * (y - mDeltaY) / (getHeight() * getScaleFactor()));

        for (int i = 0; i < getChildCount(); i++) {
            scanView = getChildAt(i);
            if ((mappedX >= scanView.getLeft()) && (mappedX <= scanView.getRight())
                    && (mappedY >= scanView.getTop()) && (mappedY <= scanView.getBottom())) {
                expectedView = scanView;
                break;
            }

        }
        return expectedView;
    }
//
    private boolean mClickCandidate = false;


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mClickCandidate = true;
                break;

            case MotionEvent.ACTION_MOVE:
                mClickCandidate = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mClickCandidate) {
                    View v = getMappedView(view, event.getX() + view.getLeft(),
                            event.getY() + view.getTop());
                    if (v != null) {
                        v.performClick();
                    }
                }
                mClickCandidate = false;
                return true;
        }
        return false;
    }



    /***********************/
    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && observer != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        if (adapter != null && observer != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        adapterIsEmpty();
    }

    /** 判断数据为空,显示emptyView */
    private void adapterIsEmpty() {
        if (emptyView != null) {
            emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
        }
    }

    /** 检查边界 */
    private void checkBorder() {
        /** 左边界 */
        if (mDeltaX > 0.0f)
            mDeltaX = 0.0f;
        /** 右边界 */
        if (-mDeltaX > getWidth() * (mScaleFactor - 1.0f))
            mDeltaX = -getWidth() * (mScaleFactor - 1.0f);
        /** 上边界 */
        if (mDeltaY > 0.0f)
            mDeltaY = 0.0f;
        /** 下边界 */
        if (-mDeltaY > getHeight() * (mScaleFactor - 1.0F))
            mDeltaY = -getHeight() * (mScaleFactor - 1.0f);
    }


    /** setter and getter */
    public View getEmptyView() {
        return emptyView;
    }
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public float getAutoBigger() {
        return mAutoBigger;
    }
    public void setAutoBigger(float autoBigger) {
        this.mAutoBigger = autoBigger;
    }

    public float getAutoSmall() {
        return mAutoSmall;
    }
    public void setAutoSmall(float autoSmall) {
        this.mAutoSmall = autoSmall;
    }

    public int getAutoTime() {
        return mAutoTime;
    }
    public void setAutoTime(int autoTime) {
        this.mAutoTime = autoTime;
    }

    public float getInitScaleFactor() {
        return mInitScaleFactor;
    }
    public void setInitScaleFactor(float initScaleFactor) {
        this.mInitScaleFactor = initScaleFactor;
    }

    public float getMaxScaleFactor() {
        return mMaxScaleFactor;
    }
    public void setMaxScaleFactor(float maxScaleFactor) {
        this.mMaxScaleFactor = maxScaleFactor;
    }

    public float getMidScaleFactor() {
        return mMidScaleFactor;
    }
    public void setMidScaleFactor(float midScaleFactor) {
        this.mMidScaleFactor = midScaleFactor;
    }

    public OnGestureListener getOnGestureListener() {
        return mOnGestureListener;
    }
    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.mOnGestureListener = onGestureListener;
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }
    public void setScaleFactor(float scaleFactor) {
        this.mScaleFactor = scaleFactor;
    }
}
