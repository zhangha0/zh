package com.example.one;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

public class HeadChangeActivity extends Activity implements View.OnClickListener {
    ArrayList<String> list = new ArrayList<>();
    int count;
    ImageOptions options;
    LinearLayout llTous;
    View vLast;
    String tou;
    TextView tvHeadSure;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    DbManager dbLogin;
    TextView tvHeadBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_change);
        funSQL();
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        tvHeadBack = (TextView) findViewById(R.id.tvHeadBack);
        tvHeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llTous = (LinearLayout) findViewById(R.id.llTous);
        tvHeadSure = (TextView) findViewById(R.id.tvHeadSure);
        tvHeadSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tou != null) {
                    editor.putString("tou", "tou/" + tou);
                    editor.commit();
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("logNum", "=", pre.getString("logNum", "123"));
                        KeyValue name = new KeyValue("tou", "tou/" + tou);
                        dbLogin.update(MyLogin.class, b, name);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    finish();
                }

            }
        });
        for (int i = 1; i < 13; i++) {
            list.add("boy" + i);
        }
        for (int i = 1; i < 19; i++) {
            list.add("gril" + i);
        }
        for (int i = 1; i < 12; i++) {
            list.add("other" + i);
        }
        count = 0;
        options = new ImageOptions.Builder().setFadeIn(true).setCircular(true).build();
        for (int i = 0; i < 13; i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_head_item, null);
            MyImage im1 = (MyImage) v.findViewById(R.id.miItem1);
            im1.type = list.get(count++);
            im1.setOnClickListener(this);
            x.image().bind(im1, "assets://tou/" + im1.type + ".jpg", options);
            MyImage im2 = (MyImage) v.findViewById(R.id.miItem2);
            im2.type = list.get(count++);
            im2.setOnClickListener(this);
            x.image().bind(im2, "assets://tou/" + im2.type + ".jpg", options);
            MyImage im3 = (MyImage) v.findViewById(R.id.miItem3);
            im3.type = list.get(count++);
            im3.setOnClickListener(this);
            x.image().bind(im3, "assets://tou/" + im3.type + ".jpg", options);
            llTous.addView(v);
        }
    }

    @Override
    public void onClick(View v) {
        if (vLast != null) {
            vLast.setBackgroundColor(0xc3e2d1);
        }
        vLast = v;
//        v.setBackgroundColor(0xb6bab8);0xf48c05
        v.setBackgroundColor(Color.GRAY);
        tvHeadSure.setBackgroundColor(0xff659ff1);
        tou = ((MyImage) v).type;
    }

    public void funSQL() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("mySave.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        dbLogin = x.getDb(daoConfig);
    }
}
