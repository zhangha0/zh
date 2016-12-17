package com.example.one;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends Activity {
    @ViewInject(R.id.tvRegBack)
    TextView tvRegBack;
    @ViewInject(R.id.etRegName)
    EditText etRegName;
    @ViewInject(R.id.etRegNum)
    EditText etRegNum;
    @ViewInject(R.id.etRegPassworld)
    EditText etRegPassworld;
    @ViewInject(R.id.btnReg)
    Button btnReg;
    @ViewInject(R.id.tvRgeAgree)
    TextView tvRgeAgree;
    @ViewInject(R.id.tvRegText)
    TextView tvRegText;
    boolean flag = false;
    MyLogin myLogin;
    DbManager dbLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        funSQL();
        String text = String.format("我已阅读并同意服务条款");
        int z = text.lastIndexOf("服");
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(Color.BLUE), z, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvRegText.setText(style);
        tvRgeAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    v.setBackgroundResource(R.drawable.yes);
                    flag = true;
                } else {
                    v.setBackgroundResource(R.drawable.no);
                    flag = false;
                }
            }
        });
        tvRegBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etRegName.getText().toString();
                String regNum=etRegNum.getText().toString();
                String passWord=etRegPassworld.getText().toString();
                if (!name.equals("")) {
                    if (!regNum.equals("")) {
                        if (!passWord.equals("")) {
                            if (flag) {
                                myLogin=new MyLogin(name,"noname2",regNum,passWord);
                                try {
                                    MyLogin ml=dbLogin.selector(MyLogin.class).where("logNum","=",regNum).findFirst();
                                    if(ml==null){
                                        dbLogin.save(myLogin);
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "该手机号已注册", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e( "zh ","存储出错" );
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(RegisterActivity.this, "是否同意协议？", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
