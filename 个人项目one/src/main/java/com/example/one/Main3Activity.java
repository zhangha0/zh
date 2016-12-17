package com.example.one;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends Activity {
    DbManager db;
    List<MyCollect> all;
    LayoutInflater li2;
    LinearLayout llCollect;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    int count = 0;
    int num;
    TextView tv3Back;
    ArrayList<View> viewList = new ArrayList<>();
    View view;
    int colors[]={0xffca0101,0xff9e6502,0xff0697f7,0xff02949c,0xff71fa62,
            0xfffdfd55,0xffc14aec,0xfff5589f,0xff1bd45c,0xffdfa473
            ,0xffea7024,0xff4b9190,0xffd7ed10,0xffef82bc,0xfff49107};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3);
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        findViewById(R.id.rlM3Title).setBackgroundColor(colors[Integer.valueOf(pre.getString("color","0"))]);
        tv3Back = (TextView) findViewById(R.id.tv3Back);
        tv3Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        li2 = getLayoutInflater().from(Main3Activity.this);
        llCollect = (LinearLayout) findViewById(R.id.llCollect);
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("myCollect.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        db = x.getDb(daoConfig);
        try {
            all = db.selector(MyCollect.class).findAll();
        } catch (Exception e) {
        }
        
        if (all.size() > 0) {
            for (int i = 0; i < all.size(); i++) {
                view = li2.inflate(R.layout.layout, null);
                MyImage iv = (MyImage) view.findViewById(R.id.ivTitle);
                MyImage miCollect = (MyImage) view.findViewById(R.id.miCollect);
                MyImage miDeal = (MyImage) view.findViewById(R.id.miDeal);
                miDeal.setVisibility(View.GONE);
                viewList.add(view);
                miCollect.setBackgroundResource(R.drawable.ic_delete_black_24dp);
                miCollect.index = count;
                miCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        num = ((MyImage) v).index;
                        AlertDialog.Builder builder = new AlertDialog.Builder(Main3Activity.this)
                                .setTitle("是否要删除该条收藏？")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MyCollect mc = null;
                                        llCollect.removeView(viewList.get(num));
                                        try {
                                            mc = db.selector(MyCollect.class).where("imgId", "=", all.get(num).imgId).findFirst();
                                            db.delete(mc);
                                            Toast.makeText(Main3Activity.this, "已删除", Toast.LENGTH_SHORT).show();
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.create().show();
                    }
                });
                MyTextView tv = (MyTextView) view.findViewById(R.id.tvTitle);
                tv.index = count;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyCollect shareMsg = all.get(((MyTextView) v).index);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("msg", shareMsg);
                        Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
                        intent.putExtras(bundle);
                        ActivityOptionsCompat compat=ActivityOptionsCompat.makeScaleUpAnimation(v,v.getWidth()/2,v.getHeight()/2,0,0);
                        ActivityCompat.startActivity(Main3Activity.this,intent,compat.toBundle());

                    }
                });
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });
                x.image().bind(iv, all.get(i).image);
                tv.setText(all.get(i).title);
                iv.index = count++;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyCollect shareMsg = all.get(((MyImage) v).index);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("msg", shareMsg);
                        Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
                        intent.putExtras(bundle);
                        ActivityOptionsCompat compat=ActivityOptionsCompat.makeScaleUpAnimation(v,v.getWidth()/2,v.getHeight()/2,0,0);
                        ActivityCompat.startActivity(Main3Activity.this,intent,compat.toBundle());

                    }
                });
                llCollect.addView(view);
            }
        } else {
            TextView t = new TextView(this);
            t.setText("没有收藏");
            llCollect.addView(t);
        }
    }
}