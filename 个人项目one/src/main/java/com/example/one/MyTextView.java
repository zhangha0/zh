package com.example.one;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 44967 on 2016/11/15.
 */
public class MyTextView extends TextView{
    public int index;
    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
