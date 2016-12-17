package com.example.one;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by 44967 on 2016/11/28.
 */
public class ParallaxScrollListView extends ListView {
    /**
     * 触摸事件的Y
     **/
    private float mLastY;
    /**
     * ListView头部视图中需要放大的ImageView
     **/
    private ImageView mImg;
    /**
     * ImageView的原始高度
     **/
    private int mImgHeight;
    /**
     * ImageView的布局参数
     **/
    private ViewGroup.LayoutParams mImgParams;
    public static final int STATE_NORMAL = 1;// 正常状态
    private static final int STATE_SCALEING = 2;// 正在缩放状态
    private static final int STATE_SCALE_AUTO = 3;// 自动缩放状态
    private int state = STATE_NORMAL;
    boolean flag=true;
    private Scroller mScroller;

    public ParallaxScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(onScrollListener);
        mScroller = new Scroller(context);
    }

    OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
        }
    };
    private int mFirstVisibleItem;

    /**
     * 2.ACTION_DOWN 返回false，其它返回super.onTouchEvent(ev)
     * ListView不能滚动，且down之后的事件不会再传到onTouch 3.ACTION_DOWN
     * 返回true，其它返回super.onTouchEvent(ev)
     * ListView可以滚动，但不可能手动停止滚动了；down，move，up事件都会传到onTouch方法 4.ACTION_DOWN
     * 返回true,ACTION_MOVE 返回false ListView不能滚动，down，move，up事件都会传到onTouch方法
     * 5.ACTION_DOWN 返回true,ACTION_MOVE 返回true
     * ListView不能滚动，down，move，up事件都会传到onTouch方法 6.ACTION_DOWN
     * 返回super.onTouchEvent(ev)，ACTION_MOVE返回false
     * ListView不能滚动，down，move，up事件都会传到onTouch方法 7.ACTION_DOWN
     * 返回super.onTouchEvent(ev)，ACTION_MOVE返回true
     * ListView不能滚动，down，move，up事件都会传到onTouch方法
     * <p/>
     * 1.默认状态--都返回super.onTouchEvent(ev)
     * ListView可正常滚动，down，move，up事件都会传到onTouch方法
     * 2.如果ACTION_DOWN返回true,ListView可以滚动，但是在滚动过程中不能停止
     * 3.如果ACTION_DOWN返回false,ListView不能滚动，且down之后的事件不会再传到onTouch
     * 所以ACTION_DOWN要返回super.onTouchEvent(ev)
     * 4.如果ACTION_MOVE返回true,意味着将失去父类中对move事件的处理，也将不能滚动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 例如：ACTION_MASK & ACTION_POINTER_2_DOWN
        //即0x000000ff& 0×00000105=0x0000005

        // 可以看到，and运算的结果总是小于等于0x000000ff，那就是说and之后，
        //无论你多少根手指加进来，都是会ACTION_POINTER_DOWN或者ACTION_POINTER_UP
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                return touchDown(ev);
            case MotionEvent.ACTION_MOVE:
                if(flag){
                    touchDown(ev);
                    flag=false;
                }
                return touchMove(ev);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                flag=true;
                if (state == STATE_SCALEING) {
                    collapseImg();
                    state = STATE_SCALE_AUTO;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean touchMove(MotionEvent ev) {
        float offset = ev.getY() - mLastY;
        switch (state) {
            case STATE_NORMAL:
                if (mFirstVisibleItem == 0 && getChildAt(0).getTop() == 0) {
                    if (offset > 0 && mImgParams != null) {
                        state = STATE_SCALEING;
                        mImgParams.height += offset / 2;
                        mImg.setLayoutParams(mImgParams);
                    }
                }
                break;
            case STATE_SCALEING:
                if (offset > 0) {
                    mImgParams.height += offset / 2;
                    if (mImgParams.height >= mImgHeight << 1) {
                        mImgParams.height = (int) (mImgHeight << 1);
                    }
                } else {
                    mImgParams.height += offset;
                    if (mImgParams.height <= mImgHeight) {
                        mImgParams.height = mImgHeight;
                        state = STATE_NORMAL;
                        return super.onTouchEvent(ev);
                    }
                }
                mImg.setLayoutParams(mImgParams);
                mLastY = ev.getY();
                return true;
        }
        mLastY = ev.getY();
        return super.onTouchEvent(ev);
    }

    public boolean touchDown(MotionEvent ev) {
        mLastY = ev.getY();
        switch (state) {
            case STATE_NORMAL:
                if (mImg == null) {
                    mImg = (ImageView) findViewById(R.id.imHead);
                    mImgHeight = mImg.getHeight();
                    mImgParams = mImg.getLayoutParams();
                }
                break;
            case STATE_SCALE_AUTO:
                mScroller.forceFinished(true);
                state = STATE_SCALEING;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 收缩ImageView
     */
    private void collapseImg() {
        mScroller.startScroll(0, mImgParams.height, 0, mImgHeight
                - mImgParams.height, 600);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                mImgParams.height = mScroller.getCurrY();
                mImg.setLayoutParams(mImgParams);
                invalidate();
            }
        } else if (state == STATE_SCALE_AUTO) {
            state = STATE_NORMAL;
        }
    }
}

