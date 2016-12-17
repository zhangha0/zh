package com.example.one;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

@ContentView(R.layout.activity_personal)
public class PersonalActivity extends Activity {
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    @ViewInject(R.id.rlPerTitle)
    RelativeLayout rlPerTitle;
    @ViewInject(R.id.tvPerName)
    TextView tvPerName;
    @ViewInject(R.id.tvPerNum)
    TextView tvPerNum;
    @ViewInject(R.id.miPerTou)
    MyImage miPerTou;
    @ViewInject(R.id.tvPerPass)
    RelativeLayout tvPerPass;
    @ViewInject(R.id.tvHistory)
    RelativeLayout tvHistory;
    @ViewInject(R.id.tvQuit)
    TextView tvQuit;
    @ViewInject(R.id.tvPerBack)
    TextView tvPerBack;
    DbManager dbLogin;
    String strName;
    EditText name;
    int colors[]={0xffca0101,0xff9e6502,0xff0697f7,0xff02949c,0xff71fa62,
            0xfffdfd55,0xffc14aec,0xfff5589f,0xff1bd45c,0xffdfa473
            ,0xffea7024,0xff4b9190,0xffd7ed10,0xffef82bc,0xfff49107};
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                tvPerName.setText(pre.getString("myName", "name"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        funSQL();
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        miPerTou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, HeadChangeActivity.class);
                startActivity(intent);
            }
        });
        rlPerTitle.setBackgroundColor(colors[Integer.valueOf(pre.getString("color","0"))]);
        tvQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalActivity.this)
                        .setTitle("是否退出登录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("login", "no");
                                editor.commit();
                                Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
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
        tvPerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvPerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = new EditText(PersonalActivity.this);
                new AlertDialog.Builder(PersonalActivity.this).setTitle("请输入新的昵称")
                        .setIcon(R.drawable.ic_border_color_black_24dp)
                        .setView(name).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!name.getText().toString().equals("")) {
                            strName = name.getText().toString();
                            editor.putString("myName", strName);
                            editor.commit();
                            WhereBuilder b = WhereBuilder.b();
                            b.and("logNum", "=", pre.getString("logNum", "123"));
                            KeyValue name = new KeyValue("myName", strName);
                            try {
                                handler.sendEmptyMessage(1);
                                dbLogin.update(MyLogin.class, b, name);
                            } catch (DbException e) {
                                Log.e("zh: ", "修改失败");
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PersonalActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .setNegativeButton("取消", null).show();
            }
        });
        tvPerPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });
        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalActivity.this,HistoryPerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        x.image().bind(miPerTou, "assets://" + pre.getString("tou", "noname2") + ".jpg");
        tvPerName.setText(pre.getString("myName", "name"));
        tvPerNum.setText(pre.getString("logNum", "logNum"));
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
