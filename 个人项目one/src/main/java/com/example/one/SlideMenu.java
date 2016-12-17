package com.example.one;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
/**
 * Created by 44967 on 2016/11/12.
 */
public class SlideMenu extends HorizontalScrollView{
    private LinearLayout max;
    private LinearLayout menu;
    private LinearLayout content;
    private int screenWidth;
    private int screenHeight;
    private int padding;
    boolean flag=true;
    public SlideMenu(Context context) {
        super(context);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight=displayMetrics.heightPixels;
        screenWidth=displayMetrics.widthPixels;
        padding= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120,context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(flag) {
            max = (LinearLayout) getChildAt(0);
            menu = (LinearLayout) max.getChildAt(0);
            content = (LinearLayout) max.getChildAt(1);
            max.getLayoutParams().height = screenHeight;
            menu.getLayoutParams().height = screenHeight;
            menu.getLayoutParams().width = (screenWidth - padding);
            content.getLayoutParams().width = screenWidth;
            flag=false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            this.scrollTo(screenWidth-padding,0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_UP){
            int t=getScrollX();
            if(t<=(screenWidth-padding)/2){
                this.smoothScrollTo(0,0);
            }else{
                this.smoothScrollTo(screenWidth-padding,0);
            }
        }
        return super.onTouchEvent(event);
    }
}
