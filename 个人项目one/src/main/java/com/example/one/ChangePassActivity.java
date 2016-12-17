package com.example.one;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

@ContentView(R.layout.activity_change_pass)
public class ChangePassActivity extends Activity {
    @ViewInject(R.id.tvChangeBack)
    TextView tvChangeBack;
    @ViewInject(R.id.etChangeOld)
    EditText etChangeOld;
    @ViewInject(R.id.etChangeNew1)
    EditText etChangeNew1;
    @ViewInject(R.id.etChangeNew2)
    EditText etChangeNew2;
    @ViewInject(R.id.btnChange)
    Button btnChange;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    DbManager dbLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        funSQL();
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        x.view().inject(this);
        tvChangeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old=etChangeOld.getText().toString();
                String new1=etChangeNew1.getText().toString();
                String new2=etChangeNew2.getText().toString();
                if(!old.equals("")){
                    if(!new1.equals("")){
                        if(!new2.equals("")){
                            if(pre.getString("passWord","zh").equals(old)){
                                if(new1.equals(new2)){
                                    editor.putString("passWord",new1);
                                    editor.commit();
                                    WhereBuilder b = WhereBuilder.b();
                                    b.and("logNum", "=", pre.getString("logNum", "123"));
                                    KeyValue name = new KeyValue("passWord", new1);
                                    try {
                                        dbLogin.update(MyLogin.class, b, name);
                                    } catch (DbException e) {
                                        Log.e("zh: ", "修改失败");
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(ChangePassActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(ChangePassActivity.this,"新密码有误",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(ChangePassActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(ChangePassActivity.this,"请确认密码",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ChangePassActivity.this,"请填写新密码",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChangePassActivity.this,"请填写旧密码",Toast.LENGTH_SHORT).show();
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
