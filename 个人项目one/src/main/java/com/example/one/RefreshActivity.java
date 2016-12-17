package com.example.one;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.ex.DbException;

import static org.xutils.common.util.DensityUtil.dip2px;

public class RefreshActivity extends Activity {
    boolean flag = true;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    int colors[]={0xffca0101,0xff9e6502,0xff0697f7,0xff02949c,0xff71fa62,
            0xfffdfd55,0xffc14aec,0xfff5589f,0xff1bd45c,0xffdfa473
            ,0xffea7024,0xff4b9190,0xffd7ed10,0xffef82bc,0xfff49107};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        findViewById(R.id.rlRefTitle).setBackgroundColor(colors[Integer.valueOf(pre.getString("color","0"))]);
        TextView tvSetting1 = (TextView) findViewById(R.id.tvSetting1);
        tvSetting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv1 = (TextView) findViewById(R.id.tvMore);
        String text = String.format("自动刷新\n每次打开应用，自动加载最新内容");
        int z = text.lastIndexOf("每");
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new AbsoluteSizeSpan(dip2px(15)), 0, z, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan(dip2px(7)), z, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tv1.setText(style);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawableL = getResources().getDrawable(R.drawable.ic_autorenew_black_24dp);
                if (flag) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_check_box_black_24dp);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(drawableL, null, drawable, null);
                    flag = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(drawableL, null, drawable, null);
                    flag = true;
                }
            }
        });
        TextView tvClean = (TextView) findViewById(R.id.tvClean);
        tvClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RefreshActivity.this)
                        .setTitle("是否要清除所有缓存？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RefreshActivity.this)
                        .setTitle("目前版本：1.0.0（测试版）\n ");
                builder.create().show();
            }
        });
        TextView tvPeo = (TextView) findViewById(R.id.tvPeo);
        tvPeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pre.getString("login", "no").equals("yes")) {
                    Intent intent = new Intent(RefreshActivity.this, PersonalActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(RefreshActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}