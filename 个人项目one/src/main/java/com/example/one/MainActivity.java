package com.example.one;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

import static org.xutils.common.util.DensityUtil.dip2px;

public class MainActivity extends Activity implements View.OnClickListener {
    TextView tvMore;
    ProgressBar pbMore;
    Intent serIntent;
    List<MySave> showSave;
    LinearLayout llTitle;
    LinearLayout llLeft;
    ArticleList articleList;
    LinearLayout llTypes;
    DbManager db;
    DbManager dbSave;
    PtrFrameLayout ptr;
    TextView tvTypes;
    TextView tvMenu;
    DrawerLayout drawerMenu;
    TextView tvCollect;
    TextView tvSetting;
    TextView tvSearch;
    TextView tb;
    TextView tvLast;
    ImageView ivLoad;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    String typeName;
    ImageOptions options;
    String login;
    int colors[] = {0xffca0101, 0xff9e6502, 0xff0697f7, 0xff02949c, 0xff71fa62,
            0xfffdfd55, 0xffc14aec, 0xfff5589f, 0xff1bd45c, 0xffdfa473
            , 0xffea7024, 0xff4b9190, 0xffd7ed10, 0xffef82bc, 0xfff49107};
    //    int colors[]={R.color.c1,R.color.c2,R.color.c3,R.color.c4,R.color.c5,
//        R.color.c6,R.color.c7,R.color.c8,R.color.c9,R.color.c10,
//        R.color.c11,R.color.c12,R.color.c13,R.color.c14,R.color.c15};
    int color;
    boolean isShua = false;
    boolean isNet = false;
    ArrayList<String> collectId = new ArrayList<>();
    String[] titleNames = new String[]{"热点", "推荐", "段子手", "养生堂", "私房话", "八卦精", "爱生活",
                                        "财经迷","汽车迷","科技咖"};
    ArrayList<String> urlList = new ArrayList<>();
    ArrayList<Content> conList = new ArrayList<>();
    HashMap<String,String> typeMap=new HashMap<>();
    int pageNum = 1;
    int typeNum = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                LayoutInflater li = getLayoutInflater().from(MainActivity.this);
                for (int i = 0; i < showSave.size(); i++) {
                    View view = li.inflate(R.layout.layout, null);
                    MyImage iv = (MyImage) view.findViewById(R.id.ivTitle);
                    MyImage miCollect = (MyImage) view.findViewById(R.id.miCollect);
                    if (collectId.contains(showSave.get(i).imgId)) {
                        miCollect.flag = true;
                        miCollect.setBackgroundResource(R.drawable.ic_star_black_24dp);
                    }
                    miCollect.index = typeNum;
                    miCollect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((MyImage) v).flag) {
                                ((MyImage) v).setBackgroundResource(R.drawable.ic_star_black_24dp);
                                ((MyImage) v).flag = true;
                                try {
                                    String im = showSave.get(((MyImage) v).index).image;
                                    String title = showSave.get(((MyImage) v).index).title;
                                    String url = showSave.get(((MyImage) v).index).url;
                                    String imgeId = showSave.get(((MyImage) v).index).imgId;
                                    db.save(new MyCollect(im, title, url, imgeId));
                                    collectId.add(imgeId);
                                } catch (DbException e) {
                                    Log.e("zzz: ", "收藏出错");
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                            } else {
                                ((MyImage) v).setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                                ((MyImage) v).flag = false;
                                try {
                                    MyCollect mc = db.selector(MyCollect.class).where("imgId", "=", showSave.get(((MyImage) v).index).imgId).findFirst();
                                    db.delete(mc);
                                    collectId.remove(showSave.get(((MyImage) v).index).imgId);
                                } catch (Exception e) {
                                    Log.e("zzz: ", "删除出错");
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    iv.index = typeNum;
                    urlList.add(showSave.get(i).url);
                    MyTextView tv = (MyTextView) view.findViewById(R.id.tvTitle);
                    tv.index = typeNum;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyCollect shareMsg = new MyCollect(showSave.get(((MyTextView) v).index).image,
                                    showSave.get(((MyTextView) v).index).title,
                                    showSave.get(((MyTextView) v).index).url
                                    , showSave.get(((MyTextView) v).index).imgId);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("msg", shareMsg);
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            intent.putExtras(bundle);
                            ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                            ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());

                        }
                    });
                    x.image().bind(iv, showSave.get(i).image);
                    tv.setText(showSave.get(i).title);

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyCollect shareMsg = new MyCollect(showSave.get(((MyImage) v).index).image,
                                    showSave.get(((MyImage) v).index).title,
                                    showSave.get(((MyImage) v).index).url
                                    , showSave.get(((MyImage) v).index).imgId);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("msg", shareMsg);
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            intent.putExtras(bundle);
                            ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                            ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());

                        }
                    });
                    typeNum++;
                    llTypes.addView(view);
                }
                llTypes.addView(tvMore);
                pbMore.setVisibility(View.GONE);
                llTypes.addView(pbMore);
                tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo info = cm.getActiveNetworkInfo();
                        if (info != null) {
                            isNet = info.isAvailable();
                        } else {
                            isNet = false;
                        }
                        if (isNet) {
//                            pbMore.setVisibility(View.VISIBLE);
                            llTypes.removeView(tvMore);
                            urlList.removeAll(urlList);
                            conList.removeAll(conList);
                            pageNum = 1;
                            typeNum = 0;
                            llTypes.removeAllViews();
                            pbMore.setVisibility(View.VISIBLE);
                            llTypes.addView(pbMore);
                            funNet(typeName);
                        } else {
                            Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                Log.e("zzz: ", "用缓存，执行完");
            }
            if (msg.what == 2) {
                try {
//                    llTypes.removeView(tvMore);
                    llTypes.removeView(pbMore);
                } catch (Exception e) {
                }
                if (articleList != null) {
                    ArrayList<MySave> saves = new ArrayList<>();
                    LayoutInflater li = getLayoutInflater().from(MainActivity.this);
                    conList.addAll(articleList.showapi_res_body.pagebean.contentlist);
                    for (int i = 0; i < articleList.showapi_res_body.pagebean.contentlist.size(); i++) {

                        View view = li.inflate(R.layout.layout, null);
                        MyImage iv = (MyImage) view.findViewById(R.id.ivTitle);
                        MyImage miCollect = (MyImage) view.findViewById(R.id.miCollect);
                        miCollect.index = typeNum;
                        miCollect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!((MyImage) v).flag) {
                                    ((MyImage) v).setBackgroundResource(R.drawable.ic_star_black_24dp);
                                    ((MyImage) v).flag = true;
                                    try {
                                        String im = conList.get(((MyImage) v).index).contentImg;
                                        String title = conList.get(((MyImage) v).index).title;
                                        String url = conList.get(((MyImage) v).index).url;
                                        String imgeId = conList.get(((MyImage) v).index).id;
                                        db.save(new MyCollect(im, title, url, imgeId));
                                        collectId.add(imgeId);
                                    } catch (DbException e) {
                                        Log.e("zzz: ", "收藏出错");
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(MainActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                                } else {
                                    ((MyImage) v).setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                                    ((MyImage) v).flag = false;
                                    try {
                                        MyCollect mc = db.selector(MyCollect.class).where("imgId", "=", conList.get(((MyImage) v).index).id).findFirst();
                                        db.delete(mc);
                                        collectId.remove(conList.get(((MyImage) v).index).id);
                                    } catch (Exception e) {
                                        Log.e("zzz: ", "删除出错");
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(MainActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        iv.index = typeNum;

                        String url = articleList.showapi_res_body.pagebean.contentlist.get(i).url;
                        String img = articleList.showapi_res_body.pagebean.contentlist.get(i).contentImg;
                        String title = articleList.showapi_res_body.pagebean.contentlist.get(i).title;
                        String imdId = articleList.showapi_res_body.pagebean.contentlist.get(i).id;
                        if (collectId.contains(imdId)) {
                            miCollect.flag = true;
                            miCollect.setBackgroundResource(R.drawable.ic_star_black_24dp);
                        }
                        saves.add(new MySave(img, title, url, imdId, typeName));
                        urlList.add(url);
                        MyTextView tv = (MyTextView) view.findViewById(R.id.tvTitle);
                        tv.index = typeNum;
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyCollect shareMsg = new MyCollect(conList.get(((MyTextView) v).index).contentImg,
                                        conList.get(((MyTextView) v).index).title,
                                        conList.get(((MyTextView) v).index).url
                                        , conList.get(((MyTextView) v).index).id);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("msg", shareMsg);
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                intent.putExtras(bundle);
                                ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                                ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());

                            }
                        });
                        x.image().bind(iv, img);
                        tv.setText(title);

                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyCollect shareMsg = new MyCollect(conList.get(((MyImage) v).index).contentImg,
                                        conList.get(((MyImage) v).index).title,
                                        conList.get(((MyImage) v).index).url
                                        , conList.get(((MyImage) v).index).id);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("msg", shareMsg);
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                intent.putExtras(bundle);
                                ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                                ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());

                            }
                        });
                        typeNum++;
                        if (isShua) {
                            isShua = false;
                            llTypes.removeAllViews();
                            ptr.refreshComplete();
                        }
                        llTypes.addView(view);
                    }
                    llTypes.addView(tvMore);
                    pbMore.setVisibility(View.GONE);
                    llTypes.addView(pbMore);
                    tvMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pbMore.setVisibility(View.VISIBLE);
                            llTypes.removeView(tvMore);
                            pageNum++;
                            funNet(typeName);
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sa", saves);
                    Intent sendIntent = new Intent("flag");
                    sendIntent.putExtras(bundle);
                    sendBroadcast(sendIntent);
                }
            }
            if (msg.what == 3) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    isNet = info.isAvailable();
                } else {
                    isNet = false;
                }
                if (isNet) {
                    try {
                        List<MySave> msD = dbSave.selector(MySave.class).where("type", "=", typeName).findAll();
                        dbSave.delete(msD);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    typeNum = 0;
                    pageNum = 1;
                    urlList.removeAll(urlList);
                    conList.removeAll(conList);
                    try {
                        collectId.removeAll(collectId);
                        List<MyCollect> myCollects = db.selector(MyCollect.class).findAll();
                        for (int i = 0; i < myCollects.size(); i++) {
                            collectId.add(myCollects.get(i).imgId);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    funNet(typeName);
                    isShua = true;
                } else {
                    ptr.refreshComplete();
                    Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }
            }
            if (msg.what == 4) {
                TextView tvLoginName = (TextView) findViewById(R.id.tvLoginName);
                if (login.equals("yes")) {
                    String s = pre.getString("tou", "noname2");
                    x.image().bind(ivLoad, "assets://" + s + ".jpg", options);
                    tvLoginName.setText(pre.getString("myName", "张三"));
                } else {
                    x.image().bind(ivLoad, "assets://noname2.jpg", options);
                    tvLoginName.setText("未登录");
                }
            }
            if (msg.what == 5) {
                color = colors[Integer.valueOf(pre.getString("color", "0"))];
                findViewById(R.id.rlCenTitle).setBackgroundColor(color);
                findViewById(R.id.llLeftTitle).setBackgroundColor(color);
                if (tvLast != null) {
                    tvLast.setTextColor(color);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        llTypes = (LinearLayout) findViewById(R.id.llTypes);
        llTitle = new LinearLayout(this);
        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        ptr = (PtrFrameLayout) findViewById(R.id.ptr);
        StoreHouseHeader storeHouseHeader = new StoreHouseHeader(this);
        storeHouseHeader.initWithString("Loading...");
        storeHouseHeader.setBackgroundColor(Color.BLACK);
        storeHouseHeader.setTextColor(Color.WHITE);
        for (int i = 0; i < titleNames.length; i++) {
            typeMap.put(titleNames[i],""+i);
        }

//        storeHouseHeader.setDropHeight(300);
        ptr.setHeaderView(storeHouseHeader);
        ptr.addPtrUIHandler(storeHouseHeader);
        ptr.setPtrHandler(new PtrDefaultHandler() {
            //在这里进行数据加载
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.sendEmptyMessage(3);
            }
        });
        color = colors[Integer.valueOf(pre.getString("color", "0"))];
        tvCollect = (TextView) findViewById(R.id.tvCollect);
        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLast != null) {
                    tvLast.setTextColor(Color.BLACK);
                    tvLast.setBackgroundColor(Color.WHITE);
                }
                tvLast = (TextView) v;
                tvLast = (TextView) v;
                tvLast.setTextColor(color);
                tvLast.setBackgroundColor(0xffdfdddd);
                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(intent);
            }
        });
        TextView tvColor = (TextView) findViewById(R.id.tvColor);
        tvColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLast != null) {
                    tvLast.setTextColor(Color.BLACK);
                    tvLast.setBackgroundColor(Color.WHITE);
                }
                tvLast = (TextView) v;
                tvLast = (TextView) v;
                tvLast.setTextColor(color);
                tvLast.setBackgroundColor(0xffdfdddd);

                LinearLayout llColor = new LinearLayout(MainActivity.this);
                llColor.setOrientation(LinearLayout.VERTICAL);
                int count = 0;
                for (int i = 0; i < 3; i++) {
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_color, null);
                    CircleTextView tv1 = (CircleTextView) view.findViewById(R.id.ctv1);
                    tv1.num = count;
                    tv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putString("color", ((CircleTextView) v).num + "");
                            editor.commit();
                            handler.sendEmptyMessage(5);
                        }
                    });
                    tv1.setBackgroundColor(colors[count++]);
                    CircleTextView tv2 = (CircleTextView) view.findViewById(R.id.ctv2);
                    tv2.num = count;
                    tv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putString("color", ((CircleTextView) v).num + "");
                            editor.commit();
                            handler.sendEmptyMessage(5);
                        }
                    });
                    tv2.setBackgroundColor(colors[count++]);
                    CircleTextView tv3 = (CircleTextView) view.findViewById(R.id.ctv3);
                    tv3.num = count;
                    tv3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putString("color", ((CircleTextView) v).num + "");
                            editor.commit();
                            handler.sendEmptyMessage(5);
                        }
                    });
                    tv3.setBackgroundColor(colors[count++]);
                    CircleTextView tv4 = (CircleTextView) view.findViewById(R.id.ctv4);
                    tv4.num = count;
                    tv4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putString("color", ((CircleTextView) v).num + "");
                            editor.commit();
                            handler.sendEmptyMessage(5);
                        }
                    });
                    tv4.setBackgroundColor(colors[count++]);
                    CircleTextView tv5 = (CircleTextView) view.findViewById(R.id.ctv5);
                    tv5.num = count;
                    tv5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putString("color", ((CircleTextView) v).num + "");
                            editor.commit();
                            handler.sendEmptyMessage(5);
                        }
                    });
                    tv5.setBackgroundColor(colors[count++]);
                    llColor.addView(view);
                }
                PopupWindow mPopupWindow = new PopupWindow(llColor, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                mPopupWindow.showAsDropDown(v);
                backgroundAlpha(MainActivity.this, 0.6f);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(MainActivity.this, 1f);
                    }
                });
            }
        });
        TextView tvHelp = (TextView) findViewById(R.id.tvHelp);
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLast != null) {
                    tvLast.setTextColor(Color.BLACK);
                    tvLast.setBackgroundColor(Color.WHITE);
                }
                tvLast = (TextView) v;
                tvLast = (TextView) v;
                tvLast.setTextColor(color);
                tvLast.setBackgroundColor(0xffdfdddd);
            }
        });
        tvSetting = (TextView) findViewById(R.id.tvSetting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLast != null) {
                    tvLast.setTextColor(Color.BLACK);
                    tvLast.setBackgroundColor(Color.WHITE);
                }
                tvLast = (TextView) v;
                tvLast = (TextView) v;
                tvLast.setTextColor(color);
                tvLast.setBackgroundColor(0xffdfdddd);
                Intent intent = new Intent(MainActivity.this, RefreshActivity.class);
                startActivity(intent);
            }
        });
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        String text = String.format("  搜索\n查询公共账号");
        int z = text.lastIndexOf("查");
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new AbsoluteSizeSpan(dip2px(15)), 0, z, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan(dip2px(7)), z, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvSearch.setText(style);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvLast != null) {
                    tvLast.setTextColor(Color.BLACK);
                    tvLast.setBackgroundColor(Color.WHITE);
                }
                tvLast = (TextView) v;
                tvLast.setTextColor(color);
                tvLast.setBackgroundColor(0xffdfdddd);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        setpop();
        tvMore = new TextView(MainActivity.this);
        pbMore=new ProgressBar(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-1, 100);
        tvMore.setLayoutParams(llp);
        tvMore.setGravity(Gravity.CENTER);
        tvMore.setText("加载更多...");

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("myCollect.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        db = x.getDb(daoConfig);
        try {
            List<MyCollect> myCollects = db.selector(MyCollect.class).findAll();
            for (int i = 0; i < myCollects.size(); i++) {
                collectId.add(myCollects.get(i).imgId);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        DbManager.DaoConfig daoConfig2 = new DbManager.DaoConfig()
                .setDbName("mySave.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        dbSave = x.getDb(daoConfig2);
        try {
            typeName = pre.getString("type", "篮球");
            showSave = dbSave.selector(MySave.class).where("type", "=", typeName).findAll();
            Log.e("zzz: ", "读到的mySaves=" + showSave.size());
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            Log.e("zzz: ", "读取失败");
            e.printStackTrace();
        }
        serIntent = new Intent(this, SaveService.class);
        startService(serIntent);
    }

    public void funNet(String key) {
        RequestParams params = new RequestParams("http://route.showapi.com/582-2");
//        params.addHeader("apikey", "d9d0f2c9c61e041318faaf6d16ce33f6");
        params.addQueryStringParameter("showapi_appid", "28939");
        params.addQueryStringParameter("showapi_sign", "692aac48909742659775da641ee6db38");

        params.addQueryStringParameter("page", "" + pageNum);
        params.addQueryStringParameter("typeId",typeName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("onSuccess: ", result);
                Gson g = new Gson();
                articleList = g.fromJson(result, ArticleList.class);
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ptr.refreshComplete();
                Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (tb != null) {
            tb.setTextColor(Color.BLACK);
        }
        ((TextView) v).setTextColor(color);
        tb = (TextView) v;
        urlList.removeAll(urlList);
        conList.removeAll(conList);
        pageNum = 1;
        typeNum = 0;
        llTypes.removeAllViews();
        typeName = typeMap.get(((TextView) v).getText().toString());
        editor.putString("type", typeName);
        editor.commit();
        try {
            showSave = dbSave.selector(MySave.class).where("type", "=", typeName).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (showSave != null) {
            handler.sendEmptyMessage(1);
        } else {
            funNet(typeName);
        }
    }

    public void setpop() {
        drawerMenu = (DrawerLayout) findViewById(R.id.drawerMenu);
        llLeft = (LinearLayout) findViewById(R.id.llLeft);
        tvTypes = (TextView) findViewById(R.id.tvTypes);
//        tvTypes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawerMenu.openDrawer(Gravity.RIGHT);
//            }
//        });
        tvMenu = (TextView) findViewById(R.id.tvMenu);
        tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerMenu.openDrawer(Gravity.LEFT);
            }
        });
        options = new ImageOptions.Builder().setFadeIn(true).setCircular(true).build();
        ivLoad = (ImageView) findViewById(R.id.ivLoad);
        x.image().bind(ivLoad, "assets://noname2.jpg", options);
        ivLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (pre.getString("login", "no").equals("no")) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, PersonalActivity.class);
                }
                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(220, -2);
        llp.setMargins(20, 20, 5, 0);
        MyRightMenu myRightMenu = (MyRightMenu) findViewById(R.id.myRightMenu);
        for (int i = 0; i < titleNames.length; i++) {//文章分类按钮的创建
            TextView tvBtn = new TextView(this);
            tvBtn.setText(titleNames[i]);
            tvBtn.setOnClickListener(this);
            tvBtn.setGravity(Gravity.CENTER);
            tvBtn.setBackgroundColor(0xffafb1b3);
            tvBtn.setAlpha(0.9f);
            tvBtn.setTextSize(17);
            tvBtn.setTextColor(Color.BLACK);
            tvBtn.setLayoutParams(llp);
            myRightMenu.addView(tvBtn);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        login = pre.getString("login", "no");
        handler.sendEmptyMessage(4);
        handler.sendEmptyMessage(5);
    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serIntent);
    }
}