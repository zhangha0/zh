package com.example.one;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * Created by 44967 on 2016/12/7.
 */
public class MyRightMenu extends ViewGroup implements View.OnClickListener {

    private final int STATUS_OPEN = 0; //菜单的状态 打开
    private final int STATUS_CLOSE = 1; //菜单的状态 关闭
    private int mRadius;
    private int mStatus;
    private View mMenuButton;

    public MyRightMenu(Context context) {
        this(context, null);
    }

    public MyRightMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRightMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SateliteMenu);
        //定义半径默认值
        float defRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
        mRadius = (int) typedArray.getDimension(R.styleable.SateliteMenu_radius, defRadius);

        typedArray.recycle(); //回收

        mStatus = STATUS_CLOSE; //默认关闭状态

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子view
        for (int i = 0, count = getChildCount(); i < count; i++) {
            //需要传入父view的spec
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override //lt 左上点  rb 右下点  如果 r<l 或 b<t 则无法显示了
    protected void onLayout(boolean changed, int l, int t, int r, int b) {//l=0, t=0  因为是相对于父view的位置
        layoutMenuButton();
        int count = getChildCount();
        t = getChildAt(0).getMeasuredWidth();
        View child;
        int w, h, bb = 0;
        for (int i = 1; i < count; i++) {
            child = getChildAt(i);
            child.setVisibility(View.GONE);
            w = child.getMeasuredWidth();
            h = child.getMeasuredHeight();
            t += h + 20;
            bb = t + h;
            l = getMeasuredWidth() - w;
//            Log.e("zh: ", "l=" + l + "  t=" + t + "  w+l=" + (w + l) + "  bb=" + bb);
            child.layout(l, t, w + l, bb);
        }

    }

    /**
     * 菜单按钮设置layout
     */
    private void layoutMenuButton() {
        mMenuButton = getChildAt(0);
        int l = 0, t = 0;
        int w = mMenuButton.getMeasuredWidth();
        int h = mMenuButton.getMeasuredHeight();
        l = getMeasuredWidth() - w;
        t = 0;
        mMenuButton.layout(l, t, w + l, h + t);
        mMenuButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        toggleMenu(500);
    }

    /**
     * 展开/隐藏子菜单
     * 子菜单动画 平移
     */
    private void toggleMenu(int duration) {

        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            final View child = getChildAt(i);
            int xflag = -1, yflag = 1;
            int oppositeLen = (int) (mRadius * i); //对边 横向
            int adjacentLen = (int) (mRadius * i); //邻边 纵向
            int stopx = xflag * 2;
            int stopy = yflag * adjacentLen;
            AnimationSet set = new AnimationSet(true);
            if (mStatus == STATUS_OPEN) {//如是打开，则要关闭
                //4个值是起始点和结束点,相对于自身x、y的距离
                TranslateAnimation tranAnim = new TranslateAnimation(0, stopx, 0, stopy);
                tranAnim.setStartOffset(mRadius / 6);//偏移
                set.addAnimation(tranAnim);
                AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0);
                set.addAnimation(alphaAnim);
                set.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        setItemClickable(child, false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            } else { //要打开
                TranslateAnimation tranAnim = new TranslateAnimation(stopx, 0, stopy, 0);
                set.addAnimation(tranAnim);
                AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
                set.addAnimation(alphaAnim);
                set.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        setItemClickable(child, false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setItemClickable(child, true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            set.setDuration(duration);
            set.setFillAfter(true);
            child.startAnimation(set);

        }

        if (mStatus == STATUS_OPEN) {
            mStatus = STATUS_CLOSE;
        } else {
            mStatus = STATUS_OPEN;
        }
    }

    private void setItemClickable(View view, boolean flag) {
        view.setClickable(flag);
        view.setFocusable(flag);
    }
}
