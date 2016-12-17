package com.example.one;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 44967 on 2016/11/13.
 */
public class Until3 extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
