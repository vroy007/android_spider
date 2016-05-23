package com.scut.spider.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.scut.spider.R;
import com.scut.spider.Tools;
import com.scut.spider.model.Constant;
import com.scut.spider.model.ContentModel;
import com.scut.spider.model.NewsItemModel;
import com.scut.spider.presenter.DetailsPresenter;
import com.scut.spider.presenter.Impl.DetailsPresenterImpl;
import com.scut.spider.view.DetailsView;

public class DetailsActivity extends Activity implements View.OnClickListener, DetailsView {

    private static final String TAG = "DetailsActivity";
    public static  final String KEY = "CONTENTS";

    private Context context;

    private Button btnLeft = null;
    private TextView tvTitle = null;

    private TextView tvContentTitle = null;
    private TextView tvContentTime = null;
    private ImageView imgContentTime = null;
    private TextView tvContent = null;

    private ProgressDialog dialog;

    //volley
    private RequestQueue mQueue = null;
    private DetailsPresenter detailsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);

        context = getApplicationContext();
        detailsPresenter = new DetailsPresenterImpl(this);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setBackgroundResource(R.drawable.abc_ic_clear_mtrl_alpha);
        tvTitle = (TextView) findViewById(R.id.tv_toptitle);
        tvContentTitle = (TextView) findViewById(R.id.tv_content_title);
        tvContentTime = (TextView) findViewById(R.id.tv_content_time);
        imgContentTime = (ImageView) findViewById(R.id.img_content_time);
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    private void initData() {
        //init volley
        mQueue = Volley.newRequestQueue(context);
        NewsItemModel model = (NewsItemModel) getIntent().getSerializableExtra(KEY);
        if (model != null) {
            tvTitle.setText(model.getName());
            refreshForUpdate(model.getUrl());
        }
    }

    private void refreshForUpdate(final String url) {
        if(Tools.isNetworkAvailable(DetailsActivity.this)) {
            // 显示“正在刷新”窗口
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在刷新...");
            dialog.setCancelable(false);
            dialog.show();

            detailsPresenter.getContents(url);
        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
                    .setTitle("刷新")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshForUpdate(url);
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    private void initEvent() {
        btnLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLeft) {
            finish();
        }
    }

    @Override
    public void onUpdateContent(ContentModel model) {
        Message msg = Message.obtain();
        msg.obj = model;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                ContentModel model = (ContentModel) msg.obj;
                tvContentTitle.setText(model.getTitle());
                tvContentTime.setText(model.getTime());
                tvContent.setText(model.getHtmlContent());
                ImageRequest request = new ImageRequest(
                        Constant.SCUT_EE_URL + model.getCountUrl(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                imgContentTime.setImageBitmap(response);
                            }
                        }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imgContentTime.setImageBitmap(null);
                    }
                });
                mQueue.add(request);
            }
            dialog.dismiss();
        }
    };
}
