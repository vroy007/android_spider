package com.scut.spider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.scut.spider.adapter.ExpandListViewAdapter;
import com.scut.spider.model.Constant;
import com.scut.spider.model.ContentModel;
import com.scut.spider.model.TeacherModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private Context context;

    private Button btnLeft = null;
    private TextView tvTitle = null;
    private DrawerLayout lDrawer = null;
    private ExpandableListView lvTree = null;
    private TextView tvContentTitle = null;
    private TextView tvContentTime = null;
    private ImageView imgContentTime = null;
    private TextView tvContent = null;

    private boolean isDrawerOpen = false;
    private long exitTime = 0;
    private ProgressDialog dialog;

    private ExpandListViewAdapter expandAdapter = null;
    private List<List<TeacherModel>> expandList = null;
    private List<TeacherModel> doctorList = null;
    private List<TeacherModel> masterList = null;
    private String httpUrl = null;
    private RequestQueue mQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        EventBus.getDefault().register(this);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);  // 退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btn_left);
        tvTitle = (TextView) findViewById(R.id.tv_toptitle);
        lDrawer = (DrawerLayout) findViewById(R.id.lDrawer);
        lvTree = (ExpandableListView) findViewById(R.id.lvTree);
        tvContentTitle = (TextView) findViewById(R.id.tv_content_title);
        tvContentTitle.setText("Welcome~");
        tvContentTime = (TextView) findViewById(R.id.tv_content_time);
        imgContentTime = (ImageView) findViewById(R.id.img_content_time);
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    private void initData() {
        expandList = new ArrayList<>();
        doctorList = new ArrayList<>();
        masterList = new ArrayList<>();
        expandAdapter = new ExpandListViewAdapter(context, lvTree, expandList);
        lvTree.setAdapter(expandAdapter);
        //init volley
        mQueue = Volley.newRequestQueue(context);
    }

    private void initEvent() {
        btnLeft.setOnClickListener(this);
        lDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
                //刷新，爬虫获取列表内容
                refreshForList();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLeft) {
            if (isDrawerOpen)
                lDrawer.closeDrawer(Gravity.LEFT);
            else
                lDrawer.openDrawer(Gravity.LEFT);
        }
    }

    /*
        处理回调，更新UI
         */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0) {
                //来自导师列表的刷新
                expandAdapter.setList(expandList);
                expandAdapter.notifyDataSetChanged();
            } else if (msg.arg1 == 1) {
                //来自导师正文内容的刷新
                ContentModel model = (ContentModel) msg.obj;
                if (model != null) {
                    tvContentTitle.setText(model.getTitle());
                    tvContentTime.setText(model.getTime());
                    tvContent.setText(model.getContent());
                    ImageRequest request = new ImageRequest(
                            Constant.SCUT_EE_URL + model.getUrl(),
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    imgContentTime.setImageBitmap(response);
                                }
                            }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    imgContentTime.setImageBitmap(null);
                                    Log.e(TAG, error.toString());
                                }
                            });
                    mQueue.add(request);
                }
            }
            dialog.dismiss();
        }
    };

    /*
    EventBus回调
     */
    public void onEventMainThread(TeacherModel model) {
        if (model != null) {
            httpUrl = model.getUrl();
            lDrawer.closeDrawer(Gravity.LEFT);
            //刷新爬虫获取信息
            refreshForContent();
        }
    }

    // 刷新导师列表
    private void refreshForList() {
        if(Tools.isNetworkAvailable(MainActivity.this)) {
            // 显示“正在刷新”窗口
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在刷新...");
            dialog.setCancelable(false);
            dialog.show();

            // 重新抓取
            expandList.clear();
            doctorList.clear();
            masterList.clear();
            new Thread(runnableForList).start();  // 子线程
        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
                    .setTitle("刷新")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshForList();
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    // 刷新导师正文内容
    private void refreshForContent() {
        if(Tools.isNetworkAvailable(MainActivity.this)) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在获取...");
            dialog.setCancelable(false);
            dialog.show();

            // 重新抓取
            new Thread(runnableForContent).start();  // 子线程
        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
                    .setTitle("刷新")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshForList();
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    /*
    获取导师列表的子线程
     */
    Runnable runnableForList = new Runnable() {
        @Override
        public void run() {
            Connection conn = null;
            Document doc = null;
            Elements elements = null;
            List<String> urlList = Constant.getUrlList();

            for (int i = 0; i < urlList.size(); i++) {
                conn = Jsoup.connect(urlList.get(i));
                // 修改http包中的header,伪装成浏览器进行抓取
                conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
                doc = null;
                try {
                    doc = conn.timeout(5000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                elements = doc.select("[height=\"15.0\"]");
                if (i == 0) {
                    for (Element element : elements) {
                        String url = element.getElementsByTag("a").first().attr("href");
                        if (!url.contains("http")) {
                            url = Constant.SCUT_EE_URL + url;
                        }
                        String name = element.getElementsByTag("font").text();
                        //System.out.println(name + ":::" + url);
                        TeacherModel model = new TeacherModel(name, url);
                        doctorList.add(model);
                    }
                } else {
                    for (Element element : elements) {
                        String url = element.getElementsByTag("a").first().attr("href");
                        if (!url.contains("http")) {
                            url = Constant.SCUT_EE_URL + url;
                        }
                        String name = element.getElementsByTag("font").text();
                        //System.out.println(name + ":::" + url);
                        TeacherModel model = new TeacherModel(name, url);
                        masterList.add(model);
                    }
                }
            }

            expandList.add(doctorList);
            expandList.add(masterList);

            // 执行完毕后给handler发送消息
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
        }
    };

    /*
    获取导师正文内容的子线程
     */
    Runnable runnableForContent = new Runnable() {
        @Override
        public void run() {

            Connection conn = Jsoup.connect(httpUrl);
            Document doc = null;
            ContentModel model = new ContentModel();
            try {
                doc = conn.timeout(5000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements elements = null;

            if (doc != null) {
                //访问正常时
                //content title
                elements = doc.select("[class=\"biaoti3\"]");
                for (Element element : elements) {
                    model.setTitle(element.text());
                }
                //content time
                elements = doc.select("[height=\"28\"]");
                for (Element element : elements) {
                    model.setTime(element.getElementsByTag("span").text());
                    model.setUrl(element.getElementsByTag("img").attr("src"));
                }
                //content
                elements = doc.select("[class=\"content\"]");
                model.setContent(elements.get(0).text());
            } else {
                //访问失效
                model.setTitle("404");
            }

            // 执行完毕后给handler发送消息
            Message msg = Message.obtain();
            msg.arg1 = 1;
            msg.obj = model;
            handler.sendMessage(msg);
        }
    };
}