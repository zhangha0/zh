package com.example.one;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by 44967 on 2016/11/14.
 */
public class MyImage extends ImageView{
    public int index;
    public boolean flag=false;
    public String type;
    public MyImage(Context context) {
        super(context);
    }

    public MyImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
