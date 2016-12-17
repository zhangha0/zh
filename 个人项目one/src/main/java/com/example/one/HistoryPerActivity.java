package com.example.one;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryPerActivity extends Activity {
    ListView lvHis;
    TextView tvHisBack;
    DbManager dbLogin;
    BaseAdapter ba;
    List<MyLogin> myLogins=new ArrayList<>();
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_per);
        funSQL();
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        try {
            myLogins=dbLogin.selector(MyLogin.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        lvHis= (ListView) findViewById(R.id.lvHis);
        tvHisBack= (TextView) findViewById(R.id.tvHisBack);
        tvHisBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ba=new BaseAdapter() {
            @Override
            public int getCount() {
                return myLogins.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                convertView= LayoutInflater.from(HistoryPerActivity.this).inflate(R.layout.layout_search,null);
                ImageView imageView= (ImageView) convertView.findViewById(R.id.imSearched);
                x.image().bind(imageView,"assets://"+myLogins.get(position).tou+".jpg");
                TextView textView= (TextView) convertView.findViewById(R.id.tvSearch_UP);
                textView.setText(myLogins.get(position).myName);
                TextView textView1= (TextView) convertView.findViewById(R.id.tvSearch_DOWN);
                textView1.setText(myLogins.get(position).logNum);
                TextView textView2= (TextView) convertView.findViewById(R.id.tvMa);
                textView2.setBackgroundResource(R.drawable.ic_swap_horiz_black_24dp);
                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!myLogins.get(position).logNum.equals(pre.getString("logNum","zh"))){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryPerActivity.this)
                                    .setTitle("是否切换到该用户？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle=new Bundle();
                                            bundle.putSerializable("login",myLogins.get(position));
                                            Intent intent = new Intent(HistoryPerActivity.this, LoginActivity.class);
                                            intent.putExtras(bundle);
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
                        }else{
                            Toast.makeText(HistoryPerActivity.this,"已登录",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return convertView;
            }
        };
    lvHis.setAdapter(ba);
        lvHis.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(!myLogins.get(position).logNum.equals(pre.getString("logNum","zh"))){
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryPerActivity.this)
                            .setTitle("是否删除该用户？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        dbLogin.delete(myLogins.get(position));
                                        myLogins.remove(position);
                                        ba.notifyDataSetChanged();
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
                }else{
                    Toast.makeText(HistoryPerActivity.this,"登录中",Toast.LENGTH_SHORT).show();
                }
                return true;
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
