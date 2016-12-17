package com.example.one;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@ContentView(R.layout.activity_main2)
public class Main2Activity extends Activity {
    @ViewInject(R.id.tvWebBack)
    TextView tvWedBack;
    @ViewInject(R.id.tvShare)
    TextView tvShare;
    @ViewInject(R.id.tvCollect_W)
    TextView tvCollect_W;
    @ViewInject(R.id.tvTalk)
    TextView tvTalk;
    @ViewInject(R.id.tvFavorite)
    TextView tvFavorite;
    @ViewInject(R.id.psl)
    ParallaxScrollListView psl;
    @ViewInject(R.id.rlWeb)
    RelativeLayout rlWeb;
    WebView web;
    String url;
    SendMessageToWX.Req req;
    private IWXAPI api;
    String APP_ID = "wxfca1ca1add071b46";
    MyCollect shareMsg;
    DbManager db;
    MyCollect m;
    LinearLayout ll;
    MyPopWindow mPopupWindow;
    boolean flag = false;
    SharedPreferences pre;
    SharedPreferences.Editor editor;
    BaseAdapter ba;
    boolean heart=false;
    private IWeiboShareAPI mWeiboShareAPI = null;
    int colors[]={0xffca0101,0xff9e6502,0xff0697f7,0xff02949c,0xff71fa62,
            0xfffdfd55,0xffc14aec,0xfff5589f,0xff1bd45c,0xffdfa473
            ,0xffea7024,0xff4b9190,0xffd7ed10,0xffef82bc,0xfff49107};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "807216949", true);
        mWeiboShareAPI.registerApp();

        pre = getSharedPreferences("zhTypes", MODE_PRIVATE);
        editor = pre.edit();
        rlWeb.setBackgroundColor(colors[Integer.valueOf(pre.getString("color","0"))]);
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shareMsg = (MyCollect) bundle.getSerializable("msg");

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("myCollect.db")
                .setDbDir(new File("/mnt/sdcard/"))
                .setDbVersion(2)
                .setAllowTransaction(true);
        db = x.getDb(daoConfig);
        tvCollect();
        url = shareMsg.url;
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        web.loadUrl(url);
        //设置Web视图
        web.setWebViewClient(new HelloWebViewClient());
        View head = getLayoutInflater().from(this).inflate(R.layout.layout_head, null);
        ImageView imHead = (ImageView) head.findViewById(R.id.imHead);
        x.image().bind(imHead, shareMsg.image);
        psl.addHeaderView(head);
        ba = new BaseAdapter() {
            @Override
            public int getCount() {
                return 1;
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
            public View getView(int position, View convertView, ViewGroup parent) {
                return web;
            }
        };
        psl.setAdapter(ba);

        tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!heart){
                    v.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    heart=true;
                }else {
                    v.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    heart=false;
                }
            }
        });
        funShareBtn();
        tvWedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                x.image().bind(im,content.get(position).code2img);
//                PopupWindow mPopupWindow = new PopupWindow(ll, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
//                mPopupWindow.setTouchable(true);
//                mPopupWindow.setOutsideTouchable(true);
//                mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
//                mPopupWindow.showAsDropDown(v);
                mPopupWindow=new MyPopWindow(ll,Main2Activity.this);
                mPopupWindow.showAtLocation(Main2Activity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                backgroundAlpha(Main2Activity.this, 0.6f);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(Main2Activity.this, 1f);
                    }
                });
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }
    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        } else {
            this.finish();
            return true;
        }

    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ba.notifyDataSetChanged();
        }
    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
    public void funShareBtn(){
        View view= LayoutInflater.from(Main2Activity.this).inflate(R.layout.layout_share,null);
        ll= (LinearLayout) view.findViewById(R.id.llMainShare);
        ImageView im1 = (ImageView) view.findViewById(R.id.ivWei1);
        im1.setBackgroundResource(R.drawable.wei1);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api.registerApp(APP_ID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;

                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = shareMsg.title;
                msg.description = "网页描述";
//                        Bitmap b = netPicToBmp(shareMsg.imgUrl);
//                        msg.thumbData = Util.bmpToByteArray(b, true);

                req = new SendMessageToWX.Req();
                req.transaction = String.valueOf("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
                finish();
            }
        });
        ImageView im2 = (ImageView) view.findViewById(R.id.ivWei2);
        im2.setBackgroundResource(R.drawable.wei2);
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api.registerApp(APP_ID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;

                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = shareMsg.title;
                msg.description = "网页描述";
//                        Bitmap b = netPicToBmp(shareMsg.imgUrl);
//                        msg.thumbData = Util.bmpToByteArray(b, true);

                req = new SendMessageToWX.Req();
                req.transaction = String.valueOf("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
                finish();
            }
        });
        ImageView im3 = (ImageView) view.findViewById(R.id.ivSina);
        im3.setBackgroundResource(R.drawable.weibo);
        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextObject textObject = new TextObject();
                textObject.text = "fengxiang";
                WebpageObject web = new WebpageObject();
                web.title = shareMsg.title;
                web.description = shareMsg.title;
                web.actionUrl = shareMsg.url;
                web.identify = Utility.generateGUID();
                web.defaultText = "Webpage 默认文案";
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noname);
                // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
                web.setThumbImage(bitmap);

                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.mediaObject = web;
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                mWeiboShareAPI.sendRequest(Main2Activity.this, request);
            }
        });
        TextView tvCancle= (TextView) view.findViewById(R.id.tvCancle);
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
    public void tvCollect() {
        try {
            m = db.selector(MyCollect.class).where("imgId", "=", shareMsg.imgId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (m == null) {
            flag = false;
            tvCollect_W.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
        } else {
            tvCollect_W.setBackgroundResource(R.drawable.ic_star_black_24dp);
            flag = true;
        }
        tvCollect_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    try {
                        v.setBackgroundResource(R.drawable.ic_star_black_24dp);
                        db.save(shareMsg);
                        Toast.makeText(Main2Activity.this, "收藏", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    flag = true;
                } else {
                    try {
                        v.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                        MyCollect mc = db.selector(MyCollect.class).where("imgId", "=", shareMsg.imgId).findFirst();
                        db.delete(mc);
                        Toast.makeText(Main2Activity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    flag = false;
                }
            }
        });

    }

    public byte[] bitMapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtil.e("Weibo.ImageObject", "put thumb failed");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap netPicToBmp(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
