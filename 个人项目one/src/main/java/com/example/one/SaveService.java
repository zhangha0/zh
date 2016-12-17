package com.example.one;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveService extends Service {
    DbManager dbSave;
    List<MySave> saveds;
    ArrayList<MySave> mySaves;
    MyReceiver mr;
    IntentFilter filter;
    public SaveService() {
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 123) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<MySave> willSave = new ArrayList<>();
                        try {
//                            saveds = dbSave.selector(MySave.class).where("type", "=", mySaves.get(0).type).findAll();
//                            Log.e("zzz: ", "saveds读取到" + (saveds == null));
//                            if(saveds!=null){
//                                dbSave.delete(saveds);
//                            }
                            dbSave.save(mySaves);
                        } catch (DbException e) {
                            Log.e("zzz: ", "服务读取出错");
                            e.printStackTrace();
                        }
                    }
                }).start();
                Log.e("zzz: ", "handler执行");

            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("zzz: ", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mr = new MyReceiver();
        Log.e("zzz: ", "服务已开启");
        filter = new IntentFilter();
        filter.addAction("flag");
        registerReceiver(mr, filter);
        DbManager.DaoConfig daoConfig2 = new DbManager.DaoConfig()
                .setDbName("mySave.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        dbSave = x.getDb(daoConfig2);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            mySaves = (ArrayList<MySave>) bundle.getSerializable("sa");
            Log.e("zzz: ", "mySaves.size()=" + mySaves.size());
            if (mySaves.size() > 0) {
                Log.e("zzz: ", "发送handler");
                handler.sendEmptyMessage(123);
            }
        }
    }
}
