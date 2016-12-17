package com.example.one;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.widget.LoginButton;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {
    @ViewInject(R.id.iv_sina)
    ImageView iv_sina;
    @ViewInject(R.id.iv_weixin)
    ImageView iv_weixin;
    @ViewInject(R.id.iv_qq)
    ImageView iv_qq;
    @ViewInject(R.id.tvLogBack)
    TextView tvLogBack;
    @ViewInject(R.id.btn_login)
    Button btn_login;
    @ViewInject(R.id.etM)
    EditText etM;
    @ViewInject(R.id.etL)
    EditText etL;
    @ViewInject(R.id.tvForget)
    TextView tvForget;
    @ViewInject(R.id.tvZhu)
    TextView tvZhu;
    @ViewInject(R.id.myTou)
    MyImage myTou;
//    @ViewInject(R.id.btnSina)
//    LoginButton btnSina;
    ImageOptions options;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    DbManager dbLogin;
    MyLogin myLogin;
    MyLogin myLogin2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (myLogin2 != null) {
                    x.image().bind(myTou, "assets://" + myLogin2.tou + ".jpg", options);
                    etL.setText(myLogin2.logNum);
                    etM.setText(myLogin2.passWord);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.e("zh: ", "bundle");
            myLogin2 = (MyLogin) bundle.getSerializable("login");

        }
        x.view().inject(this);
        funSQL();

//        AuthInfo authInfo = new AuthInfo(this, "807216949", Constants.REDIRECT_URL, Constants.SCOPE);

        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        options = new ImageOptions.Builder().setFadeIn(true).setCircular(true).build();
        x.image().bind(iv_sina, "assets://sina2.jpg", options);
        x.image().bind(iv_weixin, "assets://weixin2.jpg", options);
        x.image().bind(iv_qq, "assets://qq2.jpg", options);
        x.image().bind(myTou, "assets://noname2.jpg", options);
        tvLogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "待开发", Toast.LENGTH_SHORT).show();
            }
        });
        iv_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "待开发", Toast.LENGTH_SHORT).show();
            }
        });
        iv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "待开发", Toast.LENGTH_SHORT).show();
            }
        });
        tvZhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        myTou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etL.getText().toString().equals("")) {
                    if (!etM.getText().toString().equals("")) {
                        try {
                            myLogin = dbLogin.selector(MyLogin.class).where("logNum", "=", etL.getText().toString()).findFirst();
                            if (myLogin != null) {
                                if (myLogin.passWord.equals(etM.getText().toString())) {
                                    editor.putString("logNum", myLogin.logNum);
                                    editor.putString("passWord", myLogin.passWord);
                                    editor.putString("tou", myLogin.tou);
                                    editor.putString("myName", myLogin.myName);
                                    editor.putString("login", "yes");
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "该手机号还未注册", Toast.LENGTH_SHORT).show();
                            }
                        } catch (DbException e) {
                            Log.e("zh", "异常 ");
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pre.getString("login", "no").equals("yes")) {
            handler.sendEmptyMessage(1);
        }
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
