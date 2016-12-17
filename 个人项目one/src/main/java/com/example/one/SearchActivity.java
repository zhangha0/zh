package com.example.one;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends Activity {
    TextView tvSearchBack;
    TextView tvSearchClean;
    TextView tvSearchStar;
    EditText etSearch;
    BaseAdapter ba;
    RequestParams params;
    ListView searchLV;
    ImageOptions imageOptions;
    ProgressBar progress_bar;
    TextView tvSerInfo;
    ArrayList<Contentlist> content = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                tvSearchBack.setVisibility(View.VISIBLE);
//                tvSearchBack.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        etSearch.setText("");
//                        v.setVisibility(View.GONE);
//                    }
//                });
                if(content.size()>0){
                    progress_bar.setVisibility(View.GONE);
                    changed();
                }else {
                    progress_bar.setVisibility(View.GONE);
                    tvSerInfo.setText("没有相关公共号");
                    tvSerInfo.setVisibility(View.VISIBLE);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageOptions = new ImageOptions.Builder().setFadeIn(true).setCircular(true).build();
        params = new RequestParams("http://route.showapi.com/582-4");
//        params.addHeader("apikey", "d9d0f2c9c61e041318faaf6d16ce33f6");
//        params.addQueryStringParameter("type1_id","44");
//        params.addQueryStringParameter("type2_id","66");
//        params.addQueryStringParameter("page","1");
        params.addQueryStringParameter("showapi_appid", "28939");
        params.addQueryStringParameter("showapi_sign", "692aac48909742659775da641ee6db38");

        searchLV = (ListView) findViewById(R.id.searchLV);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        tvSearchBack = (TextView) findViewById(R.id.tvSearchBack);
        tvSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSerInfo= (TextView) findViewById(R.id.tvSerInfo);
        tvSearchClean = (TextView) findViewById(R.id.tvSearchClean);
        tvSearchStar = (TextView) findViewById(R.id.tvSearchStar);
        etSearch = (EditText) findViewById(R.id.etSearch);
        tvSearchStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearch.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    content.removeAll(content);
                    searchLV.setAdapter(null);
                    tvSerInfo.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.VISIBLE);
                    String keyworld = etSearch.getText().toString();
                    params.addQueryStringParameter("keyword", keyworld);
                    Log.e("onSuccess: ", "keyworld=" + keyworld);
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gg = new Gson();
                            SearchContent sc = gg.fromJson(result, SearchContent.class);
                            content = sc.showapi_res_body.pagebean.contentlist;
                            Log.e("onSuccess: ", "content=" + content.size());
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            progress_bar.setVisibility(View.GONE);
                            tvSerInfo.setText("网络连接失败...");
                            tvSerInfo.setVisibility(View.VISIBLE);
                            Log.e("onSuccess: ", "shibai");
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });

                }
            }
        });

    }

    public void changed() {
        ba = new BaseAdapter() {
            @Override
            public int getCount() {
                return content.size();
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
                convertView = getLayoutInflater().inflate(R.layout.layout_search, null);
                ImageView iv = (ImageView) convertView.findViewById(R.id.imSearched);
                x.image().bind(iv, content.get(position).userLogo, imageOptions);
                TextView tv1 = (TextView) convertView.findViewById(R.id.tvSearch_UP);
                tv1.setText(content.get(position).pubNum);
                TextView tv2 = (TextView) convertView.findViewById(R.id.tvSearch_DOWN);
                tv2.setText(content.get(position).weiNum);
                TextView tv3 = (TextView) convertView.findViewById(R.id.tvMa);
                tv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView im = new ImageView(SearchActivity.this);
                        x.image().bind(im, content.get(position).code2img);
                        PopupWindow mPopupWindow = new PopupWindow(im, 600, 600, true);
                        mPopupWindow.setTouchable(true);
                        mPopupWindow.setOutsideTouchable(true);
                        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                        mPopupWindow.showAtLocation(findViewById(R.id.llSearchTitle), Gravity.CENTER, 0, 0);
                        mPopupWindow.showAsDropDown(v);
                        backgroundAlpha(SearchActivity.this, 0.3f);
                        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                backgroundAlpha(SearchActivity.this, 1f);
                            }
                        });
                    }
                });
                return convertView;
            }
        };
        searchLV.setAdapter(ba);
    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
