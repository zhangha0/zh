package com.example.one;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by 44967 on 2016/12/7.
 */
public class MyPopWindow extends PopupWindow{
    View ll;
    Context context;
    public MyPopWindow(LinearLayout ll,Context context){
        super(context);
        this.ll=ll;
        this.context=context;
        setPopupWindow();
    }
    @SuppressLint("InlinedApi")
    private void setPopupWindow() {
        this.setContentView(ll);// 设置View
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_style);// 设置动画
//        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        ll.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = ll.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
